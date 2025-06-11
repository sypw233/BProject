package ovo.sypw.bsp.data.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 认证API服务类
 * 处理用户登录、注册和认证相关的网络请求
 */
class AuthApiService : BaseApiService() {
    
    /**
     * 用户登录
     * @param loginRequest 登录请求数据
     * @return 登录结果
     */
    suspend fun login(loginRequest: UserLoginDTO): NetworkResult<AuthResponseDTO> {
        return try {
            val response = httpClient.post("${NetworkConfig.BASE_URL}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val saResult = response.body<SaResult<AuthResponseDTO>>()
                    if (saResult.isSuccess) {
                        NetworkResult.Success(saResult.data ?: AuthResponseDTO())
                    } else {
                        NetworkResult.Error(Exception(saResult.msg))
                    }
                }
                else -> {
                    NetworkResult.Error(Exception("登录失败: ${response.status.description}"))
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    /**
     * 用户注册
     * @param registerRequest 注册请求数据
     * @return 注册结果
     */
    suspend fun register(registerRequest: UserRegisterDTO): NetworkResult<AuthResponseDTO> {
        return try {
            val response = httpClient.post("${NetworkConfig.BASE_URL}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(registerRequest)
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val saResult = response.body<SaResult<AuthResponseDTO>>()
                    if (saResult.isSuccess) {
                        NetworkResult.Success(saResult.data ?: AuthResponseDTO())
                    } else {
                        NetworkResult.Error(Exception(saResult.msg))
                    }
                }
                else -> {
                    NetworkResult.Error(Exception("注册失败: ${response.status.description}"))
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    /**
     * 获取当前登录用户信息
     * @param token 访问令牌
     * @return 用户信息
     */
    suspend fun getCurrentUser(token: String): NetworkResult<UserInfoDTO> {
        return try {
            val response = httpClient.get("${NetworkConfig.BASE_URL}/auth/me") {
                header("Authorization", "Bearer $token")
            }
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val saResult = response.body<SaResult<UserInfoDTO>>()
                    if (saResult.isSuccess) {
                        NetworkResult.Success(saResult.data ?: UserInfoDTO(username = ""))
                    } else {
                        NetworkResult.Error(Exception(saResult.msg))
                    }
                }
                else -> {
                    NetworkResult.Error(Exception("获取用户信息失败: ${response.status.description}"))
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}