package ovo.sypw.bsp.presentation.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ovo.sypw.bsp.presentation.components.ResponsiveListLayout
import ovo.sypw.bsp.presentation.screens.aichat.ChatInputArea
import ovo.sypw.bsp.presentation.screens.aichat.ChatMessageList
import ovo.sypw.bsp.presentation.screens.aichat.ChatSessionSidebar
import ovo.sypw.bsp.presentation.viewmodel.AIChatViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * AI聊天界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    modifier: Modifier = Modifier,
    layoutConfig: ResponsiveLayoutConfig,
    viewModel: AIChatViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 错误消息处理
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearErrorMessage()
        }
    }

    when (layoutConfig.screenSize) {
        ResponsiveUtils.ScreenSize.COMPACT -> {
            // 小屏使用Navigation Drawer
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ChatSessionSidebar(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        viewModel = viewModel,
                        layoutConfig = layoutConfig,
                        isExpanded = true,
                        onCloseDrawer = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                },
                modifier = modifier.fillMaxSize()
            ) {
                CompactChatLayoutWithDrawer(
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel,
                    layoutConfig = layoutConfig,
                    snackbarHostState = snackbarHostState,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        }
        ResponsiveUtils.ScreenSize.MEDIUM -> {
            // 中屏使用传统侧边栏
            Scaffold(
                modifier = modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
                MediumChatLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    viewModel = viewModel,
                    layoutConfig = layoutConfig
                )
            }
        }
        ResponsiveUtils.ScreenSize.EXPANDED -> {
            // 大屏使用传统侧边栏
            Scaffold(
                modifier = modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { paddingValues ->
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
 * 小屏布局（手机）- 带Navigation Drawer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactChatLayoutWithDrawer(
    modifier: Modifier = Modifier,
    viewModel: AIChatViewModel,
    layoutConfig: ResponsiveLayoutConfig,
    snackbarHostState: SnackbarHostState,
    onOpenDrawer: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("AI 对话助手") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "打开菜单"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(layoutConfig.screenPadding)
        ) {
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
 * 小屏布局（手机）- 传统布局
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
    // 侧边栏折叠状态管理
    var isSidebarExpanded by remember { mutableStateOf(true) }
    
    Row(
        modifier = modifier.padding(layoutConfig.screenPadding)
    ) {
        // 左侧会话列表 - 添加动画和折叠功能
        ChatSessionSidebar(
            modifier = Modifier
                .width(if (isSidebarExpanded) 320.dp else 80.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            viewModel = viewModel,
            layoutConfig = layoutConfig,
            isExpanded = isSidebarExpanded,
            onExpandToggle = { isSidebarExpanded = !isSidebarExpanded }
        )

        // 右侧对话区域
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (isSidebarExpanded) 24.dp else 16.dp)
                .animateContentSize()
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