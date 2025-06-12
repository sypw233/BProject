package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.RefreshTokenResponse
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository

/**
 * 刷新令牌用例
 * 处理访问令牌刷新的业务逻辑
 */
class RefreshTokenUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * 刷新访问令牌
     * @return 刷新结果
     */
    suspend operator fun invoke(): NetworkResult<RefreshTokenResponse> {
        return try {
            // 检查是否有刷新令牌
            val refreshToken = authRepository.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                return NetworkResult.Error(
                    exception = Exception("没有可用的刷新令牌"),
                    message = "没有可用的刷新令牌"
                )
            }
            
            // 执行令牌刷新
            when (val result = authRepository.refreshToken()) {
                is NetworkResult.Success -> {
                    NetworkResult.Success(result.data)
                }
                is NetworkResult.Error -> {
                    // 如果刷新失败，可能是刷新令牌已过期，清除本地认证信息
                    authRepository.clearAuthData()
                    NetworkResult.Error(
                        exception = Exception("登录已过期，请重新登录"),
                        message = "登录已过期，请重新登录"
                    )
                }
                is NetworkResult.Loading -> result
                is NetworkResult.Idle -> NetworkResult.Error(
                    exception = Exception("令牌刷新未开始"),
                    message = "令牌刷新未开始"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                exception = e,
                message = "刷新令牌失败: ${e.message}"
            )
        }
    }
    
    /**
     * 检查令牌是否需要刷新
     * 这个方法可以在拦截器中使用，自动刷新即将过期的令牌
     * @return 是否需要刷新
     */
    suspend fun shouldRefreshToken(): Boolean {
        return try {
            // 验证当前令牌是否有效
            when (val result = authRepository.validateToken()) {
                is NetworkResult.Success -> !result.data
                else -> true // 如果验证失败，认为需要刷新
            }
        } catch (e: Exception) {
            true // 异常情况下认为需要刷新
        }
    }
}