package ovo.sypw.bsp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * 设置屏幕组件
 * 显示应用的各种设置选项
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var isDarkMode by remember { mutableStateOf(false) }
    var isNotificationEnabled by remember { mutableStateOf(true) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 页面标题
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        // 外观设置
        SettingsSection(title = "外观") {
            SettingsSwitchItem(
                icon = Icons.Default.DarkMode,
                title = "深色模式",
                subtitle = "切换应用主题",
                checked = isDarkMode,
                onCheckedChange = { isDarkMode = it }
            )
        }
        
        // 通知设置
        SettingsSection(title = "通知") {
            SettingsSwitchItem(
                icon = Icons.Default.Notifications,
                title = "推送通知",
                subtitle = "接收应用通知",
                checked = isNotificationEnabled,
                onCheckedChange = { isNotificationEnabled = it }
            )
        }
        
        // 其他设置
        SettingsSection(title = "其他") {
            SettingsClickableItem(
                icon = Icons.Default.Language,
                title = "语言设置",
                subtitle = "选择应用语言",
                onClick = { /* 语言设置逻辑 */ }
            )
            
            SettingsClickableItem(
                icon = Icons.Default.Storage,
                title = "存储管理",
                subtitle = "管理应用数据",
                onClick = { /* 存储管理逻辑 */ }
            )
            
            SettingsClickableItem(
                icon = Icons.Default.Security,
                title = "隐私与安全",
                subtitle = "隐私设置",
                onClick = { /* 隐私设置逻辑 */ }
            )
        }
        
        // 关于设置
        SettingsSection(title = "关于") {
            SettingsClickableItem(
                icon = Icons.Default.Info,
                title = "关于应用",
                subtitle = "版本 1.0.0",
                onClick = { /* 关于页面逻辑 */ }
            )
            
            SettingsClickableItem(
                icon = Icons.Default.Help,
                title = "帮助与反馈",
                subtitle = "获取帮助",
                onClick = { /* 帮助页面逻辑 */ }
            )
        }
    }
}

/**
 * 设置分组组件
 * @param title 分组标题
 * @param content 分组内容
 */
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                content = content
            )
        }
    }
}

/**
 * 带开关的设置项组件
 * @param icon 图标
 * @param title 标题
 * @param subtitle 副标题
 * @param checked 开关状态
 * @param onCheckedChange 开关状态改变回调
 */
@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * 可点击的设置项组件
 * @param icon 图标
 * @param title 标题
 * @param subtitle 副标题
 * @param onClick 点击回调
 */
@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "进入",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}