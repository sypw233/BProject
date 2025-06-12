package ovo.sypw.bsp.data.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ovo.sypw.bsp.data.api.AuthApiService
import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository

/**
 * 认证仓库实现类
 * 整合网络API和本地存储，提供完整的认证功能
 */
class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    
    /**
     * 用户登录
     */
    override suspend fun login(
        username: String,
        password: String,
        rememberMe: Boolean
    ): NetworkResult<LoginResponse> {
        val loginRequest = LoginRequest(
            username = username,
            password = password,
            rememberMe = rememberMe
        )
        
        return when (val result = authApiService.login(loginRequest)) {
            is NetworkResult.Success -> {
                val apiResponse = result.data
                if (apiResponse.success && apiResponse.data != null) {
                    // 保存登录信息到本地存储
                    saveLoginInfo(apiResponse.data)
                    NetworkResult.Success(apiResponse.data)
                } else {
                    NetworkResult.Error(
                        exception = Exception(apiResponse.message),
                        message = apiResponse.message
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 用户注册
     */
    override suspend fun register(
        username: String,
        password: String,
        confirmPassword: String,
        email: String?,
        phone: String?,
        nickname: String?
    ): NetworkResult<LoginResponse> {
        val registerRequest = RegisterRequest(
            username = username,
            password = password,
            confirmPassword = confirmPassword,
            email = email,
            phone = phone,
            nickname = nickname
        )
        
        return when (val result = authApiService.register(registerRequest)) {
            is NetworkResult.Success -> {
                val apiResponse = result.data
                if (apiResponse.success && apiResponse.data != null) {
                    // 注册成功后自动保存登录信息
                    saveLoginInfo(apiResponse.data)
                    NetworkResult.Success(apiResponse.data)
                } else {
                    NetworkResult.Error(
                        exception = Exception(apiResponse.message),
                        message = apiResponse.message
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 用户登出
     */
    override suspend fun logout(): NetworkResult<Unit> {
        val accessToken = tokenStorage.getAccessToken()
        val logoutRequest = LogoutRequest(accessToken = accessToken)
        
        // 先调用服务端登出接口
        val result = authApiService.logout(logoutRequest)
        
        // 无论服务端登出是否成功，都清除本地存储
        clearAuthData()
        
        return when (result) {
            is NetworkResult.Success -> {
                val apiResponse = result.data
                if (apiResponse.success) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception(apiResponse.message),
                        message = apiResponse.message
                    )
                }
            }
            is NetworkResult.Error -> {
                // 即使服务端登出失败，本地已清除，仍返回成功
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 刷新访问令牌
     */
    override suspend fun refreshToken(): NetworkResult<RefreshTokenResponse> {
        val refreshToken = tokenStorage.getRefreshToken()
            ?: return NetworkResult.Error(
                exception = Exception("No refresh token available"),
                message = "No refresh token available"
            )
        
        val refreshRequest = RefreshTokenRequest(refreshToken = refreshToken)
        
        return when (val result = authApiService.refreshToken(refreshRequest)) {
            is NetworkResult.Success -> {
                val apiResponse = result.data
                if (apiResponse.success && apiResponse.data != null) {
                    // 保存新的令牌
                    val refreshResponse = apiResponse.data
                    tokenStorage.saveAccessToken(refreshResponse.accessToken)
                    refreshResponse.refreshToken?.let {
                        tokenStorage.saveRefreshToken(it)
                    }
                    NetworkResult.Success(refreshResponse)
                } else {
                    NetworkResult.Error(
                        exception = Exception(apiResponse.message),
                        message = apiResponse.message
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 获取当前用户信息
     */
    override suspend fun getCurrentUser(): NetworkResult<UserInfo> {
        return when (val result = authApiService.getCurrentUser()) {
            is NetworkResult.Success -> {
                val apiResponse = result.data
                if (apiResponse.success && apiResponse.data != null) {
                    // 更新本地用户信息
                    val userInfoJson = Json.encodeToString(apiResponse.data)
                    tokenStorage.saveUserInfo(userInfoJson)
                    NetworkResult.Success(apiResponse.data)
                } else {
                    NetworkResult.Error(
                        exception = Exception(apiResponse.message),
                        message = apiResponse.message
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 验证当前令牌是否有效
     */
    override suspend fun validateToken(): NetworkResult<Boolean> {
        return when (val result = authApiService.validateToken()) {
            is NetworkResult.Success -> {
                val apiResponse = result.data
                if (apiResponse.success && apiResponse.data != null) {
                    NetworkResult.Success(apiResponse.data)
                } else {
                    NetworkResult.Success(false)
                }
            }
            is NetworkResult.Error -> NetworkResult.Success(false)
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 检查是否已登录
     */
    override suspend fun isLoggedIn(): Boolean {
        return tokenStorage.hasValidToken()
    }
    
    /**
     * 获取当前访问令牌
     */
    override suspend fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }
    
    /**
     * 获取当前刷新令牌
     */
    override suspend fun getRefreshToken(): String? {
        return tokenStorage.getRefreshToken()
    }
    
    /**
     * 获取当前用户ID
     */
    override suspend fun getCurrentUserId(): String? {
        return tokenStorage.getUserId()
    }
    
    /**
     * 修改密码
     */
    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): NetworkResult<Unit> {
        return when (val result = authApiService.changePassword(oldPassword, newPassword)) {
            is NetworkResult.Success -> {
                val apiResponse = result.data
                if (apiResponse.success) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception(apiResponse.message),
                        message = apiResponse.message
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 忘记密码 - 发送重置邮件
     */
    override suspend fun forgotPassword(email: String): NetworkResult<Unit> {
        return when (val result = authApiService.forgotPassword(email)) {
            is NetworkResult.Success -> {
                val apiResponse = result.data
                if (apiResponse.success) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception(apiResponse.message),
                        message = apiResponse.message
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 重置密码
     */
    override suspend fun resetPassword(
        token: String,
        newPassword: String
    ): NetworkResult<Unit> {
        return when (val result = authApiService.resetPassword(token, newPassword)) {
            is NetworkResult.Success -> {
                val apiResponse = result.data
                if (apiResponse.success) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception(apiResponse.message),
                        message = apiResponse.message
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 清除本地认证信息
     */
    override suspend fun clearAuthData() {
        tokenStorage.clearTokens()
    }
    
    /**
     * 保存登录信息到本地存储
     */
    override suspend fun saveLoginInfo(loginResponse: LoginResponse) {
        // 保存访问令牌
        tokenStorage.saveAccessToken(loginResponse.accessToken)
        
        // 保存刷新令牌（如果有）
        loginResponse.refreshToken?.let {
            tokenStorage.saveRefreshToken(it)
        }
        
        // 保存用户信息（如果有）
        loginResponse.user?.let { userInfo ->
            tokenStorage.saveUserId(userInfo.id)
            val userInfoJson = Json.encodeToString(userInfo)
            tokenStorage.saveUserInfo(userInfoJson)
        }
    }
}