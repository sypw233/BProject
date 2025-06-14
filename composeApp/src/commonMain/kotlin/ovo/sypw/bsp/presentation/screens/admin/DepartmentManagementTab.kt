package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.utils.Logger
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
        Logger.d("当前Tab获取布局大小: ${layoutConfig}")
        // 操作按钮区域 - 响应式布局
        if (layoutConfig.screenSize!= ResponsiveUtils.ScreenSize.EXPANDED) {

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
            // 页面标题
            Text(
                text = "部门管理",
                style = MaterialTheme.typography.headlineMedium
            )
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
        Box(
            modifier = Modifier.weight(1f)
        ) {
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

        // 分页组件 - 始终显示在底部
        departmentState.pageInfo?.let { pageInfo ->
            PaginationComponent(
                pageInfo = pageInfo,
                onPageChange = { page ->
                    viewModel.loadDepartments(
                        current = page,
                        size = pageInfo.size
                    )
                },
                onPageSizeChange = { size -> viewModel.loadDepartments(current = 1, size = size) },
                layoutConfig = layoutConfig
            )
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

/**
 * 分页组件
 */
@Composable
private fun PaginationComponent(
    pageInfo: PageResultDto<DepartmentDto>,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    var showPageSizeDropdown by remember { mutableStateOf(false) }
    val pageSizeOptions = listOf(5, 10, 20, 50)

    Card(
        modifier = Modifier.fillMaxWidth()
//            .height(100.dp)
    ) {
        if (layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT) {
            // 紧凑型：垂直布局
            Column(
                modifier = Modifier.padding(layoutConfig.cardPadding),
                verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
            ) {
                // 页面大小选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 分页信息
                    Text(
                        text = "共 ${pageInfo.total} 条记录 ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "每页显示:",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Box {
                            TextButton(
                                onClick = { showPageSizeDropdown = true }
                            ) {
                                Text("${pageInfo.size} 条")
                            }

                            DropdownMenu(
                                expanded = showPageSizeDropdown,
                                onDismissRequest = { showPageSizeDropdown = false }
                            ) {
                                pageSizeOptions.forEach { size ->
                                    DropdownMenuItem(
                                        text = { Text("$size 条") },
                                        onClick = {
                                            onPageSizeChange(size)
                                            showPageSizeDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                    }

                    // 页面导航
                    Row(
//                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 上一页
                        IconButton(
                            onClick = { onPageChange(pageInfo.current - 1) },
                            enabled = pageInfo.current > 1
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "上一页"
                            )
                        }

                        // 页码显示
                        Text(
                            text = "${pageInfo.current} / ${pageInfo.pages}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // 下一页
                        IconButton(
                            onClick = { onPageChange(pageInfo.current + 1) },
                            enabled = pageInfo.current < pageInfo.pages
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "下一页"
                            )
                        }
                    }
                }


            }
        } else {
            // 中等型和扩展型：水平布局
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(layoutConfig.cardPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分页信息和页面大小选择
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(layoutConfig.horizontalSpacing)
                ) {
                    Text(
                        text = "共 ${pageInfo.total} 条记录",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "每页显示:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Box {
                        TextButton(
                            onClick = { showPageSizeDropdown = true }
                        ) {
                            Text("${pageInfo.size} 条")
                        }

                        DropdownMenu(
                            expanded = showPageSizeDropdown,
                            onDismissRequest = { showPageSizeDropdown = false }
                        ) {
                            pageSizeOptions.forEach { size ->
                                DropdownMenuItem(
                                    text = { Text("$size 条") },
                                    onClick = {
                                        onPageSizeChange(size)
                                        showPageSizeDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 页面导航
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 上一页
                    IconButton(
                        onClick = { onPageChange(pageInfo.current - 1) },
                        enabled = pageInfo.current > 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "上一页"
                        )
                    }

                    // 页码按钮组
                    PaginationButtons(
                        currentPage = pageInfo.current,
                        totalPages = pageInfo.pages,
                        onPageChange = onPageChange
                    )

                    // 下一页
                    IconButton(
                        onClick = { onPageChange(pageInfo.current + 1) },
                        enabled = pageInfo.current < pageInfo.pages
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "下一页"
                        )
                    }
                }
            }
        }
    }
}

/**
 * 分页按钮组件
 */
@Composable
private fun PaginationButtons(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    val visiblePages = getVisiblePages(currentPage, totalPages)

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visiblePages.forEach { page ->
            when (page) {
                -1 -> {
                    // 省略号
                    Text(
                        text = "...",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    Button(
                        onClick = { if (page != currentPage) onPageChange(page) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (page == currentPage) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            contentColor = if (page == currentPage) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        ),
                        modifier = Modifier.width(40.dp)
                            .height(30.dp),
                        contentPadding = PaddingValues(0.dp),
//                        border = if (page != currentPage) {
//                            BorderStroke(0.1.dp, MaterialTheme.colorScheme.outline)
//                        } else null
                    ) {
                        Text(
                            text = page.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

/**
 * 获取可见的页码列表
 */
private fun getVisiblePages(currentPage: Int, totalPages: Int): List<Int> {
    if (totalPages <= 7) {
        return (1..totalPages).toList()
    }

    val result = mutableListOf<Int>()

    // 总是显示第一页
    result.add(1)

    when {
        currentPage <= 4 -> {
            // 当前页在前面
            result.addAll(2..5)
            result.add(-1) // 省略号
            result.add(totalPages)
        }

        currentPage >= totalPages - 3 -> {
            // 当前页在后面
            result.add(-1) // 省略号
            result.addAll((totalPages - 4)..totalPages)
        }

        else -> {
            // 当前页在中间
            result.add(-1) // 省略号
            result.addAll((currentPage - 1)..(currentPage + 1))
            result.add(-1) // 省略号
            result.add(totalPages)
        }
    }

    return result
}