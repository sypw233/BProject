package ovo.sypw.bsp.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
 * 认证相关的ViewModel
 * 管理用户登录状态和认证操作
 */
class AuthViewModel(
    private val tokenStorage: TokenStorage,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
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
     */
    fun login(
        username: String,
        password: String,
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = loginUseCase(username, password)) {
                is NetworkResult.Success -> {
                    _isLoggedIn.value = true
                    _loginResult.value = result
                    _errorMessage.value = null
                }

                is NetworkResult.Error -> {
                    _isLoggedIn.value = false
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
     */
    fun register(
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = registerUseCase(username, password)) {
                is NetworkResult.Success -> {
                    _isLoggedIn.value = true
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
            _errorMessage.value = null
            _isLoggedIn.value = false
            logoutUseCase()

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
                    Logger.d(result.data.toString())
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
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param confirmPassword 确认新密码
     */
    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = changePasswordUseCase(oldPassword, newPassword, confirmPassword)) {
                is NetworkResult.Success -> {
                    _errorMessage.value = null
                    onSuccess()
                }

                is NetworkResult.Error -> {
                    val errorMsg = result.message ?: "修改密码失败"
                    _errorMessage.value = errorMsg
                    onError(errorMsg)
                }

                is NetworkResult.Loading -> {
                    // 处理加载状态
                }

                NetworkResult.Idle -> {
                    // 处理空闲状态
                }
            }
            _isLoading.value = false
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