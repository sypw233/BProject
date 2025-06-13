package ovo.sypw.bsp.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.koin.compose.koinInject
import ovo.sypw.bsp.navigation.SideNavigationBar
import ovo.sypw.bsp.navigation.rememberNavigationManager
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
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
    layoutConfig: ResponsiveLayoutConfig,
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
                1 -> DepartmentManagementPagingTab(
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
                2 -> EmployeeManagementTab(
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
    layoutConfig: ResponsiveLayoutConfig,
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
                1 -> DepartmentManagementPagingTab(
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
                2 -> EmployeeManagementTab(
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
    layoutConfig: ResponsiveLayoutConfig,
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

