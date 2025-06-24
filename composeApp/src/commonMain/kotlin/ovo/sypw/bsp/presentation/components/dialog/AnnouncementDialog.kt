package ovo.sypw.bsp.presentation.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import ovo.sypw.bsp.data.dto.AnnouncementPriority
import ovo.sypw.bsp.data.dto.AnnouncementStatus
import ovo.sypw.bsp.data.dto.AnnouncementType
import ovo.sypw.bsp.presentation.viewmodel.admin.AnnouncementDialogState
import ovo.sypw.bsp.presentation.viewmodel.admin.AnnouncementViewModel

/**
 * 公告添加/编辑Dialog组件
 * 提供公告信息的添加和编辑功能
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalRichTextApi::class)
@Composable
fun AnnouncementDialog(
    announcementViewModel: AnnouncementViewModel,
    dialogState: AnnouncementDialogState,
    onDismiss: () -> Unit
) {
    if (dialogState.isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // 标题栏
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (dialogState.isEditMode) "编辑公告" else "添加公告",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = onDismiss
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 错误消息显示
                    if (dialogState.errorMessage != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = dialogState.errorMessage,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 表单内容
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 公告标题输入
                        OutlinedTextField(
                            value = dialogState.title,
                            onValueChange = announcementViewModel::updateAnnouncementTitle,
                            label = { Text("公告标题") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("请输入公告标题") }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 公告内容富文本编辑器
                        val richTextState = rememberRichTextState()

                        // 同步富文本状态与对话框状态
                        LaunchedEffect(dialogState.content) {
                            if (richTextState.annotatedString.text != dialogState.content) {
                                richTextState.setHtml(dialogState.content)
                            }
                        }

                        LaunchedEffect(richTextState.annotatedString) {
                            val htmlContent = richTextState.toHtml()
                            if (htmlContent != dialogState.content) {
                                announcementViewModel.updateAnnouncementContent(htmlContent)
                            }
                        }

                        Text(
                            text = "公告内容",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // 富文本编辑工具栏 - 完全复制自RichEditorDemo
                        RichTextStyleRow(
                            state = richTextState,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 富文本编辑器
                        RichTextEditor(
                            state = richTextState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            placeholder = { Text("请输入公告内容，可以选中文本进行格式化") },
                            colors = RichTextEditorDefaults.richTextEditorColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Default,
                                keyboardType = KeyboardType.Text
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 公告类型选择
                        Text(
                            text = "公告类型",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.type == AnnouncementType.NOTIFICATION.value,
                                    onClick = {
                                        announcementViewModel.updateAnnouncementType(
                                            AnnouncementType.NOTIFICATION.value
                                        )
                                    }
                                )
                                Text(AnnouncementType.NOTIFICATION.displayName)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.type == AnnouncementType.ACTIVITY.value,
                                    onClick = {
                                        announcementViewModel.updateAnnouncementType(
                                            AnnouncementType.ACTIVITY.value
                                        )
                                    }
                                )
                                Text(AnnouncementType.ACTIVITY.displayName)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.type == AnnouncementType.SYSTEM.value,
                                    onClick = {
                                        announcementViewModel.updateAnnouncementType(
                                            AnnouncementType.SYSTEM.value
                                        )
                                    }
                                )
                                Text(AnnouncementType.SYSTEM.displayName)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 公告状态选择
                        Text(
                            text = "公告状态",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.status == AnnouncementStatus.DRAFT.value,
                                    onClick = {
                                        announcementViewModel.updateAnnouncementStatus(
                                            AnnouncementStatus.DRAFT.value
                                        )
                                    }
                                )
                                Text(AnnouncementStatus.DRAFT.displayName)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.status == AnnouncementStatus.PUBLISHED.value,
                                    onClick = {
                                        announcementViewModel.updateAnnouncementStatus(
                                            AnnouncementStatus.PUBLISHED.value
                                        )
                                    }
                                )
                                Text(AnnouncementStatus.PUBLISHED.displayName)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.status == AnnouncementStatus.OFFLINE.value,
                                    onClick = {
                                        announcementViewModel.updateAnnouncementStatus(
                                            AnnouncementStatus.OFFLINE.value
                                        )
                                    }
                                )
                                Text(AnnouncementStatus.OFFLINE.displayName)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 优先级选择
                        Text(
                            text = "优先级",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.priority == AnnouncementPriority.NORMAL.value,
                                    onClick = {
                                        announcementViewModel.updateAnnouncementPriority(
                                            AnnouncementPriority.NORMAL.value
                                        )
                                    }
                                )
                                Text(AnnouncementPriority.NORMAL.displayName)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.priority == AnnouncementPriority.IMPORTANT.value,
                                    onClick = {
                                        announcementViewModel.updateAnnouncementPriority(
                                            AnnouncementPriority.IMPORTANT.value
                                        )
                                    }
                                )
                                Text(AnnouncementPriority.IMPORTANT.displayName)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = dialogState.priority == AnnouncementPriority.URGENT.value,
                                    onClick = {
                                        announcementViewModel.updateAnnouncementPriority(
                                            AnnouncementPriority.URGENT.value
                                        )
                                    }
                                )
                                Text(AnnouncementPriority.URGENT.displayName)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 提示信息
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "提示：",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• 公告创建后默认为草稿状态\n• 需要手动发布才能对外显示\n• 优先级越高的公告显示越靠前",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 操作按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss
                        ) {
                            Text("取消")
                        }

                        Button(
                            onClick = announcementViewModel::saveAnnouncement,
                            enabled = dialogState.title.isNotBlank() && dialogState.content.isNotBlank()
                        ) {
                            Text(if (dialogState.isEditMode) "更新" else "添加")
                        }
                    }
                }
            }
        }
    }
}