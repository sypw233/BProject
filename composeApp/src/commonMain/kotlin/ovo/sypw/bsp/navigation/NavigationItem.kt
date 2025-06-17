package ovo.sypw.bsp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 导航项目数据类
 * @param route 路由标识
 * @param title 显示标题
 * @param icon 图标
 */
data class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

/**
 * 应用页面枚举
 */
enum class AppScreen(val route: String) {
    HOME("home"),
    ANNOUNCEMENTS("announcements"),
    ANNOUNCEMENT_DETAIL("announcement_detail"),
    PROFILE("profile"),
    API_TEST("api_test"),
    ADMIN("admin"),
    LOGIN("login"),
    REGISTER("register"),
    CHANGE_PASSWORD("change_password")
}

/**
 * 获取所有导航项目
 * @return 导航项目列表
 */
fun getNavigationItems(): List<NavigationItem> {
    return listOf(
        NavigationItem(
            route = AppScreen.HOME.route,
            title = "首页",
            icon = Icons.Default.Home
        ),
        NavigationItem(
            route = AppScreen.ANNOUNCEMENTS.route,
            title = "公告",
            icon = Icons.Default.Announcement
        ),
//        NavigationItem(
//            route = AppScreen.API_TEST.route,
//            title = "API测试",
//            icon = Icons.Default.Api
//        ),
        NavigationItem(
            route = AppScreen.ADMIN.route,
            title = "后台管理",
            icon = Icons.Default.AdminPanelSettings
        ),
        NavigationItem(
            route = AppScreen.PROFILE.route,
            title = "个人",
            icon = Icons.Default.Person
        ),
    )
}