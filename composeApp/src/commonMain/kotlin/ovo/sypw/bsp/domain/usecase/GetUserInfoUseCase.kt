package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.UserInfo
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository

/**
 * 获取用户信息用例
 * 处理获取当前用户信息的业务逻辑
 */
class GetUserInfoUseCase(
    private val authRepository: AuthRepository
) {

    /**
     * 获取当前用户信息
     * @param forceRefresh 是否强制从服务器刷新
     * @return 用户信息结果
     */
    suspend operator fun invoke(forceRefresh: Boolean = false): NetworkResult<UserInfo> {
        return try {
            // 检查是否已登录
            if (!authRepository.isLoggedIn()) {
                return NetworkResult.Error(
                    exception = Exception("用户未登录"),
                    message = "用户未登录"
                )
            }

            // 如果不强制刷新，可以先尝试从本地获取
            if (!forceRefresh) {
                // 这里可以添加从本地缓存获取用户信息的逻辑
                // 暂时直接从服务器获取
            }

            // 从服务器获取用户信息
            authRepository.getCurrentUser()
        } catch (e: Exception) {
            NetworkResult.Error(
                exception = e,
                message = "获取用户信息失败: ${e.message}"
            )
        }
    }
}