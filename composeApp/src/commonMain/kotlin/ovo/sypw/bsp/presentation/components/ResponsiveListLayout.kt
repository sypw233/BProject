package ovo.sypw.bsp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 响应式列表布局组件
 * 根据屏幕尺寸自动选择列表或网格布局
 * @param items 数据列表
 * @param isLoading 是否正在加载
 * @param emptyMessage 空状态提示信息
 * @param onLoadData 加载数据回调
 * @param layoutConfig 响应式布局配置
 * @param itemContent 列表项内容组件
 */
@Composable
fun <T> ResponsiveListLayout(
    items: List<T>,
    isLoading: Boolean,
    emptyMessage: String,
    onLoadData: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    Box(modifier = modifier) {
        when {
            items.isEmpty() && !isLoading -> {
                // 空状态
                EmptyStateCard(
                    message = emptyMessage,
                    onAction = onLoadData,
                    layoutConfig = layoutConfig
                )
            }
            layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT -> {
                // 紧凑型：垂直列表
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
                ) {
                    items(items) { item ->
                        itemContent(item)
                    }
                }
            }
            else -> {
                // 中等型和扩展型：网格布局
                LazyVerticalGrid(
                    columns = GridCells.Fixed(layoutConfig.columnCount),
                    horizontalArrangement = Arrangement.spacedBy(layoutConfig.horizontalSpacing),
                    verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
                ) {
                    items(items) { item ->
                        itemContent(item)
                    }
                }
            }
        }
    }
}

/**
 * 带分页的响应式列表布局组件
 * @param items 数据列表
 * @param isLoading 是否正在加载
 * @param emptyMessage 空状态提示信息
 * @param onLoadData 加载数据回调
 * @param pageInfo 分页信息
 * @param onPageChange 页码变更回调
 * @param onPageSizeChange 页面大小变更回调
 * @param layoutConfig 响应式布局配置
 * @param itemContent 列表项内容组件
 */
@Composable
fun <T> ResponsiveListLayoutWithPagination(
    items: List<T>,
    isLoading: Boolean,
    emptyMessage: String,
    onLoadData: () -> Unit,
    pageInfo: ovo.sypw.bsp.data.dto.PageResultDto<*>?,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit,
    layoutConfig: ResponsiveLayoutConfig,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
    ) {
        // 列表内容区域
        Box(
            modifier = Modifier.weight(1f)
        ) {
            ResponsiveListLayout(
                items = items,
                isLoading = isLoading,
                emptyMessage = emptyMessage,
                onLoadData = onLoadData,
                layoutConfig = layoutConfig,
                itemContent = itemContent
            )
        }
        
        // 分页组件
        pageInfo?.let {
            PaginationComponent(
                pageInfo = it,
                onPageChange = onPageChange,
                onPageSizeChange = onPageSizeChange,
                layoutConfig = layoutConfig
            )
        }
    }
}