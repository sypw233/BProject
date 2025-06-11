package ovo.sypw.bsp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.getPlatform

/**
 * 底部导航栏组件（移动端使用）
 * @param navigationManager 导航管理器
 * @param modifier 修饰符
 */
@Composable
fun BottomNavigationBar(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier
) {
    val currentScreen by navigationManager.currentScreen
    val navigationItems = getNavigationItems()
    
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        navigationItems.forEach { item ->
            NavigationBarItem(
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

/**
 * 侧边导航栏组件（桌面端和网页端使用）
 * 使用NavigationRail实现侧边导航
 * @param navigationManager 导航管理器
 * @param modifier 修饰符
 */
@Composable
fun SideNavigationBar(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier
) {
    val currentScreen by navigationManager.currentScreen
    val navigationItems = getNavigationItems()
    
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 导航项目
            navigationItems.forEach { item ->
                NavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = currentScreen == item.route,
                    onClick = { navigationManager.navigateTo(item.route) }
                )
            }
        }
    }
}



/**
 * 根据平台自动选择导航栏类型
 * @param navigationManager 导航管理器
 * @param modifier 修饰符
 */
@Composable
fun AdaptiveNavigationBar(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier
) {
    val platform = getPlatform()
    
    when {
        platform.name.contains("Desktop") || platform.name.contains("JS") -> {
            // 桌面端和网页端使用侧边导航
            SideNavigationBar(
                navigationManager = navigationManager,
                modifier = modifier
            )
        }
        else -> {
            // 移动端使用底部导航
            BottomNavigationBar(
                navigationManager = navigationManager,
                modifier = modifier
            )
        }
    }
}