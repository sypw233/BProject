package ovo.sypw.bsp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.StateFlow
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.data.paging.PagingLoadState

/**
 * 分页列表状态管理
 * 用于在Compose中管理分页列表的状态
 * @param T 数据类型
 */
class PagingListState<T>(
    private val pagingManager: PagingManager<T>
) {
    
    /**
     * 分页数据流
     */
    val pagingData: StateFlow<PagingData<T>> = pagingManager.pagingData
    
    /**
     * 加载状态流
     */
    val loadState: StateFlow<PagingLoadState> = pagingManager.loadState
    
    /**
     * 刷新数据
     */
    suspend fun refresh() {
        pagingManager.refresh()
    }
    
    /**
     * 重试加载
     */
    suspend fun retry() {
        pagingManager.retry()
    }
    
    /**
     * 加载下一页
     */
    suspend fun loadNextPage() {
        pagingManager.loadNextPage()
    }
    
    /**
     * 检查预加载
     * @param visibleItemIndex 当前可见的最后一个项目索引
     */
    suspend fun checkPreload(visibleItemIndex: Int) {
        pagingManager.checkPreload(visibleItemIndex)
    }
    
    /**
     * 获取指定位置的数据项
     * @param index 索引
     * @return 数据项
     */
    fun getItem(index: Int): T? {
        return pagingManager.getItem(index)
    }
    
    /**
     * 获取当前数据项总数
     */
    fun getItemCount(): Int {
        return pagingManager.getItemCount()
    }
    
    /**
     * 清空数据
     */
    fun clear() {
        pagingManager.clear()
    }
}

/**
 * 创建分页列表状态
 * @param pagingManager 分页管理器
 * @param autoInitialize 是否自动初始化
 * @return 分页列表状态
 */
@Composable
fun <T> rememberPagingListState(
    pagingManager: PagingManager<T>,
    autoInitialize: Boolean = true
): PagingListState<T> {
    val state = remember(pagingManager) {
        PagingListState(pagingManager)
    }
    
    // 自动初始化
    if (autoInitialize) {
        LaunchedEffect(pagingManager) {
            pagingManager.initialize()
        }
    }
    
    return state
}

/**
 * 分页列表项信息
 * @param T 数据类型
 */
data class PagingListItem<T>(
    /**
     * 数据项
     */
    val item: T?,
    
    /**
     * 索引
     */
    val index: Int,
    
    /**
     * 是否为占位符
     */
    val isPlaceholder: Boolean = false
)

/**
 * 分页列表工具函数
 */
object PagingListUtils {
    
    /**
     * 获取分页列表项
     * @param pagingData 分页数据
     * @param index 索引
     * @return 分页列表项
     */
    fun <T> getPagingListItem(
        pagingData: PagingData<T>,
        index: Int
    ): PagingListItem<T> {
        return if (index in pagingData.items.indices) {
            PagingListItem(
                item = pagingData.items[index],
                index = index,
                isPlaceholder = false
            )
        } else {
            PagingListItem(
                item = null,
                index = index,
                isPlaceholder = true
            )
        }
    }
    
    /**
     * 检查是否应该显示加载更多指示器
     * @param pagingData 分页数据
     * @param loadState 加载状态
     * @return 是否显示加载更多指示器
     */
    fun <T> shouldShowLoadMore(
        pagingData: PagingData<T>,
        loadState: PagingLoadState
    ): Boolean {
        return pagingData.hasNextPage && loadState == PagingLoadState.Loading
    }
    
    /**
     * 检查是否应该显示错误重试
     * @param loadState 加载状态
     * @return 是否显示错误重试
     */
    fun shouldShowRetry(loadState: PagingLoadState): Boolean {
        return loadState is PagingLoadState.Error
    }
    
    /**
     * 检查是否应该显示空状态
     * @param pagingData 分页数据
     * @param loadState 加载状态
     * @return 是否显示空状态
     */
    fun <T> shouldShowEmpty(
        pagingData: PagingData<T>,
        loadState: PagingLoadState
    ): Boolean {
        return pagingData.items.isEmpty() && 
               loadState != PagingLoadState.Loading && 
               loadState !is PagingLoadState.Error
    }
    
    /**
     * 获取加载状态文本
     * @param loadState 加载状态
     * @return 状态文本
     */
    fun getLoadStateText(loadState: PagingLoadState): String {
        return when (loadState) {
            is PagingLoadState.Idle -> "空闲"
            is PagingLoadState.Loading -> "加载中..."
            is PagingLoadState.Success -> "加载成功"
            is PagingLoadState.Error -> "加载失败: ${loadState.error}"
        }
    }
    
    /**
     * 格式化分页信息文本
     * @param pagingData 分页数据
     * @return 分页信息文本
     */
    fun <T> formatPagingInfo(pagingData: PagingData<T>): String {
        val pageInfo = PagingUtils.calculatePageInfo(
            totalCount = pagingData.totalCount,
            pageSize = pagingData.pageSize,
            currentPage = pagingData.currentPage
        )
        
        return "第 ${pageInfo.startIndex}-${pageInfo.endIndex} 项，共 ${pageInfo.totalCount} 项"
    }
}