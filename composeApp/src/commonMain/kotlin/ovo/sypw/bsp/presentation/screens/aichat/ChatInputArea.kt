package ovo.sypw.bsp.presentation.screens.aichat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.presentation.viewmodel.AIChatViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig

/**
 * 聊天输入区域组件
 * 支持流式传输的实时输入体验
 */
@Composable
fun ChatInputArea(
    modifier: Modifier = Modifier,
    viewModel: AIChatViewModel,
    layoutConfig: ResponsiveLayoutConfig
) {
    val inputMessage by viewModel.inputMessage.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val availableModels by viewModel.availableModels.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val isLoadingModels by viewModel.isLoadingModels.collectAsState()
    
    var isModelDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp) // 减少内边距从16dp到12dp
        ) {
            // 模型选择区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp), // 减少底部间距从12dp到8dp
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI模型:",
                    style = MaterialTheme.typography.bodySmall, // 缩小字体从bodyMedium到bodySmall
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                // 模型选择下拉菜单
                OutlinedButton(
                    onClick = { 
                        if (!isLoadingModels && availableModels.isNotEmpty()) {
                            isModelDropdownExpanded = true
                        }
                    },
                    modifier = Modifier.width(140.dp), // 缩小宽度从160dp到140dp
                    enabled = !isLoadingModels && availableModels.isNotEmpty()
                ) {
                    if (isLoadingModels) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp), // 缩小从16dp到14dp
                                strokeWidth = 1.5.dp // 缩小线宽从2dp到1.5dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = selectedModel,
                                    style = MaterialTheme.typography.labelSmall, // 缩小字体从bodySmall到labelSmall
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "选择模型",
                                    modifier = Modifier.size(14.dp) // 缩小从16dp到14dp
                                )
                            }
                        }
                }
                
                // 下拉菜单
                DropdownMenu(
                    expanded = isModelDropdownExpanded,
                    onDismissRequest = { isModelDropdownExpanded = false }
                ) {
                    availableModels.forEach { model ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = model,
                                    style = MaterialTheme.typography.bodySmall // 缩小字体从bodyMedium到bodySmall
                                )
                            },
                            onClick = {
                                viewModel.updateSelectedModel(model)
                                isModelDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            
            // 输入框和发送按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp), // 减少顶部间距从8dp到4dp
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = viewModel::updateInputMessage,
                    modifier = Modifier.weight(1f),
                    maxLines = 2, // 减少最大行数从4行到2行
                    minLines = 1, // 设置最小行数为1行
                    shape = RoundedCornerShape(6.dp), // 进一步减小圆角从8dp到6dp
                    textStyle = MaterialTheme.typography.bodySmall, // 设置输入文字大小
                    placeholder = { 
                        Text(
                            "输入您的消息...",
                            style = MaterialTheme.typography.bodySmall // 缩小占位符文字
                        ) 
                    }
                )

                // 发送按钮
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank() && !isStreaming) {
                            viewModel.sendMessage()
                        }
                    },
                    enabled = inputMessage.isNotBlank() && !isStreaming,
                    modifier = Modifier
                        .padding(start = 6.dp) // 减少左边距从8dp到6dp
                        .size(36.dp) // 设置按钮整体大小
                ) {
                    if (isStreaming) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp), // 缩小从20dp到16dp
                            strokeWidth = 1.5.dp // 缩小线宽从2dp到1.5dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "发送消息",
                            modifier = Modifier.size(18.dp), // 设置图标大小
                            tint = if (inputMessage.isNotBlank()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            }
                        )
                    }
                }
            }

        }
    }
}