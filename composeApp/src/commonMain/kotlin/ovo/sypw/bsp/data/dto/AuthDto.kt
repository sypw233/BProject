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
    val token: String,

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

