package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 用户登录请求DTO
 */
@Serializable
data class UserLoginDTO(
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
 * 用户注册请求DTO
 */
@Serializable
data class UserRegisterDTO(
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
 * 认证响应DTO
 */
@Serializable
data class AuthResponseDTO(
    /**
     * 访问令牌
     */
    val token: String? = null,
    
    /**
     * 用户信息
     */
    val user: UserInfoDTO? = null
)

/**
 * 用户信息DTO
 */
@Serializable
data class UserInfoDTO(
    /**
     * 用户ID
     */
    val id: Long? = null,
    
    /**
     * 用户名
     */
    val username: String,
    
    /**
     * 真实姓名
     */
    val realName: String? = null,
    
    /**
     * 头像URL
     */
    val avatar: String? = null
)

/**
 * SaResult响应包装类
 */
@Serializable
data class SaResult<T>(
    /**
     * 响应码
     */
    val code: Int = 200,
    
    /**
     * 响应消息
     */
    val msg: String = "success",
    
    /**
     * 响应数据
     */
    val data: T? = null
) {
    /**
     * 判断请求是否成功
     */
    val isSuccess: Boolean
        get() = code == 200
}