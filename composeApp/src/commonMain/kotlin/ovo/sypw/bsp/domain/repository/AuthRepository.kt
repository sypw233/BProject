package ovo.sypw.bsp.domain.repository

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 认证仓库接口
 * 定义认证相关的业务操作
 */
interface AuthRepository {
    
    /**
     * 用户登录
     * @param username 用户名或邮箱
     * @param password 密码
     * @param rememberMe 是否记住登录状态
     * @return 登录结果
     */
    suspend fun login(
        username: String,
        password: String,
        rememberMe: Boolean = false
    ): NetworkResult<LoginResponse>
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param email 邮箱（可选）
     * @param phone 手机号（可选）
     * @param nickname 昵称（可选）
     * @return 注册结果
     */
    suspend fun register(
        username: String,
        password: String,
        confirmPassword: String,
        email: String? = null,
        phone: String? = null,
        nickname: String? = null
    ): NetworkResult<LoginResponse>
    
    /**
     * 用户登出
     * @return 登出结果
     */
    suspend fun logout(): NetworkResult<Unit>
    
    /**
     * 刷新访问令牌
     * @return 刷新结果
     */
    suspend fun refreshToken(): NetworkResult<RefreshTokenResponse>
    
    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    suspend fun getCurrentUser(): NetworkResult<UserInfo>
    
    /**
     * 验证当前令牌是否有效
     * @return 验证结果
     */
    suspend fun validateToken(): NetworkResult<Boolean>
    
    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    suspend fun isLoggedIn(): Boolean
    
    /**
     * 获取当前访问令牌
     * @return 访问令牌
     */
    suspend fun getAccessToken(): String?
    
    /**
     * 获取当前刷新令牌
     * @return 刷新令牌
     */
    suspend fun getRefreshToken(): String?
    
    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    suspend fun getCurrentUserId(): String?
    
    /**
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): NetworkResult<Unit>
    
    /**
     * 忘记密码 - 发送重置邮件
     * @param email 邮箱地址
     * @return 发送结果
     */
    suspend fun forgotPassword(email: String): NetworkResult<Unit>
    
    /**
     * 重置密码
     * @param token 重置令牌
     * @param newPassword 新密码
     * @return 重置结果
     */
    suspend fun resetPassword(
        token: String,
        newPassword: String
    ): NetworkResult<Unit>
    
    /**
     * 清除本地认证信息
     */
    suspend fun clearAuthData()
    
    /**
     * 保存登录信息到本地存储
     * @param loginResponse 登录响应数据
     */
    suspend fun saveLoginInfo(loginResponse: LoginResponse)
}