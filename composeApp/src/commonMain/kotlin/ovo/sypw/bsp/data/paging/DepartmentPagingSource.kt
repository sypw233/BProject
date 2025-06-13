package ovo.sypw.bsp.data.paging

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.DepartmentUseCase
import ovo.sypw.bsp.utils.Logger

/**
 * 部门数据分页源
 * 用于Paging库的数据加载
 */
class DepartmentPagingSource(
    private val departmentUseCase: DepartmentUseCase,
    private val searchName: String? = null
) : PagingSource<Int, DepartmentDto>() {
    
    companion object {
        private const val TAG = "DepartmentPagingSource"
        private const val STARTING_PAGE_INDEX = 1
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DepartmentDto> {
        return try {
            val page = params.key ?: STARTING_PAGE_INDEX
            val pageSize = params.loadSize
            
            Logger.d(TAG, "加载部门分页数据: page=$page, size=$pageSize, searchName=$searchName")
            
            when (val result = departmentUseCase.getDepartmentPage(
                current = page,
                size = pageSize,
                name = searchName
            )) {
                is NetworkResult.Success -> {
                    val data = result.data
                    Logger.i(TAG, "部门分页数据加载成功: 当前页=${data.current}, 总数=${data.total}")
                    
                    LoadResult.Page(
                        data = data.records,
                        prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                        nextKey = if (data.current >= data.pages) null else page + 1
                    )
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门分页数据加载失败: ${result.message}")
                    LoadResult.Error(Exception(result.message))
                }
                is NetworkResult.Loading -> {
                    // 这种情况不应该发生在suspend函数中
                    LoadResult.Error(Exception("意外的加载状态"))
                }
                is NetworkResult.Idle -> {
                    LoadResult.Error(Exception("空闲状态"))
                }
            }
        } catch (exception: Exception) {
            Logger.e(TAG, "部门分页数据加载异常", exception)
            LoadResult.Error(exception)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, DepartmentDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}