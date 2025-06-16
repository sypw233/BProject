package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import ovo.sypw.bsp.navigation.SideNavigationBar
import ovo.sypw.bsp.navigation.rememberNavigationManager
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.presentation.ui.ImageTestScreen
import ovo.sypw.bsp.presentation.ui.FileUploadTestScreen
import ovo.sypw.bsp.domain.usecase.FileUploadUseCase
import org.koin.compose.koinInject
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
    viewModel: AdminViewModel = koinInject(),
    layoutConfig: ResponsiveLayoutConfig
) {

    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    // 使用BoxWithConstraints获取屏幕尺寸信息
    when (layoutConfig.screenSize) {
        ResponsiveUtils.ScreenSize.COMPACT, ResponsiveUtils.ScreenSize.MEDIUM -> {
            // 紧凑型布局：垂直Tab布局
            CompactAdminLayout(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = viewModel::selectTab,
                layoutConfig = layoutConfig
            )
        }

        ResponsiveUtils.ScreenSize.EXPANDED -> {
            // 扩展型布局：侧边Tab或并排显示
            ExpandedAdminLayout(
                selectedTabIndex = selectedTabIndex,
                onTabSelected = viewModel::selectTab,
                layoutConfig = layoutConfig
            )
        }

    }

}


/**
 * 紧凑型布局
 */
@Composable
private fun CompactAdminLayout(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AdminConfig.adminTabs.forEach { tab ->
                Tab(
                    modifier = Modifier.weight(1f),
                    selected = selectedTabIndex == tab.index,
                    onClick = { onTabSelected(tab.index) },
                    text = { Text(tab.title) }
                )
            }
        }

        // Tab内容区域
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(layoutConfig.screenPadding)
        ) {
            GetAdminTab(
                selectedTabIndex = selectedTabIndex,
                layoutConfig = layoutConfig
            )
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
    layoutConfig: ResponsiveLayoutConfig
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
            GetAdminTab(
                selectedTabIndex = selectedTabIndex,
                layoutConfig = layoutConfig
            )
        }
    }
}

@Composable
fun GetAdminTab(
    selectedTabIndex: Int,
    layoutConfig: ResponsiveLayoutConfig
) {
    when (selectedTabIndex) {
        0 -> DashboardTab(
            modifier = Modifier.fillMaxSize()
        )

        1 -> DepartmentManagementTab(
            layoutConfig = layoutConfig
        )

        2 -> EmployeeManagementTab(
            layoutConfig = layoutConfig
        )

        3 -> ClassManagementTab(
            layoutConfig = layoutConfig
        )

        4 -> StudentManagementTab(
            layoutConfig = layoutConfig
        )

        5 -> AnnouncementManagementTab(
            layoutConfig = layoutConfig
        )

        6 -> ImageTestScreen(
            modifier = Modifier.fillMaxSize()
        )

        7 -> FileUploadTestScreen(
            fileUploadUseCase = koinInject<FileUploadUseCase>(),
            modifier = Modifier.fillMaxSize()
        )

        else -> DashboardTab(
            modifier = Modifier.fillMaxSize()
        )
    }
}

