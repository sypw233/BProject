package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.LoginResponse
import ovo.sypw.bsp.domain.model.NetworkResult
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
     * @param confirmPassword 确认密码
     * @param email 邮箱（可选）
     * @param phone 手机号（可选）
     * @param nickname 昵称（可选）
     * @return 注册结果
     */
    suspend operator fun invoke(
        username: String,
        password: String,
        confirmPassword: String,
        email: String? = null,
        phone: String? = null,
        nickname: String? = null
    ): NetworkResult<LoginResponse> {
        // 输入验证
        val validationResult = validateInput(
            username = username,
            password = password,
            confirmPassword = confirmPassword,
            email = email,
            phone = phone
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
            password = password,
            confirmPassword = confirmPassword,
            email = email?.trim(),
            phone = phone?.trim(),
            nickname = nickname?.trim()
        )
    }
    
    /**
     * 验证输入参数
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param email 邮箱
     * @param phone 手机号
     * @return 验证错误信息，如果验证通过则返回null
     */
    private fun validateInput(
        username: String,
        password: String,
        confirmPassword: String,
        email: String?,
        phone: String?
    ): String? {
        return when {
            username.isBlank() -> "用户名不能为空"
            password.isBlank() -> "密码不能为空"
            confirmPassword.isBlank() -> "确认密码不能为空"
            username.length < 3 -> "用户名至少需要3个字符"
            username.length > 20 -> "用户名不能超过20个字符"
            password.length < 6 -> "密码至少需要6个字符"
            password.length > 50 -> "密码不能超过50个字符"
            password != confirmPassword -> "两次输入的密码不一致"
            !isValidUsername(username) -> "用户名只能包含字母、数字和下划线"
            !isValidPassword(password) -> "密码必须包含字母和数字"
            email != null && !isValidEmail(email) -> "邮箱格式不正确"
            phone != null && !isValidPhone(phone) -> "手机号格式不正确"
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
    
    /**
     * 验证邮箱格式
     * @param email 邮箱
     * @return 是否有效
     */
    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return regex.matches(email)
    }
    
    /**
     * 验证手机号格式（中国大陆）
     * @param phone 手机号
     * @return 是否有效
     */
    private fun isValidPhone(phone: String): Boolean {
        val regex = Regex("^1[3-9]\\d{9}$")
        return regex.matches(phone)
    }
}