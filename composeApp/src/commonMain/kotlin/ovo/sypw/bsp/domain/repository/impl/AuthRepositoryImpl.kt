package ovo.sypw.bsp.domain.repository.impl

import kotlinx.serialization.json.Json
import ovo.sypw.bsp.data.api.AuthApiService
import ovo.sypw.bsp.data.dto.ChangePasswordRequest
import ovo.sypw.bsp.data.dto.LoginRequest
import ovo.sypw.bsp.data.dto.LoginResponse
import ovo.sypw.bsp.data.dto.RegisterRequest
import ovo.sypw.bsp.data.dto.UserInfo
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.isSuccess
import ovo.sypw.bsp.data.dto.result.parseData
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.repository.AuthRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 认证仓库实现类
 * 整合网络API和本地存储，提供完整的认证功能
 */
class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    /**
     * 用户登录
     */
    override suspend fun login(
        username: String,
        password: String,
    ): NetworkResult<LoginResponse> {
        val loginRequest = LoginRequest(
            username = username,
            password = password
        )

        return when (val result = authApiService.login(loginRequest)) {
            is NetworkResult.Success -> {
                Logger.i("AuthRepository", "登录请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // 使用反序列化获取登录响应数据
                    val loginResponse = saResult.parseData<LoginResponse>()
                    if (loginResponse != null) {
                        // 保存登录信息到本地存储
                        saveLoginInfo(loginResponse)
                        Logger.i("AuthRepository", "登录信息已保存$loginResponse")

                        // 登录成功后自动获取用户信息
                        fetchAndSaveUserInfo(loginResponse.token)

                        NetworkResult.Success(loginResponse)
                    } else {
                        Logger.w("AuthRepository", "登录响应数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("登录响应数据解析失败"),
                            message = "登录响应数据解析失败"
                        )
                    }
                } else {
                    Logger.w("AuthRepository", "登录失败")
                    NetworkResult.Error(
                        exception = Exception("登录失败"),
                        message = "登录失败"
                    )
                }
            }

            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 用户注册
     */
    override suspend fun register(
        username: String,
        password: String
    ): NetworkResult<LoginResponse> {
        val registerRequest = RegisterRequest(
            username = username,
            password = password
        )

        return when (val result = authApiService.register(registerRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // 使用反序列化获取注册响应数据
                    val loginResponse = saResult.parseData<LoginResponse>()
                    if (loginResponse != null) {
                        // 注册成功后自动保存登录信息
                        saveLoginInfo(loginResponse)

                        // 注册成功后自动获取用户信息
                        fetchAndSaveUserInfo(loginResponse.token)

                        NetworkResult.Success(loginResponse)
                    } else {
                        Logger.w("AuthRepository", "注册响应数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("注册响应数据解析失败"),
                            message = "注册响应数据解析失败"
                        )
                    }
                } else {
                    NetworkResult.Error(
                        exception = Exception("注册失败"),
                        message = "注册失败"
                    )
                }
            }

            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }


    /**
     * 获取当前用户信息
     */
    override suspend fun getCurrentUser(): NetworkResult<UserInfo> {
        val token = tokenStorage.getAccessToken()
        if (token == null) {
            return NetworkResult.Error(
                exception = Exception("未找到访问令牌"),
                message = "请先登录"
            )
        }

        return when (val result = authApiService.getCurrentUser(token)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // 使用反序列化获取用户信息
                    saResult.data
                    val userInfo = saResult.parseData<UserInfo>()
                    if (userInfo != null) {
                        // 保存用户信息到本地存储
                        tokenStorage.saveUserId(userInfo.id)
                        val userInfoJson = Json.encodeToString(userInfo)
                        Logger.i("AuthRepository", "用户信息已保存$userInfoJson")
                        tokenStorage.saveUserInfo(userInfoJson)
                        NetworkResult.Success(userInfo)
                    } else {
                        Logger.w("AuthRepository", "用户信息数据解析失败::$userInfo")
                        NetworkResult.Error(
                            exception = Exception("用户信息数据解析失败"),
                            message = "用户信息数据解析失败"
                        )
                    }
                } else {
                    NetworkResult.Error(
                        exception = Exception("获取用户信息失败"),
                        message = "获取用户信息失败"
                    )
                }
            }

            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 检查是否已登录
     */
    override suspend fun isLoggedIn(): Boolean {
        return tokenStorage.hasValidToken()
    }

    /**
     * 获取当前访问令牌
     */
    override suspend fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }


    /**
     * 获取当前用户ID
     */
    override suspend fun getCurrentUserId(): String? {
        return tokenStorage.getUserId()
    }

    /**
     * 修改密码
     */
    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): NetworkResult<Unit> {
        val token = tokenStorage.getAccessToken()
        if (token.isNullOrEmpty()) {
            return NetworkResult.Error(
                exception = Exception("未找到认证令牌"),
                message = "请先登录"
            )
        }

        val changePasswordRequest = ChangePasswordRequest(
            oldPassword = oldPassword,
            newPassword = newPassword
        )

        return when (val result = authApiService.changePassword(token, changePasswordRequest)) {
            is NetworkResult.Success -> {
                val saResult = result.data
                if (saResult.isSuccess()) {
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error(
                        exception = Exception("修改密码失败"),
                        message = "修改密码失败"
                    )
                }
            }

            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }


    /**
     * 清除本地认证信息
     */
    override suspend fun clearAuthData() {
        tokenStorage.clearTokens()
    }

    /**
     * 保存登录信息到本地存储
     */
    override suspend fun saveLoginInfo(loginResponse: LoginResponse) {
        // 保存访问令牌
        tokenStorage.saveAccessToken(loginResponse.token)


        // 保存用户信息（如果有）
//        loginResponse.user?.let { userInfo ->
//            tokenStorage.saveUserId(userInfo.id)
//            val userInfoJson = Json.encodeToString(userInfo)
//            tokenStorage.saveUserInfo(userInfoJson)
//        }
    }

    /**
     * 获取并保存用户信息
     * @param token 访问令牌
     */
    private suspend fun fetchAndSaveUserInfo(token: String) {
        try {
            when (val result = authApiService.getCurrentUser(token)) {
                is NetworkResult.Success -> {
                    val saResult = result.data
                    if (saResult.isSuccess()) {
                        // 使用反序列化获取用户信息
                        val userInfo = saResult.parseData<UserInfo>()
                        if (userInfo != null) {
                            // 保存用户信息到本地存储
                            tokenStorage.saveUserId(userInfo.id)
                            val userInfoJson = Json.encodeToString(userInfo)
                            tokenStorage.saveUserInfo(userInfoJson)
                            Logger.i("AuthRepository", "用户信息获取并保存成功: $userInfoJson")
                        } else {
                            Logger.w("AuthRepository", "用户信息数据解析失败: $userInfo")
                        }
                    } else {
                        Logger.w("AuthRepository", "获取用户信息失败: ${saResult.msg}")
                    }
                }

                is NetworkResult.Error -> {
                    Logger.e("AuthRepository", "获取用户信息网络请求失败: ${result.message}")
                }

                else -> {
                    Logger.w("AuthRepository", "获取用户信息请求状态异常")
                }
            }
        } catch (e: Exception) {
            Logger.e("AuthRepository", "获取用户信息异常: ${e.message}")
        }
    }
}