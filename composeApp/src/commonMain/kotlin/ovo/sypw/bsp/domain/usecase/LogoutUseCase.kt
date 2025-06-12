package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository

/**
 * 登出用例
 * 处理用户登出的业务逻辑
 */
class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * 执行登出操作
     * @return 登出结果
     */
    suspend operator fun invoke(): NetworkResult<Unit> {
        return try {
            // 检查是否已登录
            if (!authRepository.isLoggedIn()) {
                return NetworkResult.Success(Unit)
            }
            
            // 执行登出
            authRepository.logout()
        } catch (e: Exception) {
            // 即使登出失败，也清除本地数据
            authRepository.clearAuthData()
            NetworkResult.Error(
                exception = e,
                message = "登出失败: ${e.message}"
            )
        }
    }
}