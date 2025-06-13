package ovo.sypw.bsp.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 员工管理Tab内容
 */
@Composable
internal fun EmployeeManagementTab(
    viewModel: AdminViewModel,
    layoutConfig: ResponsiveLayoutConfig
) {
    val employeeState by viewModel.employeeState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
    ) {
        // 页面标题
        Text(
            text = "员工管理",
            style = MaterialTheme.typography.headlineMedium
        )

        // 操作按钮区域 - 响应式布局
        if (layoutConfig.useFullWidthButtons) {
            // 紧凑型：垂直排列按钮
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
            ) {
                Button(
                    onClick = { viewModel.refreshEmployees() },
                    enabled = !employeeState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("刷新数据")
                }

                OutlinedButton(
                    onClick = { /* TODO: 添加员工 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("添加员工")
                }
            }
        } else {
            // 中等型和扩展型：水平排列按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(layoutConfig.horizontalSpacing)
            ) {
                Button(
                    onClick = { viewModel.refreshEmployees() },
                    enabled = !employeeState.isLoading
                ) {
                    Text("刷新数据")
                }

                OutlinedButton(
                    onClick = { /* TODO: 添加员工 */ }
                ) {
                    Text("添加员工")
                }
            }
        }

        // 加载状态
        if (employeeState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // 员工列表区域 - 响应式网格布局
        when (layoutConfig.screenSize) {
            ResponsiveUtils.ScreenSize.COMPACT -> {
                // 紧凑型：单列卡片
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(layoutConfig.cardPadding)
                    ) {
                        Text(
                            text = "员工列表",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(layoutConfig.verticalSpacing))
                        Text(
                            text = "暂无员工数据",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    // 示例卡片
                    items(4) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .let { modifier ->
                                    val maxWidth =
                                        ResponsiveUtils.Grid.getMaxCardWidth(layoutConfig.screenSize)
                                    if (maxWidth != Dp.Unspecified) {
                                        modifier.widthIn(max = maxWidth)
                                    } else modifier
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(layoutConfig.cardPadding)
                            ) {
                                Text(
                                    text = if (index == 0) "员工列表" else "员工 ${index}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(layoutConfig.verticalSpacing))
                                Text(
                                    text = if (index == 0) "暂无员工数据" else "员工详情 ${index}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}