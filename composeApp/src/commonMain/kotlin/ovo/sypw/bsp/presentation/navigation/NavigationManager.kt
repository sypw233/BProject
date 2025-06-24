package ovo.sypw.bsp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * 导航管理器类
 * 负责管理当前选中的页面状态
 */
class NavigationManager {
    private val _currentScreen = mutableStateOf(AppScreen.ADMIN.route)
    val currentScreen: State<String> = _currentScreen

    // 存储当前选中的公告ID
    private val _selectedAnnouncementId = mutableStateOf<Int?>(null)
    val selectedAnnouncementId: State<Int?> = _selectedAnnouncementId

    /**
     * 导航到指定页面
     * @param route 目标页面路由
     */
    fun navigateTo(route: String) {
        _currentScreen.value = route
    }

    /**
     * 导航到公告详情页面
     * @param announcementId 公告ID
     */
    fun navigateToAnnouncementDetail(announcementId: Int) {
        _selectedAnnouncementId.value = announcementId
        _currentScreen.value = AppScreen.ANNOUNCEMENT_DETAIL.route
    }

    /**
     * 返回到公告列表页面
     */
    fun navigateBackFromAnnouncementDetail() {
        _selectedAnnouncementId.value = null
        _currentScreen.value = AppScreen.ANNOUNCEMENTS.route
    }

    /**
     * 检查当前是否为指定页面
     * @param route 页面路由
     * @return 是否为当前页面
     */
    fun isCurrentScreen(route: String): Boolean {
        return _currentScreen.value == route
    }
}

/**
 * 创建导航管理器的Composable函数
 * @return 导航管理器实例
 */
@Composable
fun rememberNavigationManager(): NavigationManager {
    return remember { NavigationManager() }
}