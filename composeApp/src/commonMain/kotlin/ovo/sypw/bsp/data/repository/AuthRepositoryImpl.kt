package ovo.sypw.bsp.data.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ovo.sypw.bsp.data.api.AuthApiService
import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository
import ovo.sypw.bsp.utils.Logger

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
    ): NetworkResult<LoginResponse> {
        val loginRequest = LoginRequest(
            username = username,
            password = password
        )
        
        return when (val result = authApiService.login(loginRequest)) {
            is NetworkResult.Success -> {
                Logger.i("AuthRepository", "登录请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // 创建模拟的登录响应数据
                    val loginResponse = LoginResponse(
                        accessToken = "mock_token", // 实际应该从saResult.key中解析
                        refreshToken = "mock_refresh_token",
                        user = UserInfo(
                            id = "1",
                            username = username,
                            email = "$username@example.com"
                        )
                    )
                    // 保存登录信息到本地存储
                    saveLoginInfo(loginResponse)
                    Logger.i("AuthRepository", "登录信息已保存")
                    NetworkResult.Success(loginResponse)
                } else {
                    Logger.w("AuthRepository", "登录失败")
                    NetworkResult.Error(
                        exception = Exception("登录失败"),
                        message = "登录失败"
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
        password: String
    ): NetworkResult<LoginResponse> {
        val registerRequest = RegisterRequest(
            username = username,
            password = password
        )
        
        return when (val result = authApiService.register(registerRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // 创建模拟的注册响应数据
                    val loginResponse = LoginResponse(
                        accessToken = "mock_token",
                        refreshToken = "mock_refresh_token",
                        user = UserInfo(
                            id = "1",
                            username = username,
                            email = "$username@example.com"
                        )
                    )
                    // 注册成功后自动保存登录信息
                    saveLoginInfo(loginResponse)
                    NetworkResult.Success(loginResponse)
                } else {
                    NetworkResult.Error(
                        exception = Exception("注册失败"),
                        message = "注册失败"
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
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception("登出失败"),
                        message = "登出失败"
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
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // 创建模拟的刷新令牌响应数据
                    val refreshResponse = RefreshTokenResponse(
                        accessToken = "mock_new_access_token",
                        refreshToken = "mock_new_refresh_token"
                    )
                    // 保存新的令牌
                    tokenStorage.saveAccessToken(refreshResponse.accessToken)
                    refreshResponse.refreshToken?.let {
                        tokenStorage.saveRefreshToken(it)
                    }
                    NetworkResult.Success(refreshResponse)
                } else {
                    NetworkResult.Error(
                        exception = Exception("刷新令牌失败"),
                        message = "刷新令牌失败"
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
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // 创建模拟的用户信息
                    val userInfo = UserInfo(
                        id = "1",
                        username = "current_user",
                        email = "current_user@example.com"
                    )
                    // 保存用户信息到本地存储
                    val userInfoJson = Json.encodeToString(userInfo)
                    tokenStorage.saveUserInfo(userInfoJson)
                    NetworkResult.Success(userInfo)
                } else {
                    NetworkResult.Error(
                        exception = Exception("获取用户信息失败"),
                        message = "获取用户信息失败"
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
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(true)
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
        val changePasswordRequest = ChangePasswordRequest(
            oldPassword = oldPassword,
            newPassword = newPassword
        )
        
        return when (val result = authApiService.changePassword(changePasswordRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception("修改密码失败"),
                        message = "修改密码失败"
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
        val forgotPasswordRequest = ForgotPasswordRequest(email = email)
        return when (val result = authApiService.forgotPassword(forgotPasswordRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception(saResult.getErrorMessage() ?: "发送重置密码邮件失败"),
                        message = saResult.getErrorMessage() ?: "发送重置密码邮件失败"
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
        val resetPasswordRequest = ResetPasswordRequest(
            token = token,
            newPassword = newPassword
        )
        return when (val result = authApiService.resetPassword(resetPasswordRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception(saResult.getErrorMessage() ?: "重置密码失败"),
                        message = saResult.getErrorMessage() ?: "重置密码失败"
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 发送邮箱验证码
     */
    suspend fun sendEmailCode(email: String, type: String = "register"): NetworkResult<Unit> {
        val sendCodeRequest = SendCodeRequest(target = email, type = type)
        return when (val result = authApiService.sendEmailCode(sendCodeRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception(saResult.getErrorMessage() ?: "发送邮箱验证码失败"),
                        message = saResult.getErrorMessage() ?: "发送邮箱验证码失败"
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 发送手机验证码
     */
    suspend fun sendSmsCode(phone: String, type: String = "register"): NetworkResult<Unit> {
        val sendCodeRequest = SendCodeRequest(target = phone, type = type)
        return when (val result = authApiService.sendSmsCode(sendCodeRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception(saResult.getErrorMessage() ?: "发送手机验证码失败"),
                        message = saResult.getErrorMessage() ?: "发送手机验证码失败"
                    )
                }
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 验证邮箱验证码
     */
    suspend fun verifyEmailCode(email: String, code: String, type: String = "register"): NetworkResult<Boolean> {
        val verifyCodeRequest = VerifyCodeRequest(target = email, code = code, type = type)
        return when (val result = authApiService.verifyEmailCode(verifyCodeRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(true)
                } else {
                    NetworkResult.Error(
                        exception = Exception(saResult.getErrorMessage() ?: "验证码验证失败"),
                        message = saResult.getErrorMessage() ?: "验证码验证失败"
                    )
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(
                exception = result.exception,
                message = result.message
            )
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 验证手机验证码
     */
    suspend fun verifySmsCode(phone: String, code: String, type: String = "register"): NetworkResult<Boolean> {
        val verifyCodeRequest = VerifyCodeRequest(target = phone, code = code, type = type)
        return when (val result = authApiService.verifySmsCode(verifyCodeRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(true)
                } else {
                    NetworkResult.Error(
                        exception = Exception(saResult.getErrorMessage() ?: "验证码验证失败"),
                        message = saResult.getErrorMessage() ?: "验证码验证失败"
                    )
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(
                exception = result.exception,
                message = result.message
            )
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