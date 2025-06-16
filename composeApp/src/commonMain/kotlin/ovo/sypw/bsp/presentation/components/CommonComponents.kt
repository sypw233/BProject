package ovo.sypw.bsp.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 响应式操作按钮组组件
 * @param onRefresh 刷新回调
 * @param onAdd 添加回调
 * @param isLoading 是否正在加载
 * @param refreshText 刷新按钮文本
 * @param addText 添加按钮文本
 * @param title 页面标题（仅在扩展型布局显示）
 * @param layoutConfig 响应式布局配置
 */
@Composable
fun ResponsiveActionButtons(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    onAdd: () -> Unit,
    isLoading: Boolean = false,
    refreshText: String = "刷新数据",
    addText: String = "添加",
    title: String? = null,
    layoutConfig: ResponsiveLayoutConfig
) {
    if (layoutConfig.screenSize != ResponsiveUtils.ScreenSize.EXPANDED) {
        // 紧凑型：垂直排列按钮
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
        ) {
            Button(
                onClick = onRefresh,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(refreshText)
            }

            OutlinedButton(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(addText)
            }
        }
    } else {
        // 扩展型：显示标题和水平排列按钮
        Column {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(layoutConfig.horizontalSpacing)
            ) {
                Button(
                    onClick = onRefresh,
                    enabled = !isLoading
                ) {
                    Text(refreshText)
                }

                OutlinedButton(
                    onClick = onAdd
                ) {
                    Text(addText)
                }
            }
        }
    }
}

/**
 * 加载状态组件
 * @param isLoading 是否正在加载
 */
@Composable
fun LoadingIndicator(
    isLoading: Boolean
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

/**
 * 错误信息显示组件
 * @param errorMessage 错误信息
 * @param layoutConfig 响应式布局配置
 */
@Composable
fun ErrorMessageCard(
    errorMessage: String?,
    layoutConfig: ResponsiveLayoutConfig
) {
    errorMessage?.let { message ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Text(
                text = "错误: $message",
                modifier = Modifier.padding(layoutConfig.cardPadding),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

/**
 * 空状态组件
 * @param message 空状态提示信息
 * @param actionText 操作按钮文本
 * @param onAction 操作回调
 * @param layoutConfig 响应式布局配置
 */
@Composable
fun EmptyStateCard(
    message: String,
    actionText: String = "加载数据",
    onAction: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(layoutConfig.cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(layoutConfig.verticalSpacing))
            OutlinedButton(
                onClick = onAction
            ) {
                Text(actionText)
            }
        }
    }
}

/**
 * 通用卡片操作按钮组
 * @param onEdit 编辑回调
 * @param onDelete 删除回调
 * @param editContentDescription 编辑按钮描述
 * @param deleteContentDescription 删除按钮描述
 */
@Composable
fun CardActionButtons(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    editContentDescription: String = "编辑",
    deleteContentDescription: String = "删除"
) {
    Row {
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = editContentDescription,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = deleteContentDescription,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun IconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.IconButton(onClick = onClick, content = content)
}