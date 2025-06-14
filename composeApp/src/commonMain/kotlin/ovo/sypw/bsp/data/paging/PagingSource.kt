package ovo.sypw.bsp.data.paging

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
    val pageSize: Int = 5,
    
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

