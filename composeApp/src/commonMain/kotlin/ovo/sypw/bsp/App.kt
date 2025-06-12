package ovo.sypw.bsp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

import ovo.sypw.bsp.navigation.*
import ovo.sypw.bsp.screens.*
import ovo.sypw.bsp.screens.auth.*
import ovo.sypw.bsp.presentation.screen.ChangePasswordScreen
import ovo.sypw.bsp.utils.FontUtils
import ovo.sypw.bsp.di.getAllModules
import ovo.sypw.bsp.presentation.viewmodel.AuthViewModel
import ovo.sypw.bsp.utils.Logger

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
        val contentFontFamily = FontUtils.getDefaultFontFamily()
        val customTypography = createCustomTypography(contentFontFamily)
        MaterialTheme(
            typography = customTypography
        ) {
            AppContent()
        }
    }
    

    

}

@Composable
private fun AppContent() {
    val authViewModel: AuthViewModel = koinViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    
    // 显示加载状态
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
//    MainAppContent()
    // 根据登录状态显示不同内容
    if (isLoggedIn) {
        // 已登录，显示主应用界面
        MainAppContent()
    } else {
        // 未登录，显示登录界面
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
    val currentScreen by navigationManager.currentScreen
    
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
            // TODO: 创建注册界面
            LoginScreen(
                onNavigateToRegister = {
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
        val screenWidth = maxWidth
        val useRailNavigation = screenWidth >= 840.dp // Material Design 3 推荐的断点
        
        if (useRailNavigation) {
            // 宽屏：使用侧边导航布局
            RailNavigationLayout(navigationManager = navigationManager)
        } else {
            // 窄屏：使用底部导航布局
            BottomNavigationLayout(navigationManager = navigationManager)
        }
    }
}

/**
 * 侧边导航布局（宽屏使用）
 * @param navigationManager 导航管理器
 */
@Composable
private fun RailNavigationLayout(
    navigationManager: NavigationManager
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // 左侧导航栏
        SideNavigationBar(
            navigationManager = navigationManager
        )
        
        // 右侧内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            MainContent(
                navigationManager = navigationManager,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * 底部导航布局（窄屏使用）
 * @param navigationManager 导航管理器
 */
@Composable
private fun BottomNavigationLayout(
    navigationManager: NavigationManager
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
                modifier = Modifier.fillMaxSize()
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
    modifier: Modifier = Modifier
) {
    val currentScreen by navigationManager.currentScreen
    
    when (currentScreen) {
        AppScreen.HOME.route -> {
            HomeScreen(modifier = modifier)
        }
        AppScreen.API_TEST.route -> {
            ApiTestScreen(modifier = modifier)
        }
        AppScreen.ADMIN.route -> {
            AdminScreen(modifier = modifier)
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
                navigationManager.navigateTo(AppScreen.HOME.route)
            }
            HomeScreen(modifier = modifier)
        }
        AppScreen.REGISTER.route -> {
            // 在主应用中不应该显示注册界面，重定向到首页
            LaunchedEffect(Unit) {
                navigationManager.navigateTo(AppScreen.HOME.route)
            }
            HomeScreen(modifier = modifier)
        }
        else -> {
            // 默认显示首页
            HomeScreen(modifier = modifier)
        }
    }
}