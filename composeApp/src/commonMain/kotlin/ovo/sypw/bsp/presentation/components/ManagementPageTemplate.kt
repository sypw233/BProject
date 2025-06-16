package ovo.sypw.bsp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 管理页面状态接口
 * 定义管理页面所需的通用状态
 */
interface ManagementPageState<T> {
    val isLoading: Boolean
    val items: List<T>
    val pageInfo: PageResultDto<T>?
    val errorMessage: String?
}

/**
 * 管理页面操作接口
 * 定义管理页面所需的通用操作
 */
interface ManagementPageActions {
    fun refresh()
    fun loadData(current: Int = 1, size: Int = 10)
    fun showAddDialog()
}

/**
 * 通用管理页面模板组件
 * 提供标准的管理页面布局和功能
 * @param state 页面状态
 * @param actions 页面操作
 * @param title 页面标题
 * @param emptyMessage 空状态提示信息
 * @param refreshText 刷新按钮文本
 * @param addText 添加按钮文本
 * @param layoutConfig 响应式布局配置
 * @param itemContent 列表项内容组件
 * @param dialogContent 对话框内容组件
 */
@Composable
fun <T> ManagementPageTemplate(
    state: ManagementPageState<T>,
    actions: ManagementPageActions,
    title: String,
    emptyMessage: String,
    refreshText: String = "刷新数据",
    addText: String = "添加",
    layoutConfig: ResponsiveLayoutConfig,
    itemContent: @Composable (T) -> Unit,
    dialogContent: @Composable () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
        ) {
            // 标题和操作按钮区域（仅在扩展型布局显示）
            if (layoutConfig.screenSize == ResponsiveUtils.ScreenSize.EXPANDED) {
                androidx.compose.material3.Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(layoutConfig.contentPadding)
                )

                ResponsiveActionButtons(
                    onRefresh = actions::refresh,
                    onAdd = actions::showAddDialog,
                    isLoading = state.isLoading,
                    refreshText = refreshText,
                    addText = addText,
                    title = null,
                    layoutConfig = layoutConfig
                )
            }

            // 加载状态
            LoadingIndicator(isLoading = state.isLoading)

            // 错误信息显示
            ErrorMessageCard(
                errorMessage = state.errorMessage,
                layoutConfig = layoutConfig
            )

            // 列表内容区域（带分页）
            ResponsiveListLayoutWithPagination(
                items = state.items,
                isLoading = state.isLoading,
                emptyMessage = emptyMessage,
                onLoadData = { actions.loadData() },
                pageInfo = state.pageInfo,
                onPageChange = { page ->
                    actions.loadData(
                        current = page,
                        size = state.pageInfo?.size ?: 10
                    )
                },
                onPageSizeChange = { size ->
                    actions.loadData(current = 1, size = size)
                },
                layoutConfig = layoutConfig,
                modifier = Modifier.weight(1f),
                itemContent = itemContent
            )
        }

        // FAB按钮组 - 悬浮在右下角
        if (layoutConfig.screenSize != ResponsiveUtils.ScreenSize.EXPANDED) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 刷新FAB
                FloatingActionButton(
                    onClick = actions::refresh,
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = refreshText
                    )
                }

                // 添加FAB
                FloatingActionButton(
                    onClick = actions::showAddDialog,
                    modifier = Modifier.size(48.dp)

                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = addText
                    )
                }
            }
        }
    }

    // 自动加载数据
    LaunchedEffect(Unit) {
        if (state.items.isEmpty() && !state.isLoading) {
            actions.loadData()
        }
    }

    // 对话框内容
//    println("ManagementPageTemplate调用dialogContent")
    dialogContent()
}

/**
 * 简化版管理页面模板（无分页）
 * @param state 页面状态
 * @param actions 页面操作
 * @param title 页面标题
 * @param emptyMessage 空状态提示信息
 * @param refreshText 刷新按钮文本
 * @param addText 添加按钮文本
 * @param layoutConfig 响应式布局配置
 * @param itemContent 列表项内容组件
 * @param dialogContent 对话框内容组件
 */
@Composable
fun <T> SimpleManagementPageTemplate(
    state: ManagementPageState<T>,
    actions: ManagementPageActions,
    title: String,
    emptyMessage: String,
    refreshText: String = "刷新数据",
    addText: String = "添加",
    layoutConfig: ResponsiveLayoutConfig,
    itemContent: @Composable (T) -> Unit,
    dialogContent: @Composable () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
        ) {
            // 标题和操作按钮区域（仅在扩展型布局显示）
            if (layoutConfig.screenSize == ResponsiveUtils.ScreenSize.EXPANDED) {
                androidx.compose.material3.Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(layoutConfig.contentPadding)
                )

                ResponsiveActionButtons(
                    onRefresh = actions::refresh,
                    onAdd = actions::showAddDialog,
                    isLoading = state.isLoading,
                    refreshText = refreshText,
                    addText = addText,
                    title = null,
                    layoutConfig = layoutConfig
                )
            }

            // 加载状态
            LoadingIndicator(isLoading = state.isLoading)

            // 错误信息显示
            ErrorMessageCard(
                errorMessage = state.errorMessage,
                layoutConfig = layoutConfig
            )

            // 列表内容区域
            ResponsiveListLayout(
                items = state.items,
                isLoading = state.isLoading,
                emptyMessage = emptyMessage,
                onLoadData = { actions.loadData() },
                layoutConfig = layoutConfig,
                modifier = Modifier.weight(1f),
                itemContent = itemContent
            )
        }

        // FAB按钮组 - 悬浮在右下角
         if (layoutConfig.screenSize != ResponsiveUtils.ScreenSize.EXPANDED) {
             Column(
                 modifier = Modifier
                     .align(Alignment.BottomEnd)
                     .padding(16.dp),
                 verticalArrangement = Arrangement.spacedBy(8.dp)
             ) {
                 // 刷新FAB
                 FloatingActionButton(
                     onClick = actions::refresh,
                     modifier = Modifier.size(48.dp),
                     containerColor = MaterialTheme.colorScheme.secondary
                 ) {
                     Icon(
                         imageVector = Icons.Default.Refresh,
                         contentDescription = refreshText
                     )
                 }

                 // 添加FAB
                 FloatingActionButton(
                     onClick = actions::showAddDialog,
                     modifier = Modifier.size(56.dp)
                 ) {
                     Icon(
                         imageVector = Icons.Default.Add,
                         contentDescription = addText
                     )
                 }
             }
         }
    }

    // 自动加载数据
    LaunchedEffect(Unit) {
        if (state.items.isEmpty() && !state.isLoading) {
            actions.loadData()
        }
    }

    // 对话框内容
    dialogContent()
}