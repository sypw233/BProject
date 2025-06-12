package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 登录请求数据传输对象 - 对应后端UserLoginDTO
 */
@Serializable
data class LoginRequest(
    /**
     * 用户名
     */
    val username: String,
    
    /**
     * 密码
     */
    val password: String
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
 * 注册请求数据传输对象 - 对应后端UserRegisterDTO
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
    val password: String
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

/**
 * 忘记密码请求数据传输对象
 */
@Serializable
data class ForgotPasswordRequest(
    /**
     * 邮箱地址
     */
    val email: String
)

/**
 * 重置密码请求数据传输对象
 */
@Serializable
data class ResetPasswordRequest(
    /**
     * 重置令牌
     */
    val token: String,
    
    /**
     * 新密码
     */
    val newPassword: String
)

/**
 * 发送验证码请求数据传输对象
 */
@Serializable
data class SendCodeRequest(
    /**
     * 邮箱或手机号
     */
    val target: String,
    
    /**
     * 验证码类型（register, login, reset_password, bind等）
     */
    val type: String = "register"
)

/**
 * 验证码验证请求数据传输对象
 */
@Serializable
data class VerifyCodeRequest(
    /**
     * 邮箱或手机号
     */
    val target: String,
    
    /**
     * 验证码
     */
    val code: String,
    
    /**
     * 验证码类型
     */
    val type: String = "register"
)

/**
 * 更新用户信息请求数据传输对象
 */
@Serializable
data class UpdateUserRequest(
    /**
     * 昵称
     */
    val nickname: String? = null,
    
    /**
     * 头像URL
     */
    val avatar: String? = null,
    
    /**
     * 个人简介
     */
    val bio: String? = null,
    
    /**
     * 性别（male, female, other）
     */
    val gender: String? = null,
    
    /**
     * 生日（YYYY-MM-DD格式）
     */
    val birthday: String? = null
)

/**
 * 绑定邮箱请求数据传输对象
 */
@Serializable
data class BindEmailRequest(
    /**
     * 邮箱地址
     */
    val email: String,
    
    /**
     * 验证码
     */
    val code: String
)

/**
 * 绑定手机号请求数据传输对象
 */
@Serializable
data class BindPhoneRequest(
    /**
     * 手机号
     */
    val phone: String,
    
    /**
     * 验证码
     */
    val code: String
)

/**
 * 注册响应数据传输对象
 */
@Serializable
data class RegisterResponse(
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