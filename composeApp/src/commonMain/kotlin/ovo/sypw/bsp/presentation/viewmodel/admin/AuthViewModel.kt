package ovo.sypw.bsp.presentation.viewmodel.admin

import com.hoc081098.kmp.viewmodel.ViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import ovo.sypw.bsp.data.dto.LoginResponse
import ovo.sypw.bsp.data.dto.UserInfo
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.usecase.ChangePasswordUseCase
import ovo.sypw.bsp.domain.usecase.GetUserInfoUseCase
import ovo.sypw.bsp.domain.usecase.LoginUseCase
import ovo.sypw.bsp.domain.usecase.LogoutUseCase
import ovo.sypw.bsp.domain.usecase.RegisterUseCase
import ovo.sypw.bsp.utils.Logger

/**
 * 认证状态数据类
 * 封装所有认证相关的UI状态
 */
data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val userInfo: UserInfo? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginResult: NetworkResult<LoginResponse>? = null,
    val isLoginFormValid: Boolean = false
)

/**
 * 认证相关的ViewModel
 * 管理用户登录状态和认证操作
 * 重构后采用统一的状态管理和更清晰的错误处理
 */
class AuthViewModel(
    private val tokenStorage: TokenStorage,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
) : ViewModel() {

    // 统一的UI状态管理
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // 向后兼容的属性 - 直接从_uiState派生，避免使用已取消的viewModelScope
    val isLoggedIn: StateFlow<Boolean> = _uiState.map { it.isLoggedIn }
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value.isLoggedIn
        )
    
    val userInfo: StateFlow<UserInfo?> = _uiState.map { it.userInfo }
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value.userInfo
        )
    
    val isLoading: StateFlow<Boolean> = _uiState.map { it.isLoading }
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value.isLoading
        )
    
    val errorMessage: StateFlow<String?> = _uiState.map { it.errorMessage }
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value.errorMessage
        )
    
    val loginResult: StateFlow<NetworkResult<LoginResponse>?> = _uiState.map { it.loginResult }
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value.loginResult
        )

    init {
        checkLoginStatus()
    }

    /**
     * 检查登录状态
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val hasToken = tokenStorage.hasValidToken()
                Logger.d("AuthViewModel", "检查登录状态: hasToken=$hasToken")
                
                updateUiState { it.copy(isLoggedIn = hasToken) }

                if (hasToken) {
                    // 获取用户信息
                    refreshUserInfo()
                } else {
                    // 清除用户信息
                    updateUiState { it.copy(userInfo = null) }
                }
            } catch (e: Exception) {
                Logger.e("AuthViewModel", "检查登录状态失败", e)
                updateUiState { 
                    it.copy(
                        isLoggedIn = false,
                        userInfo = null,
                        errorMessage = "检查登录状态失败: ${e.message}"
                    )
                }
            } finally {
                updateUiState { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * 用户登录
     * 重构后的版本，增强了输入验证、错误处理和状态管理
     * @param username 用户名或邮箱
     * @param password 密码
     * @param rememberMe 是否记住登录状态（可选）
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun login(
        username: String,
        password: String,
        rememberMe: Boolean = false
    ) {
        Logger.i("AuthViewModel", "=== 开始登录流程 ===")
        Logger.d("AuthViewModel", "登录参数 - 用户名: '$username', 密码长度: ${password.length}, 记住我: $rememberMe")
        
        Logger.d("AuthViewModel", "准备启动协程，viewModelScope: ${viewModelScope}")
        
        // 临时使用GlobalScope测试协程是否能正常执行
        GlobalScope.launch {
            Logger.d("AuthViewModel", "GlobalScope协程已启动，开始执行登录逻辑")
            try {
                Logger.d("AuthViewModel", "重置UI状态并开始加载")
                
                // 重置状态并开始加载
                updateUiState { 
                    it.copy(
                        isLoading = true, 
                        errorMessage = null,
                        loginResult = null
                    )
                }
                
                Logger.d("AuthViewModel", "调用 loginUseCase")
                val result = loginUseCase(username, password, rememberMe)
                Logger.d("AuthViewModel", "loginUseCase 返回结果类型: ${result::class.simpleName}")

                when (result) {
                    is NetworkResult.Success -> {
                        Logger.i("AuthViewModel", "登录成功，响应数据: ${result.data}")
                        updateUiState { 
                            it.copy(
                                isLoggedIn = true,
                                loginResult = result,
                                errorMessage = null
                            )
                        }
                        
                        // 登录成功后自动获取用户信息
                        Logger.d("AuthViewModel", "开始获取用户信息")
                        refreshUserInfo(forceRefresh = true)
                    }

                    is NetworkResult.Error -> {
                        Logger.e("AuthViewModel", "登录失败 - 错误信息: ${result.message}", result.exception)
                        updateUiState { 
                            it.copy(
                                isLoggedIn = false,
                                errorMessage = result.message,
                                loginResult = result
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        Logger.d("AuthViewModel", "登录状态为加载中")
                        updateUiState { it.copy(loginResult = result) }
                    }

                    NetworkResult.Idle -> {
                        Logger.d("AuthViewModel", "登录状态为空闲")
                    }
                }
            } catch (e: Exception) {
                Logger.e("AuthViewModel", "登录过程中发生异常: ${e.message}", e)
                updateUiState { 
                    it.copy(
                        isLoggedIn = false,
                        errorMessage = "登录过程中发生错误: ${e.message}",
                        loginResult = NetworkResult.Error(e, e.message ?: "未知错误")
                    )
                }
            } finally {
                Logger.d("AuthViewModel", "=== 登录流程结束 ===")
                updateUiState { it.copy(isLoading = false) }
            }
        }
    }
    
    /**
     * 验证登录表单
     * @param username 用户名
     * @param password 密码
     * @return 是否有效
     */
    fun validateLoginForm(username: String, password: String): Boolean {
        val isValid = username.isNotBlank() && 
                     password.isNotBlank() && 
                     username.length >= 3 && 
                     password.length >= 6
        
        updateUiState { it.copy(isLoginFormValid = isValid) }
        return isValid
    }

    /**
     * 用户注册
     * 重构后的版本，改进了错误处理和状态管理
     * @param username 用户名
     * @param password 密码
     */
    fun register(
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            Logger.d("AuthViewModel", "开始注册: username=$username")
            
            updateUiState { 
                it.copy(
                    isLoading = true, 
                    errorMessage = null,
                    loginResult = null
                )
            }

            try {
                when (val result = registerUseCase(username, password)) {
                    is NetworkResult.Success -> {
                        Logger.i("AuthViewModel", "注册成功")
                        updateUiState { 
                            it.copy(
                                isLoggedIn = true,
                                loginResult = result,
                                errorMessage = null
                            )
                        }
                        
                        // 注册成功后自动获取用户信息
                        refreshUserInfo(forceRefresh = true)
                    }

                    is NetworkResult.Error -> {
                        Logger.w("AuthViewModel", "注册失败: ${result.message}")
                        updateUiState { 
                            it.copy(
                                isLoggedIn = false,
                                userInfo = null,
                                errorMessage = result.message ?: "注册失败，请重试",
                                loginResult = result
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        updateUiState { it.copy(loginResult = result) }
                    }

                    NetworkResult.Idle -> {
                        Logger.d("AuthViewModel", "注册状态为空闲")
                    }
                }
            } catch (e: Exception) {
                Logger.e("AuthViewModel", "注册过程中发生异常", e)
                updateUiState { 
                    it.copy(
                        isLoggedIn = false,
                        userInfo = null,
                        errorMessage = "注册过程中发生错误: ${e.message}",
                        loginResult = NetworkResult.Error(e, e.message ?: "未知错误")
                    )
                }
            } finally {
                updateUiState { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * 登出
     * 重构后的版本，使用统一状态更新和改进的错误处理
     */
    fun logout() {
        viewModelScope.launch {
            Logger.d("AuthViewModel", "开始登出")
            
            updateUiState { 
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            
            when (val result = logoutUseCase()) {
                is NetworkResult.Success -> {
                    Logger.i("AuthViewModel", "登出成功")
                    
                    // 清除所有认证相关状态
                    updateUiState { 
                        AuthUiState() // 重置为初始状态
                    }
                }
                
                is NetworkResult.Error -> {
                    Logger.e("AuthViewModel", "登出失败: ${result.message}")
                    updateUiState { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "登出失败: ${result.message}"
                        )
                    }
                }
                
                is NetworkResult.Loading -> {
                    Logger.d("AuthViewModel", "登出中...")
                }
                
                NetworkResult.Idle -> {
                    Logger.d("AuthViewModel", "登出空闲状态")
                }
            }
        }
    }

    /**
     * 刷新用户信息
     * 重构后的版本，改进了错误处理和日志记录
     * @param forceRefresh 是否强制从服务器刷新
     */
    fun refreshUserInfo(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            Logger.d("AuthViewModel", "刷新用户信息: forceRefresh=$forceRefresh")
            
            updateUiState { 
                it.copy(
                    isLoading = true, 
                    errorMessage = null
                )
            }

            try {
                when (val result = getUserInfoUseCase(forceRefresh)) {
                    is NetworkResult.Success -> {
                        Logger.i("AuthViewModel", "获取用户信息成功: ${result.data}")
                        updateUiState { 
                            it.copy(
                                userInfo = result.data,
                                errorMessage = null
                            )
                        }
                    }

                    is NetworkResult.Error -> {
                        Logger.w("AuthViewModel", "获取用户信息失败: ${result.message}")
                        updateUiState { 
                            it.copy(
                                errorMessage = result.message ?: "获取用户信息失败"
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        Logger.d("AuthViewModel", "正在获取用户信息...")
                        // 加载状态已在上面设置
                    }

                    NetworkResult.Idle -> {
                        Logger.d("AuthViewModel", "用户信息获取状态为空闲")
                    }
                }
            } catch (e: Exception) {
                Logger.e("AuthViewModel", "刷新用户信息过程中发生异常", e)
                updateUiState { 
                    it.copy(
                        errorMessage = "获取用户信息时发生错误: ${e.message}"
                    )
                }
            } finally {
                updateUiState { it.copy(isLoading = false) }
            }
        }
    }


    /**
     * 清除错误信息
     */
    fun clearError() {
        updateUiState { it.copy(errorMessage = null) }
    }

    /**
     * 清除登录结果
     */
    fun clearLoginResult() {
        updateUiState { it.copy(loginResult = null) }
    }
    
    /**
     * 重置所有状态
     * 新增方法，用于完全重置ViewModel状态
     */
    fun resetState() {
        updateUiState { AuthUiState() }
    }
    
    /**
     * 更新UI状态的辅助方法
     * 确保状态更新的一致性
     */
    private fun updateUiState(update: (AuthUiState) -> AuthUiState) {
        _uiState.value = update(_uiState.value)
    }

    /**
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param confirmPassword 确认密码
     * @param onSuccess 成功回调
     * @param onError 错误回调
     */
    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, errorMessage = null) }
            Logger.d("AuthViewModel", "开始修改密码")

            when (val result = changePasswordUseCase(oldPassword, newPassword, confirmPassword)) {
                is NetworkResult.Success -> {
                    updateUiState { it.copy(errorMessage = null) }
                    Logger.d("AuthViewModel", "密码修改成功")
                    onSuccess()
                }

                is NetworkResult.Error -> {
                    val errorMsg = result.message ?: "修改密码失败"
                    updateUiState { it.copy(errorMessage = errorMsg) }
                    Logger.e("AuthViewModel", "密码修改失败: $errorMsg")
                    onError(errorMsg)
                }

                is NetworkResult.Loading -> {
                    // 处理加载状态
                    Logger.d("AuthViewModel", "密码修改中...")
                }

                NetworkResult.Idle -> {
                    // 处理空闲状态
                    Logger.d("AuthViewModel", "密码修改空闲状态")
                }
            }
            updateUiState { it.copy(isLoading = false) }
        }
    }

    /**
     * 获取访问令牌
     * @return 访问令牌，如果不存在则返回null
     */
    suspend fun getAccessToken(): String? {
        return try {
            tokenStorage.getAccessToken()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取刷新令牌
     * @return 刷新令牌，如果不存在则返回null
     */
    suspend fun getRefreshToken(): String? {
        return try {
            tokenStorage.getRefreshToken()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取用户ID
     * @return 用户ID，如果不存在则返回null
     */
    suspend fun getUserId(): String? {
        return try {
            tokenStorage.getUserId()
        } catch (e: Exception) {
            null
        }
    }
}