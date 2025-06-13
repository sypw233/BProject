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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 部门管理Tab内容
 */
@Composable
internal fun DepartmentManagementTab(
    viewModel: AdminViewModel,
    layoutConfig: ResponsiveLayoutConfig
) {
    val departmentState by viewModel.departmentState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
    ) {
        // 页面标题
        Text(
            text = "部门管理",
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
                    onClick = { viewModel.refreshDepartments() },
                    enabled = !departmentState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("刷新数据")
                }

                OutlinedButton(
                    onClick = { viewModel.showAddDepartmentDialog() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("添加部门")
                }
            }
        } else {
            // 中等型和扩展型：水平排列按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(layoutConfig.horizontalSpacing)
            ) {
                Button(
                    onClick = { viewModel.refreshDepartments() },
                    enabled = !departmentState.isLoading
                ) {
                    Text("刷新数据")
                }

                OutlinedButton(
                    onClick = { viewModel.showAddDepartmentDialog() }
                ) {
                    Text("添加部门")
                }
            }
        }

        // 加载状态
        if (departmentState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // 错误信息显示
        departmentState.errorMessage?.let { errorMessage ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "错误: $errorMessage",
                    modifier = Modifier.padding(layoutConfig.cardPadding),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // 部门列表区域 - 响应式布局
        when (layoutConfig.screenSize) {
            ResponsiveUtils.ScreenSize.COMPACT -> {
                // 紧凑型：垂直列表
                if (departmentState.departments.isEmpty() && !departmentState.isLoading) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(layoutConfig.cardPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "暂无部门数据",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(layoutConfig.verticalSpacing))
                            OutlinedButton(
                                onClick = { viewModel.loadDepartments() }
                            ) {
                                Text("加载数据")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
                    ) {
                        items(departmentState.departments) { department ->
                            DepartmentCard(
                                department = department,
                                onEdit = { viewModel.showEditDepartmentDialog(department) },
                                onDelete = { viewModel.deleteDepartment(department.id) },
                                layoutConfig = layoutConfig
                            )
                        }
                    }
                }
            }
            else -> {
                // 中等型和扩展型：网格布局
                if (departmentState.departments.isEmpty() && !departmentState.isLoading) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(layoutConfig.cardPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "暂无部门数据",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(layoutConfig.verticalSpacing))
                            OutlinedButton(
                                onClick = { viewModel.loadDepartments() }
                            ) {
                                Text("加载数据")
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(layoutConfig.columnCount),
                        horizontalArrangement = Arrangement.spacedBy(layoutConfig.horizontalSpacing),
                        verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
                    ) {
                        items(departmentState.departments) { department ->
                            DepartmentCard(
                                department = department,
                                onEdit = { viewModel.showEditDepartmentDialog(department) },
                                onDelete = { viewModel.deleteDepartment(department.id) },
                                layoutConfig = layoutConfig
                            )
                        }
                    }
                }
            }
        }
    }

    // 自动加载数据
    LaunchedEffect(Unit) {
        if (departmentState.departments.isEmpty() && !departmentState.isLoading) {
            viewModel.loadDepartments()
        }
    }
    
    // 部门Dialog
    DepartmentDialog(viewModel = viewModel)
}

/**
 * 部门卡片组件
 */
@Composable
private fun DepartmentCard(
    department: DepartmentDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let { modifier ->
                val maxWidth = ResponsiveUtils.Grid.getMaxCardWidth(layoutConfig.screenSize)
                if (maxWidth != Dp.Unspecified) {
                    modifier.widthIn(max = maxWidth)
                } else modifier
            }
    ) {
        Column(
            modifier = Modifier.padding(layoutConfig.cardPadding)
        ) {
            // 部门标题和操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = department.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑部门",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除部门",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(layoutConfig.verticalSpacing))
            
            // 部门描述
//            if (department.description.isNotBlank()) {
//                Text(
//                    text = department.description,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//                Spacer(modifier = Modifier.height(layoutConfig.verticalSpacing))
//            }
            
            // 员工数量
//            Text(
//                text = "员工数量: ${department.employeeCount}",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
        }
    }
}