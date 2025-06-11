package ovo.sypw.bsp.domain.usecase

import kotlinx.coroutines.flow.Flow
import ovo.sypw.bsp.data.dto.AuthResponseDTO
import ovo.sypw.bsp.data.dto.UserInfoDTO
import ovo.sypw.bsp.data.dto.UserLoginDTO
import ovo.sypw.bsp.data.dto.UserRegisterDTO
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.AuthRepository

/**
 * 认证用例类
 * 封装认证相关的业务逻辑
 */
class AuthUseCase(
    private val authRepository: AuthRepository
) {
    
    /**
     * 用户登录用例
     * @param username 用户名
     * @param password 密码
     * @return 登录结果Flow
     */
    fun login(username: String, password: String): Flow<NetworkResult<AuthResponseDTO>> {
        // 输入验证
        if (username.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(Exception("用户名不能为空"))
            )
        }
        if (password.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(Exception("密码不能为空"))
            )
        }
        if (password.length < 6) {
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(Exception("密码长度不能少于6位"))
            )
        }
        
        val loginRequest = UserLoginDTO(
            username = username.trim(),
            password = password
        )
        
        return authRepository.login(loginRequest)
    }
    
    /**
     * 用户注册用例
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return 注册结果Flow
     */
    fun register(
        username: String, 
        password: String, 
        confirmPassword: String
    ): Flow<NetworkResult<AuthResponseDTO>> {
        // 输入验证
        if (username.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(Exception("用户名不能为空"))
            )
        }
        if (username.length < 3) {
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(Exception("用户名长度不能少于3位"))
            )
        }
        if (password.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(Exception("密码不能为空"))
            )
        }
        if (password.length < 6) {
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(Exception("密码长度不能少于6位"))
            )
        }
        if (password != confirmPassword) {
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(Exception("两次输入的密码不一致"))
            )
        }
        
        val registerRequest = UserRegisterDTO(
            username = username.trim(),
            password = password
        )
        
        return authRepository.register(registerRequest)
    }
    
    /**
     * 获取当前用户信息用例
     * @return 用户信息Flow
     */
    fun getCurrentUser(): Flow<NetworkResult<UserInfoDTO>> {
        return authRepository.getCurrentUser()
    }
    
    /**
     * 登出用例
     */
    suspend fun logout() {
        authRepository.logout()
    }
    
    /**
     * 检查登录状态用例
     * @return 是否已登录
     */
    suspend fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
    
    /**
     * 获取当前token用例
     * @return 访问令牌
     */
    suspend fun getToken(): String? {
        return authRepository.getToken()
    }
}