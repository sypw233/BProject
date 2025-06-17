package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.LoginResponse
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository

/**
 * 注册用例
 * 处理用户注册的业务逻辑
 */
class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * 执行注册操作
     * @param username 用户名
     * @param password 密码
     * @return 注册结果
     */
    suspend operator fun invoke(
        username: String,
        password: String
    ): NetworkResult<LoginResponse> {
        // 输入验证
        val validationResult = validateInput(
            username = username,
            password = password
        )
        if (validationResult != null) {
            return NetworkResult.Error(
                exception = Exception(validationResult),
                message = validationResult
            )
        }
        
        // 执行注册
        return authRepository.register(
            username = username.trim(),
            password = password
        )
    }
    
    /**
     * 验证输入参数
     * @param username 用户名
     * @param password 密码
     * @return 验证错误信息，如果验证通过则返回null
     */
    private fun validateInput(
        username: String,
        password: String
    ): String? {
        return when {
            username.isBlank() -> "用户名不能为空"
            password.isBlank() -> "密码不能为空"
            username.length < 3 -> "用户名至少需要3个字符"
            username.length > 20 -> "用户名不能超过20个字符"
            password.length < 6 -> "密码至少需要6个字符"
            password.length > 50 -> "密码不能超过50个字符"
            !isValidUsername(username) -> "用户名只能包含字母、数字和下划线"
            !isValidPassword(password) -> "密码必须包含字母和数字"
            else -> null
        }
    }
    
    /**
     * 验证用户名格式
     * @param username 用户名
     * @return 是否有效
     */
    private fun isValidUsername(username: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9_]+$")
        return regex.matches(username)
    }
    
    /**
     * 验证密码强度
     * @param password 密码
     * @return 是否有效
     */
    private fun isValidPassword(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }
    

}