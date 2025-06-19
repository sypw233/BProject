package ovo.sypw.bsp.presentation.screens.aichat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ovo.sypw.bsp.data.dto.ChatMessage
import ovo.sypw.bsp.presentation.viewmodel.AIChatViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography


/**
 * 聊天消息列表组件
 */
@Composable
fun ChatMessageList(
    modifier: Modifier = Modifier,
    viewModel: AIChatViewModel,
    layoutConfig: ResponsiveLayoutConfig
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val listState = rememberLazyListState()

    // 自动滚动到底部
    LaunchedEffect(messages.size, isStreaming) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = modifier) {
        if (messages.isEmpty() && !isLoading) {
            // 空状态
            EmptyMessageState(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    val isLastMessage = message == messages.lastOrNull()
                    val showStreamingCursor = isLastMessage && message.role == "assistant" && isStreaming
                    
                    MessageItem(
                        message = message,
                        layoutConfig = layoutConfig,
                        isStreaming = showStreamingCursor
                    )
                }
            }
        }

        // 加载指示器
        if (isLoading && messages.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

/**
 * 单个消息项
 * 支持流式传输的实时显示效果
 */
@Composable
private fun MessageItem(
    message: ChatMessage,
    layoutConfig: ResponsiveLayoutConfig,
    isStreaming: Boolean = false
) {
    val isUser = message.role == "user"
    val backgroundColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI头像
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .padding(
                    start = if (isUser) 48.dp else 8.dp,
                    end = if (isUser) 8.dp else 48.dp
                ),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 根据消息角色选择渲染方式
                    if (isUser) {
                        // 用户消息使用普通Text显示
                        Text(
                            text = message.message,
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // AI消息使用Markdown渲染
                         Markdown(
                             content = message.message,
                             modifier = Modifier.weight(1f),
                             typography = markdownTypography(
                                 h1 = MaterialTheme.typography.titleLarge,
                                 h2 = MaterialTheme.typography.titleMedium,
                                 h3 = MaterialTheme.typography.titleSmall,
                                 h4 = MaterialTheme.typography.bodyLarge,
                                 h5 = MaterialTheme.typography.bodyMedium,
                                 h6 = MaterialTheme.typography.bodySmall,
                                 text = MaterialTheme.typography.bodySmall,
                                 code = MaterialTheme.typography.labelSmall,
                                 quote = MaterialTheme.typography.bodySmall
                             )
                         )
                    }
                    
                    // 流式传输时显示动画光标
                    if (isStreaming) {
                        var cursorVisible by remember { mutableStateOf(true) }
                        
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(500)
                                cursorVisible = !cursorVisible
                            }
                        }
                        
                        Text(
                            text = "|",
                            color = textColor.copy(alpha = if (cursorVisible) 0.8f else 0.2f),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }

        if (isUser) {
            // 用户头像
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "用户",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * 空消息状态
 */
@Composable
private fun EmptyMessageState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = "AI助手",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Text(
            text = "开始与AI助手对话",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 16.dp)
        )
        
        Text(
            text = "输入您的问题，AI助手将为您提供帮助",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
