package ovo.sypw.bsp.data.paging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.DepartmentUseCase
import ovo.sypw.bsp.utils.Logger

/**
 * 分页加载状态
 */
sealed class PagingLoadState {
    /**
     * 空闲状态
     */
    object Idle : PagingLoadState()
    
    /**
     * 加载中状态
     */
    object Loading : PagingLoadState()
    
    /**
     * 加载成功状态
     */
    object Success : PagingLoadState()
    
    /**
     * 加载失败状态
     * @param error 错误信息
     */
    data class Error(val error: String) : PagingLoadState()
}

/**
 * 分页数据容器
 * @param T 数据类型
 */
data class PagingData<T>(
    /**
     * 数据列表
     */
    val items: List<T> = emptyList(),
    
    /**
     * 当前页码
     */
    val currentPage: Int = 1,
    
    /**
     * 每页大小
     */
    val pageSize: Int = 10,
    
    /**
     * 总记录数
     */
    val totalCount: Long = 0,
    
    /**
     * 总页数
     */
    val totalPages: Int = 0,
    
    /**
     * 是否有下一页
     */
    val hasNextPage: Boolean = false,
    
    /**
     * 是否有上一页
     */
    val hasPreviousPage: Boolean = false,
    
    /**
     * 加载状态
     */
    val loadState: PagingLoadState = PagingLoadState.Idle
)

/**
 * 部门数据分页源
 * 用于API分页查询的自定义分页实现
 */
class DepartmentPagingSource(
    private val departmentUseCase: DepartmentUseCase,
    private val searchName: String? = null
) {
    
    companion object {
        private const val TAG = "DepartmentPagingSource"
        private const val DEFAULT_PAGE_SIZE = 10
    }
    
    // 分页数据状态
    private val _pagingData = MutableStateFlow(
        PagingData<DepartmentDto>(
            pageSize = DEFAULT_PAGE_SIZE
        )
    )
    val pagingData: StateFlow<PagingData<DepartmentDto>> = _pagingData.asStateFlow()
    
    // 当前加载状态
    private val _loadState = MutableStateFlow<PagingLoadState>(PagingLoadState.Idle)
    val loadState: StateFlow<PagingLoadState> = _loadState.asStateFlow()
    
    /**
     * 加载指定页的数据
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param append 是否追加到现有数据（true为追加，false为替换）
     */
    suspend fun loadPage(
        page: Int = 1,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        append: Boolean = false
    ) {
        try {
            _loadState.value = PagingLoadState.Loading
            
            Logger.d(TAG, "加载部门分页数据: page=$page, size=$pageSize, searchName=$searchName, append=$append")
            
            when (val result = departmentUseCase.getDepartmentPage(
                current = page,
                size = pageSize,
                name = searchName
            )) {
                is NetworkResult.Success -> {
                    val data = result.data
                    Logger.i(TAG, "部门分页数据加载成功: 当前页=${data.current}, 总数=${data.total}")
                    
                    updatePagingData(data, append)
                    _loadState.value = PagingLoadState.Success
                }
                
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门分页数据加载失败: ${result.message}")
                    _loadState.value = PagingLoadState.Error(result.message)
                }
                
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                
                is NetworkResult.Idle -> {
                    _loadState.value = PagingLoadState.Error("空闲状态")
                }
            }
        } catch (exception: Exception) {
            Logger.e(TAG, "部门分页数据加载异常", exception)
            _loadState.value = PagingLoadState.Error(exception.message ?: "未知错误")
        }
    }
    
    /**
     * 加载下一页数据
     */
    suspend fun loadNextPage() {
        val currentData = _pagingData.value
        if (currentData.hasNextPage && _loadState.value != PagingLoadState.Loading) {
            loadPage(
                page = currentData.currentPage + 1,
                pageSize = currentData.pageSize,
                append = true
            )
        }
    }
    
    /**
     * 刷新数据（重新加载第一页）
     */
    suspend fun refresh() {
        loadPage(page = 1, pageSize = _pagingData.value.pageSize, append = false)
    }
    
    /**
     * 重试加载
     */
    suspend fun retry() {
        val currentData = _pagingData.value
        loadPage(
            page = currentData.currentPage,
            pageSize = currentData.pageSize,
            append = false
        )
    }
    
    /**
     * 清空数据
     */
    fun clear() {
        _pagingData.value = PagingData(
            pageSize = _pagingData.value.pageSize
        )
        _loadState.value = PagingLoadState.Idle
    }
    
    /**
     * 更新分页数据
     * @param pageResult API返回的分页结果
     * @param append 是否追加到现有数据
     */
    private fun updatePagingData(pageResult: PageResultDto<DepartmentDto>, append: Boolean) {
        val currentData = _pagingData.value
        
        val newItems = if (append) {
            currentData.items + pageResult.records
        } else {
            pageResult.records
        }
        
        _pagingData.value = PagingData(
            items = newItems,
            currentPage = pageResult.current,
            pageSize = pageResult.size,
            totalCount = pageResult.total,
            totalPages = pageResult.pages,
            hasNextPage = pageResult.current < pageResult.pages,
            hasPreviousPage = pageResult.current > 1,
            loadState = PagingLoadState.Success
        )
    }
}

/**
 * 通用分页工具类
 * 可用于任何类型的API分页查询
 * @param T 数据类型
 */
class GenericPagingSource<T>(
    private val loadData: suspend (page: Int, pageSize: Int) -> NetworkResult<PageResultDto<T>>
) {
    
    companion object {
        private const val TAG = "GenericPagingSource"
        private const val DEFAULT_PAGE_SIZE = 10
    }
    
    // 分页数据状态
    private val _pagingData = MutableStateFlow(
        PagingData<T>(
            pageSize = DEFAULT_PAGE_SIZE
        )
    )
    val pagingData: StateFlow<PagingData<T>> = _pagingData.asStateFlow()
    
    // 当前加载状态
    private val _loadState = MutableStateFlow<PagingLoadState>(PagingLoadState.Idle)
    val loadState: StateFlow<PagingLoadState> = _loadState.asStateFlow()
    
    /**
     * 加载指定页的数据
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param append 是否追加到现有数据（true为追加，false为替换）
     */
    suspend fun loadPage(
        page: Int = 1,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        append: Boolean = false
    ) {
        try {
            _loadState.value = PagingLoadState.Loading
            
            Logger.d(TAG, "加载分页数据: page=$page, size=$pageSize, append=$append")
            
            when (val result = loadData(page, pageSize)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    Logger.i(TAG, "分页数据加载成功: 当前页=${data.current}, 总数=${data.total}")
                    
                    updatePagingData(data, append)
                    _loadState.value = PagingLoadState.Success
                }
                
                is NetworkResult.Error -> {
                    Logger.e(TAG, "分页数据加载失败: ${result.message}")
                    _loadState.value = PagingLoadState.Error(result.message)
                }
                
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                
                is NetworkResult.Idle -> {
                    _loadState.value = PagingLoadState.Error("空闲状态")
                }
            }
        } catch (exception: Exception) {
            Logger.e(TAG, "分页数据加载异常", exception)
            _loadState.value = PagingLoadState.Error(exception.message ?: "未知错误")
        }
    }
    
    /**
     * 加载下一页数据
     */
    suspend fun loadNextPage() {
        val currentData = _pagingData.value
        if (currentData.hasNextPage && _loadState.value != PagingLoadState.Loading) {
            loadPage(
                page = currentData.currentPage + 1,
                pageSize = currentData.pageSize,
                append = true
            )
        }
    }
    
    /**
     * 刷新数据（重新加载第一页）
     */
    suspend fun refresh() {
        loadPage(page = 1, pageSize = _pagingData.value.pageSize, append = false)
    }
    
    /**
     * 重试加载
     */
    suspend fun retry() {
        val currentData = _pagingData.value
        loadPage(
            page = currentData.currentPage,
            pageSize = currentData.pageSize,
            append = false
        )
    }
    
    /**
     * 清空数据
     */
    fun clear() {
        _pagingData.value = PagingData(
            pageSize = _pagingData.value.pageSize
        )
        _loadState.value = PagingLoadState.Idle
    }
    
    /**
     * 更新分页数据
     * @param pageResult API返回的分页结果
     * @param append 是否追加到现有数据
     */
    private fun updatePagingData(pageResult: PageResultDto<T>, append: Boolean) {
        val currentData = _pagingData.value
        
        val newItems = if (append) {
            currentData.items + pageResult.records
        } else {
            pageResult.records
        }
        
        _pagingData.value = PagingData(
            items = newItems,
            currentPage = pageResult.current,
            pageSize = pageResult.size,
            totalCount = pageResult.total,
            totalPages = pageResult.pages,
            hasNextPage = pageResult.current < pageResult.pages,
            hasPreviousPage = pageResult.current > 1,
            loadState = PagingLoadState.Success
        )
    }
}