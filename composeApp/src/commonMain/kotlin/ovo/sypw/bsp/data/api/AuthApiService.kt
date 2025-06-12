package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 认证相关的API服务
 * 提供登录、注册、登出等网络请求功能
 */
class AuthApiService : BaseApiService() {
    private val path = "auth"
    
    /**
     * 用户登录
     * @param loginRequest 登录请求参数
     * @return 登录结果
     */
    suspend fun login(loginRequest: LoginRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/login",
            body = loginRequest
        )
    }
    
    /**
     * 用户注册
     * @param registerRequest 注册请求参数
     * @return 注册响应结果
     */
    suspend fun register(registerRequest: RegisterRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/register",
            body = registerRequest
        )
    }
    
    /**
     * 刷新访问令牌
     * @param refreshTokenRequest 刷新令牌请求参数
     * @return 刷新令牌响应结果
     */
    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/refresh",
            body = refreshTokenRequest
        )
    }
    
    /**
     * 用户登出
     * @param logoutRequest 登出请求参数
     * @return 登出响应结果
     */
    suspend fun logout(logoutRequest: LogoutRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/logout",
            body = logoutRequest
        )
    }
    
    /**
     * 获取当前用户信息
     * @return 用户信息响应结果
     */
    suspend fun getCurrentUser(): NetworkResult<SaResult> {
        return get(
            endpoint = "$path/me"
        )
    }
    
    /**
     * 验证令牌有效性
     * @return 验证结果
     */
    suspend fun validateToken(): NetworkResult<SaResult> {
        return get(
            endpoint = "$path/validate"
        )
    }
    
    /**
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改密码响应结果
     */
    suspend fun changePassword(
        changePasswordRequest: ChangePasswordRequest
    ): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/change-password",
            body = changePasswordRequest
        )
    }
    
    /**
     * 忘记密码 - 发送重置邮件
     * @param email 邮箱地址
     * @return 发送结果
     */
    suspend fun forgotPassword(email: String): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/forgot-password",
            body = mapOf("email" to email)
        )
    }
    
    /**
     * 重置密码
     * @param token 重置令牌
     * @param newPassword 新密码
     * @return 重置结果
     */
    suspend fun resetPassword(
        token: String,
        newPassword: String
    ): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/reset-password",
            body = mapOf(
                "token" to token,
                "newPassword" to newPassword
            )
        )
    }
}