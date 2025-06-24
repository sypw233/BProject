package ovo.sypw.bsp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import com.hoc081098.kmp.viewmodel.koin.compose.koinKmpViewModel
import ovo.sypw.bsp.presentation.navigation.AppScreen
import ovo.sypw.bsp.presentation.navigation.BottomNavigationBar
import ovo.sypw.bsp.presentation.navigation.NavigationManager
import ovo.sypw.bsp.presentation.navigation.SideNavigationBar
import ovo.sypw.bsp.presentation.navigation.getNavigationItems
import ovo.sypw.bsp.presentation.navigation.rememberNavigationManager
import ovo.sypw.bsp.presentation.screens.NetdiskScreen
import ovo.sypw.bsp.presentation.screens.ProfileScreen
import ovo.sypw.bsp.presentation.screens.admin.AdminScreen
import ovo.sypw.bsp.presentation.screens.admin.GetAdminTab
import ovo.sypw.bsp.presentation.screens.aichat.AIChatScreen
import ovo.sypw.bsp.presentation.screens.announcement.AnnouncementDetailScreen
import ovo.sypw.bsp.presentation.screens.announcement.PublicAnnouncementScreen
import ovo.sypw.bsp.presentation.screens.auth.ChangePasswordScreen
import ovo.sypw.bsp.presentation.screens.auth.LoginScreen
import ovo.sypw.bsp.presentation.screens.auth.RegisterScreen
import ovo.sypw.bsp.presentation.viewmodel.admin.AuthViewModel
import ovo.sypw.bsp.utils.Logger
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils
import ovo.sypw.bsp.utils.getResponsiveLayoutConfig

/**
 * 平台特定的 Koin 应用初始化
 */
@Composable
expect fun PlatformKoinApplication(content: @Composable () -> Unit)

/**
 * 创建自定义字体排版
 * @param fontFamily 要应用的字体族
 * @return 应用了自定义字体的Typography
 */
@Composable
private fun createCustomTypography(fontFamily: FontFamily): Typography {
    val defaultTypography = MaterialTheme.typography
    return Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = fontFamily)
    )
}

/**
 * 应用主组件
 * 根据窗口宽度自适应显示导航界面
 */
@Composable
fun App() {
    Logger.i("APP START ON ${getPlatform().name}")
    PlatformKoinApplication {
        // 加载自定义字体并创建自定义主题
//        val contentFontFamily = FontUtils.getDefaultFontFamily()
//        val customTypography = createCustomTypography(contentFontFamily)
        MaterialTheme {
            AppContent()
        }
    }


}

@Composable
private fun AppContent() {
    val authViewModel: AuthViewModel = koinKmpViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    // 显示加载状态
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
//    ApiTestScreen()
//    MainAppContent()
    // 根据登录状态显示不同内容
    if (isLoggedIn) {
        // 已登录，显示主应用界面
        MainAppContent()
    } else {
        val navigationManager = rememberNavigationManager()
        // 设置初始路由为登录页面
        LaunchedEffect(Unit) {
            navigationManager.navigateTo(AppScreen.LOGIN.route)
        }

        AuthContent(navigationManager = navigationManager)
    }
}

/**
 * 认证相关内容（登录/注册界面）
 * @param navigationManager 导航管理器
 */
@Composable
private fun AuthContent(
    navigationManager: NavigationManager
) {
    val currentScreen = navigationManager.currentScreen.value

    when (currentScreen) {
        AppScreen.LOGIN.route -> {
            LoginScreen(
                onLoginSuccess = {
                    // 登录成功后不需要手动导航，AuthViewModel会更新isLoggedIn状态
                },
                onNavigateToRegister = {
                    navigationManager.navigateTo(AppScreen.REGISTER.route)
                }
            )
        }

        AppScreen.REGISTER.route -> {
            RegisterScreen(
                onNavigateToLogin = {
                    navigationManager.navigateTo(AppScreen.LOGIN.route)
                },
                onRegisterSuccess = {
                    navigationManager.navigateTo(AppScreen.LOGIN.route)
                }
            )
        }

        else -> {
            // 默认显示登录界面
            LoginScreen(
                onNavigateToRegister = {
                    navigationManager.navigateTo(AppScreen.REGISTER.route)
                }
            )
        }
    }
}

/**
 * 主应用内容（已登录状态）
 */
@Composable
private fun MainAppContent() {
    val navigationManager = rememberNavigationManager()

    // 使用BoxWithConstraints获取窗口尺寸
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val layoutConfig = getResponsiveLayoutConfig(maxWidth)
//        Logger.d("当前Nav获取布局大小: ${layoutConfig}")
        when (layoutConfig.screenSize) {
            ResponsiveUtils.ScreenSize.COMPACT ->
                BottomNavigationLayout(
                    navigationManager = navigationManager,
                    layoutConfig = layoutConfig
                )

            ResponsiveUtils.ScreenSize.MEDIUM ->
                MediumRailNavigationLayout(
                    navigationManager = navigationManager,
                    layoutConfig = layoutConfig
                )

            ResponsiveUtils.ScreenSize.EXPANDED ->
                ExpandedRailNavigationLayout(
                    navigationManager = navigationManager,
                    layoutConfig = layoutConfig
                )
        }

    }
}

/**
 * 侧边导航布局（中屏使用）
 * 导航栏显示在侧边，没有子项，文字显示在图标下方
 * @param navigationManager 导航管理器
 */
@Composable
private fun MediumRailNavigationLayout(
    navigationManager: NavigationManager,
    layoutConfig: ResponsiveLayoutConfig
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // 左侧导航栏 - 中等布局：文字在图标下方，无子项
        NavigationRail(
            modifier = Modifier.fillMaxHeight()
        ) {
            val currentScreen = navigationManager.currentScreen.value
            val navigationItems = getNavigationItems()
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly, // 均匀分布
                horizontalAlignment = Alignment.CenterHorizontally // 水平居中
            ) {
                navigationItems.forEach { item ->
                    NavigationRailItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentScreen == item.route,
                        onClick = { navigationManager.navigateTo(item.route) }
                    )
                }
            }

        }

        // 右侧内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            MainContent(
                navigationManager = navigationManager,
                modifier = Modifier.fillMaxSize(),
                layoutConfig = layoutConfig,
            )
        }
    }
}

/**
 * 侧边导航布局（大屏使用）
 * 导航栏显示在侧边，可以显示子项，文字在图标右方，同时支持隐藏文字
 * @param navigationManager 导航管理器
 */
@Composable
private fun ExpandedRailNavigationLayout(
    navigationManager: NavigationManager,
    layoutConfig: ResponsiveLayoutConfig
) {
    var isRailExpanded by remember { mutableStateOf(true) }
    var adminTabIndex by remember { mutableStateOf(0) }
    val currentScreen = navigationManager.currentScreen.value

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // 左侧可折叠导航栏 - 大布局：支持子项，文字在图标右方，可隐藏文字
        SideNavigationBar(
            navigationManager = navigationManager,
            isExpanded = isRailExpanded,
            onExpandToggle = { isRailExpanded = !isRailExpanded },
            adminTabIndex = if (currentScreen == AppScreen.ADMIN.route) adminTabIndex else -1,
            onAdminTabSelected = { adminTabIndex = it }
        )

        // 右侧内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // 如果是后台管理页面，直接显示对应的Tab内容
            if (currentScreen == AppScreen.ADMIN.route) {
                AdminContentLayout(
                    selectedTabIndex = adminTabIndex,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                MainContent(
                    navigationManager = navigationManager,
                    modifier = Modifier.fillMaxSize(),
                    layoutConfig = layoutConfig,
                )
            }
        }
    }
}

/**
 * 底部导航布局（窄屏使用）
 * @param navigationManager 导航管理器
 */
@Composable
private fun BottomNavigationLayout(
    navigationManager: NavigationManager,
    layoutConfig: ResponsiveLayoutConfig
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 主要内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            MainContent(
                navigationManager = navigationManager,
                modifier = Modifier.fillMaxSize(),
                layoutConfig = layoutConfig
            )
        }

        // 底部导航栏
        BottomNavigationBar(
            navigationManager = navigationManager
        )
    }
}

/**
 * 主要内容区域
 * 根据当前选中的页面显示对应内容
 * @param navigationManager 导航管理器
 * @param modifier 修饰符
 */
@Composable
private fun MainContent(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier,
    layoutConfig: ResponsiveLayoutConfig
) {
    val currentScreen = navigationManager.currentScreen.value

    when (currentScreen) {
        AppScreen.ANNOUNCEMENTS.route -> {
            PublicAnnouncementScreen(
                modifier = modifier,
                layoutConfig = layoutConfig,
                onAnnouncementClick = { announcement ->
                    if (announcement.id != null) {
                        navigationManager.navigateToAnnouncementDetail(announcement.id)
                    } else {
                        Logger.e("公告ID为空")
                    }
                }
            )
        }

        AppScreen.ANNOUNCEMENT_DETAIL.route -> {
            val selectedAnnouncementId = navigationManager.selectedAnnouncementId.value
            if (selectedAnnouncementId != null) {
                AnnouncementDetailScreen(
                    announcementId = selectedAnnouncementId,
                    onBackClick = {
                        navigationManager.navigateBackFromAnnouncementDetail()
                    },
                    modifier = modifier
                )
            } else {
                // 如果没有选中的公告ID，返回公告列表
                LaunchedEffect(Unit) {
                    navigationManager.navigateTo(AppScreen.ANNOUNCEMENTS.route)
                }
            }
        }

        AppScreen.AI_CHAT.route -> {
            AIChatScreen(
                modifier = modifier,
                layoutConfig = layoutConfig
            )
        }

        AppScreen.NETDISK.route -> {
            NetdiskScreen(
                modifier = modifier
            )
        }

        AppScreen.ADMIN.route -> {
            AdminScreen(
                modifier = modifier,
                layoutConfig = layoutConfig
            )
        }

        AppScreen.PROFILE.route -> {
            ProfileScreen(
                modifier = modifier,
                onNavigateToChangePassword = {
                    navigationManager.navigateTo(AppScreen.CHANGE_PASSWORD.route)
                },
                onNavigateToLogin = {
                    // 退出登录后跳转到登录页面
                    navigationManager.navigateTo(AppScreen.LOGIN.route)
                }
            )
        }

        AppScreen.CHANGE_PASSWORD.route -> {
            ChangePasswordScreen(
                onNavigateBack = {
                    navigationManager.navigateTo(AppScreen.LOGIN.route)
                }
            )
        }

        AppScreen.LOGIN.route -> {
            // 在主应用中不应该显示登录界面，重定向到首页
            LaunchedEffect(Unit) {
                navigationManager.navigateTo(AppScreen.ADMIN.route)
            }
            AdminScreen(modifier = modifier, layoutConfig = layoutConfig)
        }

        AppScreen.REGISTER.route -> {
            // 在主应用中不应该显示注册界面，重定向到首页
            LaunchedEffect(Unit) {
                navigationManager.navigateTo(AppScreen.ADMIN.route)
            }
            AdminScreen(modifier = modifier, layoutConfig = layoutConfig)
        }

        else -> {
            // 默认显示首页
            AdminScreen(modifier = modifier, layoutConfig = layoutConfig)
        }
    }
}

/**
 * 后台管理内容布局（大布局专用）
 * 直接显示后台管理的Tab内容，避免重复的侧边栏
 * @param selectedTabIndex 选中的Tab索引
 * @param modifier 修饰符
 */
@Composable
private fun AdminContentLayout(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier
) {

    // 使用BoxWithConstraints获取屏幕尺寸信息
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val layoutConfig = getResponsiveLayoutConfig(maxWidth)

        // 直接显示Tab内容，不需要Tab栏（由侧边导航栏控制）
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