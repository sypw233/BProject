package ovo.sypw.bsp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.presentation.viewmodel.AuthViewModel

/**
 * 注册界面
 * @param authViewModel 认证ViewModel
 * @param onRegisterSuccess 注册成功回调
 * @param onNavigateToLogin 导航到登录页面回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()
    val formState by authViewModel.registerFormState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // 监听注册状态变化
    LaunchedEffect(registerState) {
        if (registerState is NetworkResult.Success) {
            onRegisterSuccess()
            authViewModel.resetRegisterState()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 标题
        Text(
            text = "创建账号",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "请填写以下信息完成注册",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // 用户名输入框
        OutlinedTextField(
            value = formState.username,
            onValueChange = authViewModel::updateRegisterUsername,
            label = { Text("用户名") },
            placeholder = { Text("请输入用户名（至少3位）") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            supportingText = {
                Text(
                    text = "${formState.username.length}/20",
                    color = if (formState.username.length >= 3) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        )
        
        // 密码输入框
        OutlinedTextField(
            value = formState.password,
            onValueChange = authViewModel::updateRegisterPassword,
            label = { Text("密码") },
            placeholder = { Text("请输入密码（至少6位）") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            androidx.compose.material.icons.Icons.Default.Visibility
                        } else {
                            androidx.compose.material.icons.Icons.Default.VisibilityOff
                        },
                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            supportingText = {
                Text(
                    text = "密码强度: ${getPasswordStrength(formState.password)}",
                    color = getPasswordStrengthColor(formState.password)
                )
            }
        )
        
        // 确认密码输入框
        OutlinedTextField(
            value = formState.confirmPassword,
            onValueChange = authViewModel::updateRegisterConfirmPassword,
            label = { Text("确认密码") },
            placeholder = { Text("请再次输入密码") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) {
                            androidx.compose.material.icons.Icons.Default.Visibility
                        } else {
                            androidx.compose.material.icons.Icons.Default.VisibilityOff
                        },
                        contentDescription = if (confirmPasswordVisible) "隐藏密码" else "显示密码"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = formState.confirmPassword.isNotEmpty() && formState.password != formState.confirmPassword,
            supportingText = {
                if (formState.confirmPassword.isNotEmpty() && formState.password != formState.confirmPassword) {
                    Text(
                        text = "密码不一致",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        
        // 注册按钮
        Button(
            onClick = authViewModel::register,
            enabled = formState.isValid && registerState !is NetworkResult.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (registerState is NetworkResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "注册",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // 错误信息显示
        if (registerState is NetworkResult.Error) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = (registerState as NetworkResult.Error).exception.message ?: "注册失败",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // 登录链接
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "已有账号？",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            TextButton(
                onClick = onNavigateToLogin
            ) {
                Text(
                    text = "立即登录",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 获取密码强度描述
 */
@Composable
private fun getPasswordStrength(password: String): String {
    return when {
        password.length < 6 -> "弱"
        password.length < 8 -> "中等"
        password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() } -> "强"
        else -> "中等"
    }
}

/**
 * 获取密码强度颜色
 */
@Composable
private fun getPasswordStrengthColor(password: String): androidx.compose.ui.graphics.Color {
    return when (getPasswordStrength(password)) {
        "弱" -> MaterialTheme.colorScheme.error
        "中等" -> MaterialTheme.colorScheme.tertiary
        "强" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}