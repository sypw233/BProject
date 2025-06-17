package ovo.sypw.bsp.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import ovo.sypw.bsp.data.dto.AIChatRequest
import ovo.sypw.bsp.data.dto.AIChatResponse
import ovo.sypw.bsp.data.dto.ChatSession
import ovo.sypw.bsp.data.dto.SessionsResponse
import ovo.sypw.bsp.data.dto.SessionDetailResponse
import ovo.sypw.bsp.data.dto.DeleteSessionResponse
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.AIChatRepository
import ovo.sypw.bsp.utils.Logger

/**
 * AI对话用例类
 * 封装AI对话相关的业务逻辑，提供输入验证和错误处理
 */
class AIChatUseCase(
    private val aiChatRepository: AIChatRepository
) {

    companion object {
        private const val TAG = "AIChatUseCase"
        private const val MIN_MESSAGE_LENGTH = 1
        private const val MAX_MESSAGE_LENGTH = 4000
        private const val MIN_TITLE_LENGTH = 1
        private const val MAX_TITLE_LENGTH = 100
    }

    /**
     * 发送AI对话消息（非流式）
     * @param message 用户消息内容
     * @param sessionId 会话ID（可选，新对话时不传）
     * @param model AI模型名称
     * @param temperature 温度参数
     * @param maxTokens 最大令牌数
     * @return 对话响应结果
     */
    suspend fun sendMessage(
        aiChatRequest: AIChatRequest
    ): NetworkResult<AIChatResponse> {
        // 输入验证
        val validationResult = validateMessage(aiChatRequest.message)
        if (validationResult != null) {
            Logger.w(TAG, "消息验证失败: $validationResult")
            return NetworkResult.Error(
                Exception(validationResult),
                validationResult
            )
        }

//        val modelValidationResult = validateModel(model)
//        if (modelValidationResult != null) {
//            Logger.w(TAG, "模型验证失败: $modelValidationResult")
//            return NetworkResult.Error(
//                Exception(modelValidationResult),
//                modelValidationResult
//            )
//        }

//        val paramValidationResult = validateChatParameters(temperature, maxTokens)
//        if (paramValidationResult != null) {
//            Logger.w(TAG, "参数验证失败: $paramValidationResult")
//            return NetworkResult.Error(
//                Exception(paramValidationResult),
//                paramValidationResult
//            )
//        }

        // 检查登录状态
        if (!aiChatRepository.isLoggedIn()) {
            Logger.w(TAG, "用户未登录")
            return NetworkResult.Error(
                Exception("用户未登录"),
                "请先登录后再使用AI对话功能"
            )
        }

        val request = AIChatRequest(
            message = aiChatRequest.message.trim(),
            sessionId = aiChatRequest.sessionId,
//            model = model,
//            stream = false,
//            temperature = temperature,
//            maxTokens = maxTokens
        )

        Logger.i(TAG, "发送AI对话消息: ${aiChatRequest.message.take(50)}...")
        return aiChatRepository.sendMessage(request)
    }

    /**
     * 发送AI对话消息（流式传输）
     * @param message 用户消息内容
     * @param sessionId 会话ID（可选，新对话时不传）
     * @param model AI模型名称
     * @param temperature 温度参数
     * @param maxTokens 最大令牌数
     * @return 流式响应结果
     */
    suspend fun sendMessageStream(
        aiChatRequest: AIChatRequest
    ): Flow<NetworkResult<String>> {
        // 输入验证
        val validationResult = validateMessage(aiChatRequest.message)
        if (validationResult != null) {
            Logger.w(TAG, "流式消息验证失败: $validationResult")
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(
                    Exception(validationResult),
                    validationResult
                )
            )
        }

//        val modelValidationResult = validateModel(model)
//        if (modelValidationResult != null) {
//            Logger.w(TAG, "流式模型验证失败: $modelValidationResult")
//            return kotlinx.coroutines.flow.flowOf(
//                NetworkResult.Error(
//                    Exception(modelValidationResult),
//                    modelValidationResult
//                )
//            )
//        }
//
//        val paramValidationResult = validateChatParameters(temperature, maxTokens)
//        if (paramValidationResult != null) {
//            Logger.w(TAG, "流式参数验证失败: $paramValidationResult")
//            return kotlinx.coroutines.flow.flowOf(
//                NetworkResult.Error(
//                    Exception(paramValidationResult),
//                    paramValidationResult
//                )
//            )
//        }

        // 检查登录状态
        if (!aiChatRepository.isLoggedIn()) {
            Logger.w(TAG, "用户未登录")
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(
                    Exception("用户未登录"),
                    "请先登录后再使用AI对话功能"
                )
            )
        }

        val request = AIChatRequest(
            message = aiChatRequest.message.trim(),
            sessionId = aiChatRequest.sessionId,
//            model = model,
//            stream = true,
//            temperature = temperature,
//            maxTokens = maxTokens
        )

        Logger.i(TAG, "发送AI流式对话消息: ${aiChatRequest.message.take(50)}...")
        return aiChatRepository.sendMessageStream(request)
            .onStart {
                Logger.d(TAG, "开始流式传输")
            }
            .catch { exception ->
                Logger.e(TAG, "流式传输异常: ${exception.message}")
                emit(NetworkResult.Error(exception, exception.message ?: "流式传输失败"))
            }
    }

    /**
     * 获取会话列表
     * @return 会话列表结果
     */
    suspend fun getSessions(): NetworkResult<SessionsResponse> {
        // 检查登录状态
        if (!aiChatRepository.isLoggedIn()) {
            Logger.w(TAG, "用户未登录")
            return NetworkResult.Error(
                Exception("用户未登录"),
                "请先登录后再查看会话列表"
            )
        }

        Logger.i(TAG, "获取会话列表")
        return aiChatRepository.getSessions()
    }

    /**
     * 获取指定会话的详细信息
     * @param sessionId 会话ID
     * @return 会话详细信息结果
     */
    suspend fun getSession(
        sessionId: String
    ): NetworkResult<SessionDetailResponse> {
        // 输入验证
        if (sessionId.isBlank()) {
            Logger.w(TAG, "会话ID不能为空")
            return NetworkResult.Error(
                Exception("会话ID不能为空"),
                "会话ID不能为空"
            )
        }

        // 检查登录状态
        if (!aiChatRepository.isLoggedIn()) {
            Logger.w(TAG, "用户未登录")
            return NetworkResult.Error(
                Exception("用户未登录"),
                "请先登录后再查看会话详情"
            )
        }

        Logger.i(TAG, "获取会话详情: $sessionId")
        return aiChatRepository.getSession(sessionId)
    }

    /**
     * 删除指定会话
     * @param sessionId 会话ID
     * @return 删除结果
     */
    suspend fun deleteSession(
        sessionId: String
    ): NetworkResult<DeleteSessionResponse> {
        // 输入验证
        if (sessionId.isBlank()) {
            Logger.w(TAG, "会话ID不能为空")
            return NetworkResult.Error(
                Exception("会话ID不能为空"),
                "会话ID不能为空"
            )
        }

        // 检查登录状态
        if (!aiChatRepository.isLoggedIn()) {
            Logger.w(TAG, "用户未登录")
            return NetworkResult.Error(
                Exception("用户未登录"),
                "请先登录后再删除会话"
            )
        }

        Logger.i(TAG, "删除会话: $sessionId")
        return aiChatRepository.deleteSession(sessionId)
    }

    /**
     * 更新会话标题
     * @param sessionId 会话ID
     * @param title 新标题
     * @return 更新结果
     */
    suspend fun updateSessionTitle(
        sessionId: String,
        title: String
    ): NetworkResult<Boolean> {
        // 输入验证
        if (sessionId.isBlank()) {
            Logger.w(TAG, "会话ID不能为空")
            return NetworkResult.Error(
                Exception("会话ID不能为空"),
                "会话ID不能为空"
            )
        }

        val titleValidationResult = validateTitle(title)
        if (titleValidationResult != null) {
            Logger.w(TAG, "标题验证失败: $titleValidationResult")
            return NetworkResult.Error(
                Exception(titleValidationResult),
                titleValidationResult
            )
        }

        // 检查登录状态
        if (!aiChatRepository.isLoggedIn()) {
            Logger.w(TAG, "用户未登录")
            return NetworkResult.Error(
                Exception("用户未登录"),
                "请先登录后再修改会话标题"
            )
        }

        Logger.i(TAG, "更新会话标题: $sessionId -> $title")
        return aiChatRepository.updateSessionTitle(sessionId, title.trim())
    }

    /**
     * 获取可用的AI模型列表
     * @return 模型列表结果（字符串数组）
     */
    suspend fun getAvailableModels(): NetworkResult<List<String>> {
        // 检查登录状态
        if (!aiChatRepository.isLoggedIn()) {
            Logger.w(TAG, "用户未登录")
            return NetworkResult.Error(
                Exception("用户未登录"),
                "请先登录后再获取模型列表"
            )
        }

        Logger.i(TAG, "获取可用AI模型列表")
        return aiChatRepository.getAvailableModels()
    }

    /**
     * 清空所有会话历史
     * @return 清空结果
     */
    suspend fun clearAllSessions(): NetworkResult<Boolean> {
        // 检查登录状态
        if (!aiChatRepository.isLoggedIn()) {
            Logger.w(TAG, "用户未登录")
            return NetworkResult.Error(
                Exception("用户未登录"),
                "请先登录后再清空会话历史"
            )
        }

        Logger.i(TAG, "清空所有会话历史")
        return aiChatRepository.clearAllSessions()
    }

    /**
     * 验证消息内容
     */
    private fun validateMessage(message: String): String? {
        return when {
            message.isBlank() -> "消息内容不能为空"
            message.length < MIN_MESSAGE_LENGTH -> "消息内容至少需要${MIN_MESSAGE_LENGTH}个字符"
            message.length > MAX_MESSAGE_LENGTH -> "消息内容不能超过${MAX_MESSAGE_LENGTH}个字符"
            else -> null
        }
    }

    /**
     * 验证AI模型
     */
    private fun validateModel(model: String): String? {
        return when {
            model.isBlank() -> "AI模型不能为空"
            model.length > 50 -> "AI模型名称过长"
            else -> null
        }
    }

    /**
     * 验证对话参数
     */
    private fun validateChatParameters(temperature: Double, maxTokens: Int): String? {
        return when {
            temperature < 0.0 || temperature > 2.0 -> "温度参数必须在0.0-2.0之间"
            maxTokens < 1 || maxTokens > 8192 -> "最大令牌数必须在1-8192之间"
            else -> null
        }
    }



    /**
     * 验证标题
     */
    private fun validateTitle(title: String): String? {
        return when {
            title.isBlank() -> "标题不能为空"
            title.length < MIN_TITLE_LENGTH -> "标题至少需要${MIN_TITLE_LENGTH}个字符"
            title.length > MAX_TITLE_LENGTH -> "标题不能超过${MAX_TITLE_LENGTH}个字符"
            else -> null
        }
    }
}