package ovo.sypw.bsp.domain.repository

import kotlinx.coroutines.flow.Flow
import ovo.sypw.bsp.data.dto.AIChatRequest
import ovo.sypw.bsp.data.dto.AIChatResponse
import ovo.sypw.bsp.data.dto.AIChatStreamResponse
import ovo.sypw.bsp.data.dto.ChatSession
import ovo.sypw.bsp.data.dto.SessionsResponse
import ovo.sypw.bsp.data.dto.SessionDetailResponse
import ovo.sypw.bsp.data.dto.DeleteSessionResponse
import ovo.sypw.bsp.data.dto.result.NetworkResult

/**
 * AI对话仓库接口
 * 定义AI对话相关的业务操作
 */
interface AIChatRepository {

    /**
     * 发送AI对话消息（非流式）
     * @param request 对话请求
     * @return 对话响应结果
     */
    suspend fun sendMessage(
        request: AIChatRequest
    ): NetworkResult<AIChatResponse>

    /**
     * 发送AI对话消息（流式传输）
     * @param request 对话请求
     * @return 流式响应结果
     */
    suspend fun sendMessageStream(
        request: AIChatRequest
    ): Flow<NetworkResult<String>>

    /**
     * 获取会话列表
     * @return 会话列表结果
     */
    suspend fun getSessions(): NetworkResult<SessionsResponse>

    /**
     * 获取指定会话的详细信息
     * @param sessionId 会话ID
     * @return 会话详细信息结果
     */
    suspend fun getSession(
        sessionId: String
    ): NetworkResult<SessionDetailResponse>

    /**
     * 删除指定会话
     * @param sessionId 会话ID
     * @return 删除结果
     */
    suspend fun deleteSession(
        sessionId: String
    ): NetworkResult<DeleteSessionResponse>

    /**
     * 更新会话标题
     * @param sessionId 会话ID
     * @param title 新标题
     * @return 更新结果
     */
    suspend fun updateSessionTitle(
        sessionId: String,
        title: String
    ): NetworkResult<Boolean>

    /**
     * 获取可用的AI模型列表
     * @return 模型列表结果（字符串数组）
     */
    suspend fun getAvailableModels(): NetworkResult<List<String>>

    /**
     * 清空所有会话历史
     * @return 清空结果
     */
    suspend fun clearAllSessions(): NetworkResult<Boolean>

    /**
     * 检查用户是否已登录
     * @return 是否已登录
     */
    suspend fun isLoggedIn(): Boolean

    /**
     * 获取当前用户的访问令牌
     * @return 访问令牌，如果未登录则返回null
     */
    suspend fun getAccessToken(): String?
}