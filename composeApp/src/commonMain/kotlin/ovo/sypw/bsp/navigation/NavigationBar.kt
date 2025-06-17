package ovo.sypw.bsp.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.presentation.screens.admin.AdminConfig
import ovo.sypw.bsp.utils.Logger

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
 * 侧边导航栏组件
 * 使用NavigationRail实现侧边导航，支持缩小功能和子项显示
 * @param navigationManager 导航管理器
 * @param modifier 修饰符
 * @param isExpanded 是否展开状态
 * @param onExpandToggle 展开/收起切换回调
 * @param adminTabIndex 后台管理当前选中的Tab索引（-1表示未选中后台管理）
 * @param onAdminTabSelected 后台管理Tab选择回调
 */
@Composable
fun SideNavigationBar(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onExpandToggle: (() -> Unit)? = null,
    adminTabIndex: Int = -1,
    onAdminTabSelected: ((Int) -> Unit)? = null
) {
    val currentScreen by navigationManager.currentScreen
    val navigationItems = getNavigationItems()
    var adminExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(if (isExpanded) 240.dp else 80.dp)
            .animateContentSize(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            // 顶部折叠/展开按钮（如果提供了回调）
            onExpandToggle?.let { toggle ->
                IconButton(
                    onClick = toggle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isExpanded) 16.dp else 8.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.AutoMirrored.Filled.MenuOpen else Icons.Default.Menu,
                        contentDescription = if (isExpanded) "收起导航栏" else "展开导航栏"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 导航项目
            navigationItems.forEach { item ->
                if (item.route == AppScreen.ADMIN.route) {
                    // 后台管理项目 - 支持子项展开
                    AdminNavigationItem(
                        selected = currentScreen == item.route,
                        onClick = {
                            navigationManager.navigateTo(item.route)
                            if (isExpanded) {
                                adminExpanded = !adminExpanded
                            }
                        },
                        icon = item.icon,
                        label = item.title,
                        isExpanded = isExpanded,
                        hasSubItems = true,
                        subItemsExpanded = adminExpanded && currentScreen == item.route
                    )

                    // 后台管理子项（仅在展开且选中后台管理时显示）
                    if (isExpanded && adminExpanded && currentScreen == item.route) {
                        AdminConfig.adminTabs.forEach { tab ->
                            AdminSubNavigationItem(
                                selected = adminTabIndex == tab.index,
                                onClick = {
                                    onAdminTabSelected?.invoke(tab.index)
                                    Logger.d("Now click Tab: ${tab.title} ${tab.index}")
                                },
                                icon = tab.icon,
                                label = tab.title,
                                isExpanded = isExpanded
                            )
                        }
                    }
                } else {
                    // 普通导航项目
                    AdminNavigationItem(
                        selected = currentScreen == item.route,
                        onClick = { navigationManager.navigateTo(item.route) },
                        icon = item.icon,
                        label = item.title,
                        isExpanded = isExpanded,
                        hasSubItems = false,
                        subItemsExpanded = false
                    )
                }
            }
        }
    }
}

/**
 * 导航项组件
 */
@Composable
private fun AdminNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    isExpanded: Boolean,
    hasSubItems: Boolean = false,
    subItemsExpanded: Boolean = false
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isExpanded) {
            // 展开模式：图标在左，文字在右
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )

                // 如果有子项，显示展开/收起图标
                if (hasSubItems) {
                    Icon(
                        imageVector = if (subItemsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (subItemsExpanded) "收起" else "展开",
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            // 收起模式：仅显示图标
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * 子导航项组件
 */
@Composable
private fun AdminSubNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    isExpanded: Boolean
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp)) // 缩进
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }
        }
    }
}
