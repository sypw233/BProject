package ovo.sypw.bsp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ovo.sypw.bsp.data.api.AIChatApiService
import ovo.sypw.bsp.data.dto.AIChatRequest
import ovo.sypw.bsp.data.dto.AIChatResponse
import ovo.sypw.bsp.data.dto.AIChatStreamResponse
import ovo.sypw.bsp.data.dto.ChatSession
import ovo.sypw.bsp.data.dto.SessionsResponse
import ovo.sypw.bsp.data.dto.SessionDetailResponse
import ovo.sypw.bsp.data.dto.DeleteSessionResponse
import ovo.sypw.bsp.data.dto.ModelsResponse
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.NetworkResult.*
import ovo.sypw.bsp.data.dto.result.isSuccess
import ovo.sypw.bsp.data.dto.result.parseData
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.repository.AIChatRepository
import ovo.sypw.bsp.utils.Logger

/**
 * AI对话仓库实现类
 * 整合网络API和本地存储，提供完整的AI对话功能
 */
class AIChatRepositoryImpl(
    private val aiChatApiService: AIChatApiService,
    private val tokenStorage: TokenStorage
) : AIChatRepository {

    companion object {
        private const val TAG = "AIChatRepository"
    }



    /**
     * 获取会话列表
     */
    override suspend fun getSessions(): NetworkResult<SessionsResponse> {
        val token = getAccessToken()
            ?: return Error(
                Exception("用户未登录"),
                "用户未登录，请先登录"
            )

        return when (val result = aiChatApiService.getSessions(token)) {
            is Success -> {
                Logger.i(TAG, "获取会话列表成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // 后端返回包装在SaResult的data字段中的ChatSession数组
                    val sessionsResponse = saResult.parseData<SessionsResponse>()
                    if (sessionsResponse != null) {
                        Logger.i(TAG, "会话列表解析成功: ${sessionsResponse.size}条记录")
                        Success(sessionsResponse)
                    } else {
                        Logger.e(TAG, "会话列表解析失败")
                        Error(
                            Exception("响应解析失败"),
                            "响应数据格式错误"
                        )
                    }
                } else {
                    Logger.e(TAG, "获取会话列表失败: ${saResult.msg}")
                    Error(
                        Exception(saResult.msg),
                        saResult.msg
                    )
                }
            }

            is Error -> {
                Logger.e(TAG, "获取会话列表网络请求失败: ${result.message}")
                result
            }

            is Loading -> result
            Idle -> TODO()
        }
    }

    /**
     * 获取指定会话的详细信息
     */
    override suspend fun getSession(
        sessionId: String
    ): NetworkResult<SessionDetailResponse> {
        val token = getAccessToken()
            ?: return Error(
                Exception("用户未登录"),
                "用户未登录，请先登录"
            )

        return when (val result = aiChatApiService.getSession(sessionId, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取会话详情成功: $sessionId")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val sessionDetail = saResult.parseData<SessionDetailResponse>()
                    if (sessionDetail != null) {
                        // SessionDetailResponse现在是包装在SaResult的data字段中的ChatSession
                        Logger.i(TAG, "会话详情解析成功: ${sessionDetail.messages?.size ?: 0}条消息")
                        Success(sessionDetail)
                    } else {
                        Logger.e(TAG, "会话详情解析失败")
                        Error(
                            Exception("响应解析失败"),
                            "响应数据格式错误"
                        )
                    }
                } else {
                    Logger.e(TAG, "获取会话详情失败: ${saResult.msg}")
                    Error(
                        Exception(saResult.msg),
                        saResult.msg
                    )
                }
            }

            is Error -> {
                Logger.e(TAG, "获取对话详情网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            NetworkResult.Idle -> TODO()
            is NetworkResult.Success<*> -> TODO()
        }
    }

    /**
     * 删除指定会话
     */
    override suspend fun deleteSession(
        sessionId: String
    ): NetworkResult<DeleteSessionResponse> {
        val token = getAccessToken()
            ?: return Error(
                Exception("用户未登录"),
                "用户未登录，请先登录"
            )

        return when (val result = aiChatApiService.deleteSession(sessionId, token)) {
            is Success -> {
                Logger.i(TAG, "删除会话成功: $sessionId")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val deleteResponse = saResult.parseData<DeleteSessionResponse>()
                    if (deleteResponse != null) {
                        Success(deleteResponse)
                    } else {
                        // 如果解析失败，创建一个默认的成功响应
                        Success(DeleteSessionResponse(code = 200, msg = "ok", data = null))
                    }
                } else {
                    Logger.e(TAG, "删除会话失败: ${saResult.msg}")
                    Error(
                        Exception(saResult.msg),
                        saResult.msg
                    )
                }
            }

            is Error -> {
                Logger.e(TAG, "删除会话网络请求失败: ${result.message}")
                Error(result.exception, result.message)
            }

            is Loading -> Loading
            Idle -> TODO()
        }
    }

    /**
     * 更新会话标题
     */
    override suspend fun updateSessionTitle(
        sessionId: String,
        title: String
    ): NetworkResult<Boolean> {
        val token = getAccessToken()
            ?: return Error(
                Exception("用户未登录"),
                "用户未登录，请先登录"
            )

        return when (val result =
            aiChatApiService.updateSessionTitle(sessionId, title, token)) {
            is Success -> {
                Logger.i(TAG, "更新会话标题成功: $sessionId -> $title")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Success(true)
                } else {
                    Logger.e(TAG, "更新会话标题失败: ${saResult.msg}")
                    Error(
                        Exception(saResult.msg),
                        saResult.msg
                    )
                }
            }

            is Error -> {
                Logger.e(TAG, "更新会话标题网络请求失败: ${result.message}")
                Error(result.exception, result.message)
            }

            is Loading -> Loading
            Idle -> TODO()
        }
    }

    /**
     * 获取可用的AI模型列表
     */
    override suspend fun getAvailableModels(): NetworkResult<List<String>> {
        val token = getAccessToken()
            ?: return Error(
                Exception("用户未登录"),
                "用户未登录，请先登录"
            )

        return when (val result = aiChatApiService.getAvailableModels(token)) {
            is Success -> {
                Logger.i(TAG, "获取AI模型列表成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val modelsResponse = saResult.parseData<ModelsResponse>()
                    if (modelsResponse != null) {
                        Logger.i(TAG, "AI模型列表解析成功: ${modelsResponse.size}个模型")
                        Success(modelsResponse)
                    } else {
                        Logger.e(TAG, "AI模型列表解析失败")
                        Error(
                            Exception("响应解析失败"),
                            "响应数据格式错误"
                        )
                    }
                } else {
                    Logger.e(TAG, "获取AI模型列表失败: ${saResult.msg}")
                    Error(
                        Exception(saResult.msg),
                        saResult.msg
                    )
                }
            }

            is Error -> {
                Logger.e(TAG, "获取AI模型列表网络请求失败: ${result.message}")
                result
            }

            is Loading -> result
            Idle -> TODO()
        }
    }

    /**
     * 清空所有会话历史
     */
    override suspend fun clearAllSessions(): NetworkResult<Boolean> {
        val token = getAccessToken()
            ?: return Error(
                Exception("用户未登录"),
                "用户未登录，请先登录"
            )

        return when (val result = aiChatApiService.clearAllSessions(token)) {
            is Success -> {
                Logger.i(TAG, "清空所有会话历史成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Success(true)
                } else {
                    Logger.e(TAG, "清空所有会话历史失败: ${saResult.msg}")
                    Error(
                        Exception(saResult.msg),
                        saResult.msg
                    )
                }
            }

            is Error -> {
                Logger.e(TAG, "清空所有会话历史网络请求失败: ${result.message}")
                Error(result.exception, result.message)
            }

            is Loading -> Loading
            Idle -> TODO()
        }
    }

    /**
     * 检查用户是否已登录
     */
    override suspend fun isLoggedIn(): Boolean {
        return try {
            val token = tokenStorage.getAccessToken()
            !token.isNullOrBlank()
        } catch (e: Exception) {
            Logger.e(TAG, "检查登录状态失败: ${e.message}")
            false
        }
    }

    /**
     * 获取当前用户的访问令牌
     */
    override suspend fun getAccessToken(): String? {
        return try {
            tokenStorage.getAccessToken()
        } catch (e: Exception) {
            Logger.e(TAG, "获取访问令牌失败: ${e.message}")
            null
        }
    }
}