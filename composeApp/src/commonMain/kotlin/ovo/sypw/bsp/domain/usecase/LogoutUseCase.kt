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
    suspend operator fun invoke() {
        try {
            authRepository.clearAuthData()
        } catch (e: Exception) {
            throw e
        }
    }
}