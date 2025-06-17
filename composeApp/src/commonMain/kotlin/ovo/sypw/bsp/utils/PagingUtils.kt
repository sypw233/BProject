package ovo.sypw.bsp.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.data.paging.PagingLoadState
import ovo.sypw.bsp.data.dto.result.NetworkResult

/**
 * 分页配置
 */
data class PagingConfig(
    /**
     * 默认页面大小
     */
    val pageSize: Int = 9,
    
    /**
     * 初始加载大小（通常是pageSize的倍数）
     */
    val initialLoadSize: Int = pageSize * 3,
    
    /**
     * 预加载距离（距离列表末尾多少项时开始预加载）
     */
    val prefetchDistance: Int = pageSize / 2,
    
    /**
     * 是否启用占位符
     */
    val enablePlaceholders: Boolean = false
)

/**
 * 分页工厂接口
 * @param T 数据类型
 */
fun interface PagingSourceFactory<T> {
    /**
     * 创建分页数据加载函数
     * @return 分页数据加载函数
     */
    suspend fun loadPage(page: Int, pageSize: Int): NetworkResult<PageResultDto<T>>
}

/**
 * 通用分页管理器
 * 提供完整的分页功能，包括预加载、缓存等
 * @param T 数据类型
 */
class PagingManager<T>(
    private val config: PagingConfig = PagingConfig(),
    private val sourceFactory: PagingSourceFactory<T>
) {
    
    companion object {
        private const val TAG = "PagingManager"
    }
    
    // 分页数据状态
    private val _pagingData = MutableStateFlow(
        PagingData<T>(
            pageSize = config.pageSize
        )
    )
    val pagingData: StateFlow<PagingData<T>> = _pagingData.asStateFlow()
    
    // 当前加载状态
    private val _loadState = MutableStateFlow<PagingLoadState>(PagingLoadState.Idle)
    val loadState: StateFlow<PagingLoadState> = _loadState.asStateFlow()
    
    // 是否正在加载
    private var isLoading = false
    
    /**
     * 初始化加载
     */
    suspend fun initialize() {
        if (_pagingData.value.items.isEmpty()) {
            loadPage(page = 1, pageSize = config.initialLoadSize, append = false)
        }
    }
    
    /**
     * 加载指定页的数据
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @param append 是否追加到现有数据
     */
    suspend fun loadPage(
        page: Int = 1,
        pageSize: Int = config.pageSize,
        append: Boolean = false
    ) {
        if (isLoading) {
            Logger.d(TAG, "正在加载中，跳过重复请求")
            return
        }
        
        try {
            isLoading = true
            _loadState.value = PagingLoadState.Loading
            
            Logger.d(TAG, "加载分页数据: page=$page, size=$pageSize, append=$append")
            
            when (val result = sourceFactory.loadPage(page, pageSize)) {
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
        } finally {
            isLoading = false
        }
    }
    
    /**
     * 加载下一页数据
     */
    suspend fun loadNextPage() {
        val currentData = _pagingData.value
        if (currentData.hasNextPage && !isLoading) {
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
        loadPage(page = 1, pageSize = config.initialLoadSize, append = false)
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
            pageSize = config.pageSize
        )
        _loadState.value = PagingLoadState.Idle
    }
    
    /**
     * 检查是否需要预加载
     * @param visibleItemIndex 当前可见的最后一个项目索引
     */
    suspend fun checkPreload(visibleItemIndex: Int) {
        val currentData = _pagingData.value
        val shouldPreload = visibleItemIndex >= currentData.items.size - config.prefetchDistance
        
        if (shouldPreload && currentData.hasNextPage && !isLoading) {
            Logger.d(TAG, "触发预加载: visibleIndex=$visibleItemIndex, totalItems=${currentData.items.size}")
            loadNextPage()
        }
    }
    
    /**
     * 获取指定位置的数据项
     * @param index 索引
     * @return 数据项，如果索引超出范围则返回null
     */
    fun getItem(index: Int): T? {
        val items = _pagingData.value.items
        return if (index in items.indices) items[index] else null
    }
    
    /**
     * 获取当前数据项总数
     */
    fun getItemCount(): Int = _pagingData.value.items.size
    
    /**
     * 更新分页数据
     * @param pageResult API返回的分页结果
     * @param append 是否追加到现有数据
     */
    private fun updatePagingData(pageResult: PageResultDto<T>, append: Boolean) {
        val currentData = _pagingData.value
        
        val newItems = if (append) {
            // 去重处理（基于索引位置）
            val existingItems = currentData.items
            val newRecords = pageResult.records
            existingItems + newRecords
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
 * 分页工具类扩展函数
 */
object PagingUtils {
    
    /**
     * 创建部门分页管理器
     * @param loadData 数据加载函数
     * @param config 分页配置
     * @return 分页管理器
     */
    fun <T> createPagingManager(
        loadData: suspend (page: Int, pageSize: Int) -> NetworkResult<PageResultDto<T>>,
        config: PagingConfig = PagingConfig()
    ): PagingManager<T> {
        return PagingManager(
            config = config,
            sourceFactory = PagingSourceFactory { page, pageSize ->
                loadData(page, pageSize)
            }
        )
    }
    
    /**
     * 计算页码信息
     * @param totalCount 总记录数
     * @param pageSize 每页大小
     * @param currentPage 当前页码
     * @return 页码信息
     */
    fun calculatePageInfo(
        totalCount: Long,
        pageSize: Int,
        currentPage: Int
    ): PageInfo {
        val totalPages = if (totalCount == 0L) 0 else ((totalCount - 1) / pageSize + 1).toInt()
        
        return PageInfo(
            currentPage = currentPage,
            totalPages = totalPages,
            totalCount = totalCount,
            pageSize = pageSize,
            hasNextPage = currentPage < totalPages,
            hasPreviousPage = currentPage > 1,
            startIndex = (currentPage - 1) * pageSize + 1,
            endIndex = minOf(currentPage * pageSize, totalCount.toInt())
        )
    }
    
    /**
     * 生成页码列表（用于分页导航）
     * @param currentPage 当前页码
     * @param totalPages 总页数
     * @param maxVisible 最大可见页码数
     * @return 页码列表
     */
    fun generatePageNumbers(
        currentPage: Int,
        totalPages: Int,
        maxVisible: Int = 7
    ): List<Int> {
        if (totalPages <= maxVisible) {
            return (1..totalPages).toList()
        }
        
        val half = maxVisible / 2
        val start = maxOf(1, currentPage - half)
        val end = minOf(totalPages, start + maxVisible - 1)
        
        return (start..end).toList()
    }
}

/**
 * 页码信息数据类
 */
data class PageInfo(
    val currentPage: Int,
    val totalPages: Int,
    val totalCount: Long,
    val pageSize: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val startIndex: Int,
    val endIndex: Int
)