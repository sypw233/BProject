package ovo.sypw.bsp.data.repository

import kotlinx.coroutines.flow.Flow
import ovo.sypw.bsp.data.api.AuthApiService
import ovo.sypw.bsp.data.dto.AuthResponseDTO
import ovo.sypw.bsp.data.dto.UserInfoDTO
import ovo.sypw.bsp.data.dto.UserLoginDTO
import ovo.sypw.bsp.data.dto.UserRegisterDTO
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository
import ovo.sypw.bsp.domain.repository.BaseRepository

/**
 * 认证Repository实现类
 * 整合API服务和本地存储，提供完整的认证功能
 */
class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    
    /**
     * 用户登录
     * @param loginRequest 登录请求数据
     * @return 登录结果Flow
     */
    override fun login(loginRequest: UserLoginDTO): Flow<NetworkResult<AuthResponseDTO>> {
        return performNetworkCall {
            val result = authApiService.login(loginRequest)
            // 如果登录成功，保存token
            if (result is NetworkResult.Success && result.data.token != null) {
                saveToken(result.data.token)
            }
            result
        }
    }
    
    /**
     * 用户注册
     * @param registerRequest 注册请求数据
     * @return 注册结果Flow
     */
    override fun register(registerRequest: UserRegisterDTO): Flow<NetworkResult<AuthResponseDTO>> {
        return performNetworkCall {
            val result = authApiService.register(registerRequest)
            // 如果注册成功，保存token
            if (result is NetworkResult.Success && result.data.token != null) {
                saveToken(result.data.token)
            }
            result
        }
    }
    
    /**
     * 获取当前登录用户信息
     * @return 用户信息Flow
     */
    override fun getCurrentUser(): Flow<NetworkResult<UserInfoDTO>> {
        return performNetworkCall {
            val token = getToken()
            if (token != null) {
                authApiService.getCurrentUser(token)
            } else {
                NetworkResult.Error(Exception("未找到访问令牌，请重新登录"))
            }
        }
    }
    
    /**
     * 保存认证token
     * @param token 访问令牌
     */
    override suspend fun saveToken(token: String) {
        tokenStorage.saveToken(token)
    }
    
    /**
     * 获取当前保存的token
     * @return 访问令牌，如果不存在则返回null
     */
    override suspend fun getToken(): String? {
        return tokenStorage.getToken()
    }
    
    /**
     * 清除认证信息（登出）
     */
    override suspend fun logout() {
        tokenStorage.clearToken()
    }
    
    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    override suspend fun isLoggedIn(): Boolean {
        return tokenStorage.isLoggedIn()
    }
    
    /**
     * 执行网络请求并返回Flow
     * @param apiCall 网络请求函数
     * @return Flow<NetworkResult<T>>
     */
    override fun <T> performNetworkCall(
        apiCall: suspend () -> NetworkResult<T>
    ): Flow<NetworkResult<T>> {
        return kotlinx.coroutines.flow.flow {
            emit(NetworkResult.Loading)
            try {
                val result = apiCall()
                emit(result)
            } catch (e: Exception) {
                emit(NetworkResult.Error(e))
            }
        }
    }
}