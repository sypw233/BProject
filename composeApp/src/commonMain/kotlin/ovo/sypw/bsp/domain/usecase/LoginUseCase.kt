package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.LoginResponse
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository

/**
 * 登录用例
 * 处理用户登录的业务逻辑
 */
class LoginUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * 执行登录操作
     * @param username 用户名或邮箱
     * @param password 密码
     * @param rememberMe 是否记住登录状态
     * @return 登录结果
     */
    suspend operator fun invoke(
        username: String,
        password: String,
        rememberMe: Boolean = false
    ): NetworkResult<LoginResponse> {
        // 输入验证
        val validationResult = validateInput(username, password)
        if (validationResult != null) {
            return NetworkResult.Error(
                exception = Exception(validationResult),
                message = validationResult
            )
        }
        
        // 执行登录
        return authRepository.login(
            username = username.trim(),
            password = password,
            rememberMe = rememberMe
        )
    }
    
    /**
     * 验证输入参数
     * @param username 用户名
     * @param password 密码
     * @return 验证错误信息，如果验证通过则返回null
     */
    private fun validateInput(username: String, password: String): String? {
        return when {
            username.isBlank() -> "用户名不能为空"
            password.isBlank() -> "密码不能为空"
            username.length < 3 -> "用户名至少需要3个字符"
            password.length < 6 -> "密码至少需要6个字符"
            else -> null
        }
    }
}