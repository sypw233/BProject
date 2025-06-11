package ovo.sypw.bsp.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.navigation.AppScreen
import ovo.sypw.bsp.navigation.NavigationManager
import ovo.sypw.bsp.presentation.viewmodel.AuthViewModel
import ovo.sypw.bsp.domain.model.NetworkResult
import org.koin.compose.koinInject
import ovo.sypw.bsp.data.dto.UserInfoDTO

/**
 * 认证演示页面
 * 提供登录注册功能的入口和用户状态显示
 * @param navigationManager 导航管理器
 * @param modifier 修饰符
 */
@Composable
fun AuthDemoScreen(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = koinInject()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val userInfoState by authViewModel.userInfoState.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()
    val registerState by authViewModel.registerState.collectAsState()
    
    // 检查登录状态
    LaunchedEffect(Unit) {
        authViewModel.checkLoginStatus()
        if (isLoggedIn) {
            authViewModel.getCurrentUser()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        Text(
            text = "认证功能演示",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 用户状态卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "用户状态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (isLoggedIn) {
                    Text(
                        text = "✅ 已登录",
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    when (userInfoState) {
                        is NetworkResult.Success -> {
                            val user = (userInfoState as NetworkResult.Success<UserInfoDTO>).data
                            Text(text = "用户名: ${user.username}")
                            Text(text = "用户ID: ${user.id}")
                        }
                        is NetworkResult.Loading -> {
                            Text(text = "正在获取用户信息...")
                        }
                        is NetworkResult.Error -> {
                            Text(
                                text = "获取用户信息失败: ${(userInfoState as NetworkResult.Error).message}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> {
                            Text(text = "用户信息未加载")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            authViewModel.logout()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("退出登录")
                    }
                } else {
                    Text(
                        text = "❌ 未登录",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        
        // 功能按钮区域
        if (!isLoggedIn) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "认证操作",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Button(
                        onClick = {
                            navigationManager.navigateTo(AppScreen.LOGIN.route)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("前往登录")
                    }
                    
                    OutlinedButton(
                        onClick = {
                            navigationManager.navigateTo(AppScreen.REGISTER.route)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("前往注册")
                    }
                }
            }
        }
        
        // API信息卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "API信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "登录接口: POST /auth/login",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "注册接口: POST /auth/register",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "用户信息: GET /auth/current",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        // 错误信息显示
        when {
            loginState is NetworkResult.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "登录错误: ${(loginState as NetworkResult.Error).message}",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            registerState is NetworkResult.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "注册错误: ${(registerState as NetworkResult.Error).message}",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            userInfoState is NetworkResult.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "用户信息错误: ${(userInfoState as NetworkResult.Error).message}",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
        
        // 加载状态
        if (loginState is NetworkResult.Loading ||
            registerState is NetworkResult.Loading ||
            userInfoState is NetworkResult.Loading) {
            CircularProgressIndicator()
        }
    }
}