package ovo.sypw.bsp.presentation.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatAlignLeft
import androidx.compose.material.icons.automirrored.outlined.FormatAlignRight
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FormatAlignCenter
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.TextDecrease
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.RichTextState

/**
 * 富文本编辑器样式工具栏组件
 * 提供文本对齐、样式、列表等格式化功能
 */
@OptIn(ExperimentalRichTextApi::class)
@Composable
fun RichTextStyleRow(
    modifier: Modifier = Modifier,
    state: RichTextState,
) {
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // 左对齐按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.addParagraphStyle(
                        ParagraphStyle(
                            textAlign = TextAlign.Left,
                        )
                    )
                },
                isSelected = state.currentParagraphStyle.textAlign == TextAlign.Left,
                icon = Icons.AutoMirrored.Outlined.FormatAlignLeft
            )
        }

        // 居中对齐按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.addParagraphStyle(
                        ParagraphStyle(
                            textAlign = TextAlign.Center
                        )
                    )
                },
                isSelected = state.currentParagraphStyle.textAlign == TextAlign.Center,
                icon = Icons.Outlined.FormatAlignCenter
            )
        }

        // 右对齐按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.addParagraphStyle(
                        ParagraphStyle(
                            textAlign = TextAlign.Right
                        )
                    )
                },
                isSelected = state.currentParagraphStyle.textAlign == TextAlign.Right,
                icon = Icons.AutoMirrored.Outlined.FormatAlignRight
            )
        }

        // 加粗按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = Icons.Outlined.FormatBold
            )
        }

        // 斜体按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
                icon = Icons.Outlined.FormatItalic
            )
        }

        // 下划线按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                },
                isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
                icon = Icons.Outlined.FormatUnderlined
            )
        }

        // 删除线按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                },
                isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
                icon = Icons.Outlined.FormatStrikethrough
            )
        }

        // 大字号按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontSize = 28.sp
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontSize == 28.sp,
                icon = Icons.Outlined.FormatSize
            )
        }

        // 红色文字按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            color = Color.Red
                        )
                    )
                },
                isSelected = state.currentSpanStyle.color == Color.Red,
                icon = Icons.Filled.Circle,
                tint = Color.Red
            )
        }

        // 黄色背景按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            background = Color.Yellow
                        )
                    )
                },
                isSelected = state.currentSpanStyle.background == Color.Yellow,
                icon = Icons.Outlined.Circle,
                tint = Color.Yellow
            )
        }

        // 分隔线
        item {
            Box(
                Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .background(Color(0xFF393B3D))
            )
        }

        // 无序列表按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleUnorderedList()
                },
                isSelected = state.isUnorderedList,
                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
            )
        }

        // 有序列表按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleOrderedList()
                },
                isSelected = state.isOrderedList,
                icon = Icons.Outlined.FormatListNumbered,
            )
        }

        // 增加列表层级按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.increaseListLevel()
                },
                isSelected = false, // 这个按钮不需要选中状态
                icon = Icons.Outlined.TextIncrease,
            )
        }

        // 减少列表层级按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.decreaseListLevel()
                },
                isSelected = false, // 这个按钮不需要选中状态
                icon = Icons.Outlined.TextDecrease,
            )
        }

        // 分隔线
        item {
            Box(
                Modifier
                    .height(24.dp)
                    .width(1.dp)
                    .background(Color(0xFF393B3D))
            )
        }

        // 代码块按钮
        item {
            RichTextStyleButton(
                onClick = {
                    state.toggleCodeSpan()
                },
                isSelected = state.isCodeSpan,
                icon = Icons.Outlined.Code,
            )
        }
    }
}