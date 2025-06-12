package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 登录请求数据传输对象
 */
@Serializable
data class LoginRequest(
    /**
     * 用户名或邮箱
     */
    val username: String,
    
    /**
     * 密码
     */
    val password: String,
    
    /**
     * 记住登录状态
     */
    val rememberMe: Boolean = false
)

/**
 * 登录响应数据传输对象
 */
@Serializable
data class LoginResponse(
    /**
     * 访问令牌
     */
    val accessToken: String,
    
    /**
     * 刷新令牌
     */
    val refreshToken: String? = null,
    
    /**
     * 令牌类型
     */
    val tokenType: String = "Bearer",
    
    /**
     * 令牌过期时间（秒）
     */
    val expiresIn: Long? = null,
    
    /**
     * 用户信息
     */
    val user: UserInfo? = null
)

/**
 * 用户信息数据传输对象
 */
@Serializable
data class UserInfo(
    /**
     * 用户ID
     */
    val id: String,
    
    /**
     * 用户名
     */
    val username: String,
    
    /**
     * 邮箱
     */
    val email: String? = null,
    
    /**
     * 昵称
     */
    val nickname: String? = null,
    
    /**
     * 头像URL
     */
    val avatar: String? = null,
    
    /**
     * 手机号
     */
    val phone: String? = null,
    
    /**
     * 用户角色
     */
    val role: String? = null,
    
    /**
     * 创建时间
     */
    val createdAt: String? = null,
    
    /**
     * 更新时间
     */
    val updatedAt: String? = null
)

/**
 * 注册请求数据传输对象
 */
@Serializable
data class RegisterRequest(
    /**
     * 用户名
     */
    val username: String,
    
    /**
     * 密码
     */
    val password: String,
    
    /**
     * 确认密码
     */
    val confirmPassword: String,
    
    /**
     * 邮箱
     */
    val email: String? = null,
    
    /**
     * 手机号
     */
    val phone: String? = null,
    
    /**
     * 昵称
     */
    val nickname: String? = null
)

/**
 * 刷新令牌请求数据传输对象
 */
@Serializable
data class RefreshTokenRequest(
    /**
     * 刷新令牌
     */
    val refreshToken: String
)

/**
 * 刷新令牌响应数据传输对象
 */
@Serializable
data class RefreshTokenResponse(
    /**
     * 新的访问令牌
     */
    val accessToken: String,
    
    /**
     * 新的刷新令牌（可选）
     */
    val refreshToken: String? = null,
    
    /**
     * 令牌类型
     */
    val tokenType: String = "Bearer",
    
    /**
     * 令牌过期时间（秒）
     */
    val expiresIn: Long? = null
)

/**
 * 登出请求数据传输对象
 */
@Serializable
data class LogoutRequest(
    /**
     * 访问令牌
     */
    val accessToken: String? = null
)

/**
 * 修改密码请求数据传输对象
 */
@Serializable
data class ChangePasswordRequest(
    /**
     * 旧密码
     */
    val oldPassword: String,
    
    /**
     * 新密码
     */
    val newPassword: String
)

/**
 * 令牌验证结果数据传输对象
 */
@Serializable
data class TokenValidationResult(
    /**
     * 令牌是否有效
     */
    val isValid: Boolean,
    
    /**
     * 令牌过期时间戳
     */
    val expiresAt: Long? = null
)