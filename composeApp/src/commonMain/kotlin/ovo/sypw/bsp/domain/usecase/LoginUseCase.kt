package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.LoginResponse
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository
import ovo.sypw.bsp.utils.Logger

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
        Logger.i("LoginUseCase", "=== 开始执行登录用例 ===")
        Logger.d(
            "LoginUseCase",
            "输入参数 - 用户名: '$username', 密码长度: ${password.length}, 记住我: $rememberMe"
        )

        try {
            // 输入验证
            Logger.d("LoginUseCase", "开始输入验证")
            val validationResult = validateInput(username, password)
            if (validationResult != null) {
                Logger.w("LoginUseCase", "输入验证失败: $validationResult")
                return NetworkResult.Error(
                    exception = Exception(validationResult),
                    message = validationResult
                )
            }
            Logger.d("LoginUseCase", "输入验证通过")

            // 执行登录
            Logger.d("LoginUseCase", "调用 authRepository.login")
            val result = authRepository.login(
                username = username.trim(),
                password = password,
            )
            Logger.d(
                "LoginUseCase",
                "authRepository.login 返回结果类型: ${result::class.simpleName}"
            )

            return result
        } catch (e: Exception) {
            Logger.e("LoginUseCase", "登录用例执行过程中发生异常: ${e.message}", e)
            return NetworkResult.Error(
                exception = e,
                message = e.message ?: "登录过程中发生未知错误"
            )
        } finally {
            Logger.i("LoginUseCase", "=== 登录用例执行结束 ===")
        }
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
            else -> null
        }
    }
}