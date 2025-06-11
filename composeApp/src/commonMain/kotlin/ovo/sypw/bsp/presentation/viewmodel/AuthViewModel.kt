package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.AuthResponseDTO
import ovo.sypw.bsp.data.dto.UserInfoDTO
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.AuthUseCase

/**
 * 认证ViewModel
 * 管理登录、注册相关的UI状态和业务逻辑
 */
class AuthViewModel(
    private val authUseCase: AuthUseCase
) : ViewModel() {
    
    // 登录状态
    private val _loginState = MutableStateFlow<NetworkResult<AuthResponseDTO>>(NetworkResult.Idle)
    val loginState: StateFlow<NetworkResult<AuthResponseDTO>> = _loginState.asStateFlow()
    
    // 注册状态
    private val _registerState = MutableStateFlow<NetworkResult<AuthResponseDTO>>(NetworkResult.Idle)
    val registerState: StateFlow<NetworkResult<AuthResponseDTO>> = _registerState.asStateFlow()
    
    // 用户信息状态
    private val _userInfoState = MutableStateFlow<NetworkResult<UserInfoDTO>>(NetworkResult.Idle)
    val userInfoState: StateFlow<NetworkResult<UserInfoDTO>> = _userInfoState.asStateFlow()
    
    // 登录状态
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    // 登录表单状态
    private val _loginFormState = MutableStateFlow(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginFormState.asStateFlow()
    
    // 注册表单状态
    private val _registerFormState = MutableStateFlow(RegisterFormState())
    val registerFormState: StateFlow<RegisterFormState> = _registerFormState.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    /**
     * 检查登录状态
     */
    fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoggedIn.value = authUseCase.isLoggedIn()
        }
    }
    
    /**
     * 更新登录表单用户名
     */
    fun updateLoginUsername(username: String) {
        _loginFormState.value = _loginFormState.value.copy(username = username)
    }
    
    /**
     * 更新登录表单密码
     */
    fun updateLoginPassword(password: String) {
        _loginFormState.value = _loginFormState.value.copy(password = password)
    }
    
    /**
     * 更新注册表单用户名
     */
    fun updateRegisterUsername(username: String) {
        _registerFormState.value = _registerFormState.value.copy(username = username)
    }
    
    /**
     * 更新注册表单密码
     */
    fun updateRegisterPassword(password: String) {
        _registerFormState.value = _registerFormState.value.copy(password = password)
    }
    
    /**
     * 更新注册表单确认密码
     */
    fun updateRegisterConfirmPassword(confirmPassword: String) {
        _registerFormState.value = _registerFormState.value.copy(confirmPassword = confirmPassword)
    }
    
    /**
     * 执行登录
     */
    fun login() {
        val formState = _loginFormState.value
        viewModelScope.launch {
            authUseCase.login(formState.username, formState.password)
                .collect { result ->
                    _loginState.value = result
                    if (result is NetworkResult.Success) {
                        _isLoggedIn.value = true
                        // 清空表单
                        _loginFormState.value = LoginFormState()
                    }
                }
        }
    }
    
    /**
     * 执行注册
     */
    fun register() {
        val formState = _registerFormState.value
        viewModelScope.launch {
            authUseCase.register(
                formState.username, 
                formState.password, 
                formState.confirmPassword
            ).collect { result ->
                _registerState.value = result
                if (result is NetworkResult.Success) {
                    _isLoggedIn.value = true
                    // 清空表单
                    _registerFormState.value = RegisterFormState()
                }
            }
        }
    }
    
    /**
     * 获取当前用户信息
     */
    fun getCurrentUser() {
        viewModelScope.launch {
            authUseCase.getCurrentUser()
                .collect { result ->
                    _userInfoState.value = result
                }
        }
    }
    
    /**
     * 登出
     */
    fun logout() {
        viewModelScope.launch {
            authUseCase.logout()
            _isLoggedIn.value = false
            _userInfoState.value = NetworkResult.Idle
            _loginState.value = NetworkResult.Idle
            _registerState.value = NetworkResult.Idle
        }
    }
    
    /**
     * 重置登录状态
     */
    fun resetLoginState() {
        _loginState.value = NetworkResult.Idle
    }
    
    /**
     * 重置注册状态
     */
    fun resetRegisterState() {
        _registerState.value = NetworkResult.Idle
    }
}

/**
 * 登录表单状态
 */
data class LoginFormState(
    val username: String = "",
    val password: String = ""
) {
    val isValid: Boolean
        get() = username.isNotBlank() && password.isNotBlank()
}

/**
 * 注册表单状态
 */
data class RegisterFormState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = ""
) {
    val isValid: Boolean
        get() = username.isNotBlank() && 
                password.isNotBlank() && 
                confirmPassword.isNotBlank() &&
                password == confirmPassword
}