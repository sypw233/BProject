package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository

/**
 * 修改密码用例
 * 处理用户修改密码的业务逻辑
 */
class ChangePasswordUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * 执行修改密码操作
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param confirmPassword 确认新密码
     * @return 修改密码结果
     */
    suspend operator fun invoke(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): NetworkResult<Unit> {
        // 输入验证
        val validationResult = validateInput(
            oldPassword = oldPassword,
            newPassword = newPassword,
            confirmPassword = confirmPassword
        )
        if (validationResult != null) {
            return NetworkResult.Error(
                exception = Exception(validationResult),
                message = validationResult
            )
        }
        
        // 执行修改密码
        return authRepository.changePassword(
            oldPassword = oldPassword.trim(),
            newPassword = newPassword.trim()
        )
    }
    
    /**
     * 验证输入参数
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param confirmPassword 确认新密码
     * @return 验证错误信息，如果验证通过则返回null
     */
    private fun validateInput(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): String? {
        return when {
            oldPassword.isBlank() -> "旧密码不能为空"
            newPassword.isBlank() -> "新密码不能为空"
            confirmPassword.isBlank() -> "确认密码不能为空"
            oldPassword.length < 6 -> "旧密码至少需要6个字符"
            newPassword.length < 6 -> "新密码至少需要6个字符"
            newPassword.length > 50 -> "新密码不能超过50个字符"
            newPassword != confirmPassword -> "两次输入的新密码不一致"
            oldPassword == newPassword -> "新密码不能与旧密码相同"
            else -> null
        }
    }
    

}