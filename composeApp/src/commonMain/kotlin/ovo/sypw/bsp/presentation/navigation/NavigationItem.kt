package ovo.sypw.bsp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Cloud
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
    //    HOME("home"),
    ANNOUNCEMENTS("announcements"),
    ANNOUNCEMENT_DETAIL("announcement_detail"),
    AI_CHAT("ai_chat"),
    NETDISK("netdisk"),
    PROFILE("profile"),

    //    API_TEST("api_test"),
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
            route = AppScreen.ANNOUNCEMENTS.route,
            title = "公告",
            icon = Icons.AutoMirrored.Filled.Announcement
        ),
        NavigationItem(
            route = AppScreen.AI_CHAT.route,
            title = "AI对话",
            icon = Icons.AutoMirrored.Filled.Chat
        ),
        NavigationItem(
            route = AppScreen.ADMIN.route,
            title = "后台管理",
            icon = Icons.Default.AdminPanelSettings
        ),
        NavigationItem(
            route = AppScreen.NETDISK.route,
            title = "网盘管理",
            icon = Icons.Default.Cloud
        ),
//        NavigationItem(
//            route = AppScreen.API_TEST.route,
//            title = "API测试",
//            icon = Icons.Default.Api
//        ),

        NavigationItem(
            route = AppScreen.PROFILE.route,
            title = "个人",
            icon = Icons.Default.Person
        ),
    )
}