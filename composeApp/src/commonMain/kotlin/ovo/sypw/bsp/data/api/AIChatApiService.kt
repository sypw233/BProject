package ovo.sypw.bsp.data.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ovo.sypw.bsp.data.dto.AIChatRequest
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.utils.Logger

/**
 * AI对话相关的API服务
 * 提供AI对话、历史记录、模型管理等网络请求功能
 */
class AIChatApiService : BaseApiService() {
    // 移除chatPath前缀，直接使用API路径

    /**
     * 发送AI对话请求（非流式）
     * @param chatRequest 对话请求参数
     * @param token 认证令牌
     * @return 对话响应结果
     */
    suspend fun sendMessage(
        chatRequest: AIChatRequest,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d("AIChatApiService", "发送AI对话请求: ${chatRequest.message}")
        return postWithToken(
            endpoint = "/messages",
            body = chatRequest,
            token = token
        )
    }


    /**
     * 发送AI对话消息（流式响应）
     * @param chatRequest 对话请求参数
     * @param token 认证令牌
     * @return 流式响应结果
     */
    suspend fun sendMessageStream(
        chatRequest: AIChatRequest,
        token: String
    ): Flow<String> = flow {
        try {
            Logger.d("AIChatApiService", "发送AI流式对话请求: ${chatRequest.message}")
            postWithTokenStreaming(
                endpoint = "/messages",
                body = chatRequest,
                token = token
            ).collect { text ->
//                Logger.d("AI响应返回内容: $text")
                emit(text)
            }

//            Logger.d("AIChatApiService", "收到响应状态: ${response.status}")
//

//
//            // 使用流式读取响应内容（纯文本流）
//            val channel = response.bodyAsChannel()
//            val buffer = ByteArray(1024) // 使用较小的缓冲区以提高响应速度
//
//            while (!channel.isClosedForRead) {
//                val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
//                if (bytesRead > 0) {
//                    val chunk = buffer.decodeToString(0, bytesRead)
//                    // 直接发送每个字符块，实现真正的流式效果
//                    if (chunk.isNotEmpty()) {
//                        emit(chunk)
//                    }
//                }
//            }
//
//            Logger.d("AIChatApiService", "流式传输完成")

        } catch (e: Exception) {
            Logger.e("AIChatApiService", "流式请求异常: ${e.message}")
            emit("发送消息失败: ${e.message}")
        }
    }

    /**
     * 获取会话列表
     * @param token 认证令牌
     * @return 会话列表结果
     */
    suspend fun getSessions(
        token: String
    ): NetworkResult<SaResult> {
        Logger.d("AIChatApiService", "获取会话列表")
        return getWithToken(
            endpoint = "/sessions",
            token = token
        )
    }

    /**
     * 获取指定会话的详细信息
     * @param sessionId 会话ID
     * @param token 认证令牌
     * @return 会话详细信息结果
     */
    suspend fun getSession(
        sessionId: String,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d("AIChatApiService", "获取会话详情: $sessionId")
        return getWithToken(
            endpoint = "/sessions/$sessionId",
            token = token
        )
    }

    /**
     * 删除指定会话
     * @param sessionId 会话ID
     * @param token 认证令牌
     * @return 删除结果
     */
    suspend fun deleteSession(
        sessionId: String,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d("AIChatApiService", "删除会话: $sessionId")
        return deleteWithToken(
            endpoint = "/sessions/$sessionId",
            token = token
        )
    }

    /**
     * 更新会话标题
     * @param sessionId 会话ID
     * @param title 新标题
     * @param token 认证令牌
     * @return 更新结果
     */
    suspend fun updateSessionTitle(
        sessionId: String,
        title: String,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d("AIChatApiService", "更新会话标题: $sessionId -> $title")
        return putWithToken(
            endpoint = "/sessions/$sessionId/title",
            body = mapOf("title" to title),
            token = token
        )
    }

    /**
     * 获取可用的AI模型列表
     * @param token 认证令牌
     * @return 模型列表结果
     */
    suspend fun getAvailableModels(
        token: String
    ): NetworkResult<SaResult> {
        Logger.d("AIChatApiService", "获取可用AI模型列表")
        return getWithToken(
            endpoint = "/models",
            token = token
        )
    }

    /**
     * 清空所有会话历史
     * @param token 认证令牌
     * @return 清空结果
     */
    suspend fun clearAllSessions(
        token: String
    ): NetworkResult<SaResult> {
        Logger.d("AIChatApiService", "清空所有会话历史")
        return deleteWithToken(
            endpoint = "/sessions",
            token = token
        )
    }
}