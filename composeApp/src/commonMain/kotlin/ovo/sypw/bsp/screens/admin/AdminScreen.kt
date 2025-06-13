package ovo.sypw.bsp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ovo.sypw.bsp.navigation.NavigationManager
import ovo.sypw.bsp.navigation.SideNavigationBar
import ovo.sypw.bsp.navigation.rememberNavigationManager
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.utils.ResponsiveUtils
import ovo.sypw.bsp.utils.getResponsiveLayoutConfig

/**
 * 后台管理主界面
 * 包含部门管理和员工管理的Tab切换
 * 支持响应式设计，在不同屏幕尺寸下有不同的布局表现
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminViewModel = koinInject()
) {
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    
    // 使用BoxWithConstraints获取屏幕尺寸信息
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val layoutConfig = getResponsiveLayoutConfig(maxWidth)
        
        when (layoutConfig.screenSize) {
            ResponsiveUtils.ScreenSize.COMPACT -> {
                // 紧凑型布局：垂直Tab布局
                CompactAdminLayout(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = viewModel::selectTab,
                    layoutConfig = layoutConfig,
                    viewModel = viewModel
                )
            }
            ResponsiveUtils.ScreenSize.MEDIUM -> {
                // 中等型布局：标准Tab布局
                MediumAdminLayout(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = viewModel::selectTab,
                    layoutConfig = layoutConfig,
                    viewModel = viewModel
                )
            }
            ResponsiveUtils.ScreenSize.EXPANDED -> {
                // 扩展型布局：侧边Tab或并排显示
                ExpandedAdminLayout(
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = viewModel::selectTab,
                    layoutConfig = layoutConfig,
                    viewModel = viewModel
                )
            }
        }
    }
}

/**
 * 紧凑型布局（手机竖屏）
 */
@Composable
private fun CompactAdminLayout(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    layoutConfig: ovo.sypw.bsp.utils.ResponsiveLayoutConfig,
    viewModel: AdminViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 使用ScrollableTabRow适应小屏幕
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = layoutConfig.contentPadding
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text("部门管理") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text("员工管理") }
            )
        }
        
        // Tab内容区域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(layoutConfig.screenPadding)
        ) {
            when (selectedTabIndex) {
                0 -> DepartmentManagementTab(
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
                1 -> EmployeeManagementTab(
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
            }
        }
    }
}

/**
 * 中等型布局（手机横屏/小平板）
 */
@Composable
private fun MediumAdminLayout(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    layoutConfig: ovo.sypw.bsp.utils.ResponsiveLayoutConfig,
    viewModel: AdminViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 标准TabRow
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text("部门管理") },
                modifier = Modifier.defaultMinSize(minWidth = ResponsiveUtils.Tab.getTabMinWidth(layoutConfig.screenSize))
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text("员工管理") },
                modifier = Modifier.defaultMinSize(minWidth = ResponsiveUtils.Tab.getTabMinWidth(layoutConfig.screenSize))
            )
        }
        
        // Tab内容区域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(layoutConfig.screenPadding)
        ) {
            when (selectedTabIndex) {
                0 -> DepartmentManagementTab(
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
                1 -> EmployeeManagementTab(
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
            }
        }
    }
}

/**
 * 扩展型布局（大平板/桌面）
 */
@Composable
private fun ExpandedAdminLayout(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    layoutConfig: ovo.sypw.bsp.utils.ResponsiveLayoutConfig,
    viewModel: AdminViewModel
) {
    var isRailExpanded by remember { mutableStateOf(true) }
    val navigationManager = rememberNavigationManager()
    
    // 确保导航到后台管理页面
    LaunchedEffect(Unit) {
        navigationManager.navigateTo("admin")
    }
    
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // 左侧集成的侧边导航栏
        SideNavigationBar(
            navigationManager = navigationManager,
            isExpanded = isRailExpanded,
            onExpandToggle = { isRailExpanded = !isRailExpanded },
            adminTabIndex = selectedTabIndex,
            onAdminTabSelected = onTabSelected
        )
        
        // 右侧内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(layoutConfig.screenPadding)
        ) {
            when (selectedTabIndex) {
                0 -> DepartmentManagementTab(
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
                1 -> EmployeeManagementTab(
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
            }
        }
    }
}



/**
 * 部门管理Tab内容
 */
@Composable
internal fun DepartmentManagementTab(
    viewModel: AdminViewModel,
    layoutConfig: ovo.sypw.bsp.utils.ResponsiveLayoutConfig
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
                    onClick = { /* TODO: 添加部门 */ },
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
                    onClick = { /* TODO: 添加部门 */ }
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
        
        // 部门列表区域 - 响应式网格布局
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
                            text = "部门列表",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(layoutConfig.verticalSpacing))
                        Text(
                            text = "暂无部门数据",
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
                    items(3) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .let { modifier ->
                                    val maxWidth = ResponsiveUtils.Grid.getMaxCardWidth(layoutConfig.screenSize)
                                    if (maxWidth != androidx.compose.ui.unit.Dp.Unspecified) {
                                        modifier.widthIn(max = maxWidth)
                                    } else modifier
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(layoutConfig.cardPadding)
                            ) {
                                Text(
                                    text = if (index == 0) "部门列表" else "部门 ${index + 1}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(layoutConfig.verticalSpacing))
                                Text(
                                    text = if (index == 0) "暂无部门数据" else "部门详情 ${index + 1}",
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

/**
 * 员工管理Tab内容
 */
@Composable
internal fun EmployeeManagementTab(
    viewModel: AdminViewModel,
    layoutConfig: ovo.sypw.bsp.utils.ResponsiveLayoutConfig
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
                                    val maxWidth = ResponsiveUtils.Grid.getMaxCardWidth(layoutConfig.screenSize)
                                    if (maxWidth != androidx.compose.ui.unit.Dp.Unspecified) {
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