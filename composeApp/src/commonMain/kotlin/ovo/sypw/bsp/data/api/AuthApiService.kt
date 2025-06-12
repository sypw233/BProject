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
     * @param forgotPasswordRequest 忘记密码请求参数
     * @return 发送结果
     */
    suspend fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/forgot-password",
            body = forgotPasswordRequest
        )
    }
    
    /**
     * 重置密码
     * @param resetPasswordRequest 重置密码请求参数
     * @return 重置结果
     */
    suspend fun resetPassword(
        resetPasswordRequest: ResetPasswordRequest
    ): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/reset-password",
            body = resetPasswordRequest
        )
    }
    
    /**
     * 发送邮箱验证码
     * @param sendCodeRequest 发送验证码请求参数
     * @return 发送结果
     */
    suspend fun sendEmailCode(sendCodeRequest: SendCodeRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/send-email-code",
            body = sendCodeRequest
        )
    }
    
    /**
     * 发送手机验证码
     * @param sendCodeRequest 发送验证码请求参数
     * @return 发送结果
     */
    suspend fun sendSmsCode(sendCodeRequest: SendCodeRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/send-sms-code",
            body = sendCodeRequest
        )
    }
    
    /**
     * 验证邮箱验证码
     * @param verifyCodeRequest 验证码验证请求参数
     * @return 验证结果
     */
    suspend fun verifyEmailCode(verifyCodeRequest: VerifyCodeRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/verify-email-code",
            body = verifyCodeRequest
        )
    }
    
    /**
     * 验证手机验证码
     * @param verifyCodeRequest 验证码验证请求参数
     * @return 验证结果
     */
    suspend fun verifySmsCode(verifyCodeRequest: VerifyCodeRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/verify-sms-code",
            body = verifyCodeRequest
        )
    }
    
    /**
     * 更新用户信息
     * @param updateUserRequest 更新用户信息请求参数
     * @return 更新结果
     */
    suspend fun updateUserInfo(updateUserRequest: UpdateUserRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/update-profile",
            body = updateUserRequest
        )
    }
    
    /**
     * 绑定邮箱
     * @param bindEmailRequest 绑定邮箱请求参数
     * @return 绑定结果
     */
    suspend fun bindEmail(bindEmailRequest: BindEmailRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/bind-email",
            body = bindEmailRequest
        )
    }
    
    /**
     * 绑定手机号
     * @param bindPhoneRequest 绑定手机号请求参数
     * @return 绑定结果
     */
    suspend fun bindPhone(bindPhoneRequest: BindPhoneRequest): NetworkResult<SaResult> {
        return post(
            endpoint = "$path/bind-phone",
            body = bindPhoneRequest
        )
    }
}