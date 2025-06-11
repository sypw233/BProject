package ovo.sypw.bsp.domain.repository

import kotlinx.coroutines.flow.Flow
import ovo.sypw.bsp.data.dto.AuthResponseDTO
import ovo.sypw.bsp.data.dto.UserInfoDTO
import ovo.sypw.bsp.data.dto.UserLoginDTO
import ovo.sypw.bsp.data.dto.UserRegisterDTO
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 认证Repository接口
 * 定义认证相关的数据访问操作
 */
interface AuthRepository : BaseRepository {
    
    /**
     * 用户登录
     * @param loginRequest 登录请求数据
     * @return 登录结果Flow
     */
    fun login(loginRequest: UserLoginDTO): Flow<NetworkResult<AuthResponseDTO>>
    
    /**
     * 用户注册
     * @param registerRequest 注册请求数据
     * @return 注册结果Flow
     */
    fun register(registerRequest: UserRegisterDTO): Flow<NetworkResult<AuthResponseDTO>>
    
    /**
     * 获取当前登录用户信息
     * @return 用户信息Flow
     */
    fun getCurrentUser(): Flow<NetworkResult<UserInfoDTO>>
    
    /**
     * 保存认证token
     * @param token 访问令牌
     */
    suspend fun saveToken(token: String)
    
    /**
     * 获取当前保存的token
     * @return 访问令牌，如果不存在则返回null
     */
    suspend fun getToken(): String?
    
    /**
     * 清除认证信息（登出）
     */
    suspend fun logout()
    
    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    suspend fun isLoggedIn(): Boolean
}