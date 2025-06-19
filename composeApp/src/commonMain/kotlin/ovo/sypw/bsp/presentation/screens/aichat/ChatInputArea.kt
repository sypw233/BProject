package ovo.sypw.bsp.presentation.screens.aichat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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


    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 输入框和发送按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = inputMessage,
                    onValueChange = viewModel::updateInputMessage,
                    modifier = Modifier.weight(1f),
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )

                // 发送按钮
                IconButton(
                    onClick = {
                        if (inputMessage.isNotBlank() && !isStreaming) {
                            viewModel.sendMessage()
                        }
                    },
                    enabled = inputMessage.isNotBlank() && !isStreaming,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (isStreaming) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "发送消息",
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