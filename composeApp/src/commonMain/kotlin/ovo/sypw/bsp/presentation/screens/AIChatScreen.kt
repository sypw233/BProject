package ovo.sypw.bsp.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ovo.sypw.bsp.presentation.screens.aichat.ChatInputArea
import ovo.sypw.bsp.presentation.screens.aichat.ChatMessageList
import ovo.sypw.bsp.presentation.screens.aichat.ChatSessionSidebar
import ovo.sypw.bsp.presentation.viewmodel.AIChatViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * AI对话主界面
 * 根据屏幕尺寸自适应布局
 */
@Composable
fun AIChatScreen(
    modifier: Modifier = Modifier,
    layoutConfig: ResponsiveLayoutConfig,
    viewModel: AIChatViewModel = koinViewModel()
) {
    val layoutConfig = ResponsiveLayoutConfig(
        screenSize = ResponsiveUtils.ScreenSize.MEDIUM,
        screenSizeOrigin = 600.dp,
        screenPadding = 16.dp,
        contentPadding = 12.dp,
        cardPadding = 8.dp,
        verticalSpacing = 8.dp,
        horizontalSpacing = 8.dp,
        columnCount = 1,
        useFullWidthButtons = true,
        useScrollableTabs = false
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.collectAsState()

    // 显示错误消息
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (layoutConfig.screenSize) {
            ResponsiveUtils.ScreenSize.COMPACT -> {
                // 小屏：单列布局，不显示侧边栏
                CompactChatLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
            }
            ResponsiveUtils.ScreenSize.MEDIUM -> {
                // 中屏：双列布局，侧边栏较窄
                MediumChatLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
            }
            ResponsiveUtils.ScreenSize.EXPANDED -> {
                // 大屏：双列布局，侧边栏较宽
                ExpandedChatLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
            }
        }
    }
}

/**
 * 小屏布局（手机）
 */
@Composable
private fun CompactChatLayout(
    modifier: Modifier = Modifier,
    viewModel: AIChatViewModel,
    layoutConfig: ResponsiveLayoutConfig
) {
    Column(
        modifier = modifier.padding(layoutConfig.screenPadding)
    ) {
        // 标题栏
        Text(
            text = "AI 对话助手",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 消息列表
        ChatMessageList(
            modifier = Modifier.weight(1f),
            viewModel = viewModel,
            layoutConfig = layoutConfig
        )

        // 输入区域
        ChatInputArea(
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel,
            layoutConfig = layoutConfig
        )
    }
}

/**
 * 中屏布局（平板）
 */
@Composable
private fun MediumChatLayout(
    modifier: Modifier = Modifier,
    viewModel: AIChatViewModel,
    layoutConfig: ResponsiveLayoutConfig
) {
    Row(
        modifier = modifier.padding(layoutConfig.screenPadding)
    ) {
        // 左侧会话列表
        ChatSessionSidebar(
            modifier = Modifier.fillMaxWidth(0.3f),
            viewModel = viewModel,
            layoutConfig = layoutConfig,
            isExpanded = false
        )

        // 右侧对话区域
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            // 标题栏
            Text(
                text = "AI 对话助手",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 消息列表
            ChatMessageList(
                modifier = Modifier.weight(1f),
                viewModel = viewModel,
                layoutConfig = layoutConfig
            )

            // 输入区域
            ChatInputArea(
                modifier = Modifier.fillMaxWidth(),
                viewModel = viewModel,
                layoutConfig = layoutConfig
            )
        }
    }
}

/**
 * 大屏布局（桌面）
 */
@Composable
private fun ExpandedChatLayout(
    modifier: Modifier = Modifier,
    viewModel: AIChatViewModel,
    layoutConfig: ResponsiveLayoutConfig
) {
    Row(
        modifier = modifier.padding(layoutConfig.screenPadding)
    ) {
        // 左侧会话列表
        ChatSessionSidebar(
            modifier = Modifier.fillMaxWidth(0.25f),
            viewModel = viewModel,
            layoutConfig = layoutConfig,
            isExpanded = true
        )

        // 右侧对话区域
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp)
        ) {
            // 标题栏
            Text(
                text = "AI 对话助手",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 消息列表
            ChatMessageList(
                modifier = Modifier.weight(1f),
                viewModel = viewModel,
                layoutConfig = layoutConfig
            )

            // 输入区域
            ChatInputArea(
                modifier = Modifier.fillMaxWidth(),
                viewModel = viewModel,
                layoutConfig = layoutConfig
            )
        }
    }
}