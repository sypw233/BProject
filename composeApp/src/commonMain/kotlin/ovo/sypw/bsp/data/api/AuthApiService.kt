package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 认证相关的API服务
 * 提供登录、注册、登出等网络请求功能
 */
class AuthApiService : BaseApiService() {
    private val path = "/auth"
    
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
     * 获取当前用户信息（带Token）
     * @param token 认证令牌
     * @return 用户信息响应结果
     */
    suspend fun getCurrentUser(token: String): NetworkResult<SaResult> {
        return getWithToken(
            endpoint = "$path/me",
            token = token
        )
    }
    

    /**
     * 修改密码（需要认证）
     * @param token 认证令牌
     * @param changePasswordRequest 修改密码请求参数
     * @return 修改密码响应结果
     */
    suspend fun changePassword(
        token: String,
        changePasswordRequest: ChangePasswordRequest
    ): NetworkResult<SaResult> {
        return postWithToken(
            endpoint = "$path/change-password",
            token = token,
            body = changePasswordRequest
        )
    }

}