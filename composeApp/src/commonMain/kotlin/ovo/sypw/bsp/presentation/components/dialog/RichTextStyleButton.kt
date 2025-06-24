package ovo.sypw.bsp.presentation.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 富文本编辑器样式按钮组件
 * 提供统一的按钮样式和交互行为
 */
@Composable
fun RichTextStyleButton(
    onClick: () -> Unit,
    icon: ImageVector,
    tint: Color? = null,
    isSelected: Boolean = false,
) {
    IconButton(
        modifier = Modifier
            // 防止富文本编辑器在点击按钮时失去焦点的解决方案
            // (主要在桌面端会出现这个问题)
            .focusProperties { canFocus = false },
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onBackground
            },
        ),
    ) {
        Icon(
            icon,
            contentDescription = icon.name,
            tint = tint ?: LocalContentColor.current,
            modifier = Modifier
                .background(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    shape = CircleShape
                )
        )
    }
}