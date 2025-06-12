package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.LoginResponse
import ovo.sypw.bsp.data.dto.UserInfo
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.*

/**
 * 认证相关的ViewModel
 * 管理用户登录状态和认证操作
 */
class AuthViewModel(
    private val tokenStorage: TokenStorage,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase
) : ViewModel() {
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _loginResult = MutableStateFlow<NetworkResult<LoginResponse>?>(null)
    val loginResult: StateFlow<NetworkResult<LoginResponse>?> = _loginResult.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    /**
     * 检查登录状态
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val hasToken = tokenStorage.hasValidToken()
                _isLoggedIn.value = hasToken
                
                if (hasToken) {
                    // 获取用户信息
                    refreshUserInfo()
                }
            } catch (e: Exception) {
                _isLoggedIn.value = false
                _userInfo.value = null
                _errorMessage.value = "检查登录状态失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 用户登录
     * @param username 用户名或邮箱
     * @param password 密码
     * @param rememberMe 是否记住登录状态
     */
    fun login(
        username: String,
        password: String,
        rememberMe: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = loginUseCase(username, password, rememberMe)) {
                is NetworkResult.Success -> {
                    _isLoggedIn.value = true
                    _userInfo.value = result.data.user
                    _loginResult.value = result
                    _errorMessage.value = null
                }
                is NetworkResult.Error -> {
                    _isLoggedIn.value = false
                    _userInfo.value = null
                    _errorMessage.value = result.message
                    _loginResult.value = result
                }
                is NetworkResult.Loading -> {
                    _loginResult.value = result
                }

                NetworkResult.Idle -> TODO()
            }
            _isLoading.value = false
        }
    }
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param email 邮箱（可选）
     * @param phone 手机号（可选）
     * @param nickname 昵称（可选）
     */
    fun register(
        username: String,
        password: String,
        confirmPassword: String,
        email: String? = null,
        phone: String? = null,
        nickname: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = registerUseCase(username, password, confirmPassword, email, phone, nickname)) {
                is NetworkResult.Success -> {
                    _isLoggedIn.value = true
                    _userInfo.value = result.data.user
                    _loginResult.value = result
                    _errorMessage.value = null
                }
                is NetworkResult.Error -> {
                    _isLoggedIn.value = false
                    _userInfo.value = null
                    _errorMessage.value = result.message
                    _loginResult.value = result
                }
                is NetworkResult.Loading -> {
                    _loginResult.value = result
                }

                NetworkResult.Idle -> TODO()
            }
            _isLoading.value = false
        }
    }
    
    /**
     * 用户登出
     */
    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = logoutUseCase()) {
                is NetworkResult.Success -> {
                    _isLoggedIn.value = false
                    _userInfo.value = null
                    _loginResult.value = null
                    _errorMessage.value = null
                }
                is NetworkResult.Error -> {
                    // 即使登出失败，也更新本地状态
                    _isLoggedIn.value = false
                    _userInfo.value = null
                    _loginResult.value = null
                    _errorMessage.value = result.message
                }
                is NetworkResult.Loading -> {
                    // 处理加载状态
                }

                NetworkResult.Idle -> TODO()
            }
            _isLoading.value = false
        }
    }
    
    /**
     * 刷新用户信息
     * @param forceRefresh 是否强制从服务器刷新
     */
    fun refreshUserInfo(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = getUserInfoUseCase(forceRefresh)) {
                is NetworkResult.Success -> {
                    _userInfo.value = result.data
                    _errorMessage.value = null
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                }
                is NetworkResult.Loading -> {
                    // 处理加载状态
                }

                NetworkResult.Idle -> TODO()
            }
            _isLoading.value = false
        }
    }
    
    /**
     * 刷新访问令牌
     */
    fun refreshToken() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = refreshTokenUseCase()) {
                is NetworkResult.Success -> {
                    _errorMessage.value = null
                    // 令牌刷新成功，保持登录状态
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                    // 如果是认证错误，可能需要重新登录
                    if (result.message?.contains("登录已过期") == true) {
                        _isLoggedIn.value = false
                        _userInfo.value = null
                        _loginResult.value = null
                    }
                }
                is NetworkResult.Loading -> {
                    // 处理加载状态
                }

                NetworkResult.Idle -> TODO()
            }
            _isLoading.value = false
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * 清除登录结果
     */
    fun clearLoginResult() {
        _loginResult.value = null
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