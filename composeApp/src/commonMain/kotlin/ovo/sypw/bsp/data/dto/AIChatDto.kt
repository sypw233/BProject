package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * AI对话请求数据传输对象
 */
@Serializable
data class AIChatRequest(
    /**
     * 用户消息内容
     */
    val message: String,

    /**
     * 会话ID（可选，新对话时不提供）
     */
    val sessionId: String? = null,

    val model: String = "qwq-plus",

    )

/**
 * AI对话响应数据传输对象（流式返回的文本内容）
 */
@Serializable
data class AIChatResponse(
    /**
     * AI回复内容（流式文本）
     */
    val content: String
)

/**
 * 流式响应数据传输对象
 */
@Serializable
data class AIChatStreamResponse(
    /**
     * 增量内容
     */
    val delta: String,

    /**
     * 是否完成
     */
    val finished: Boolean = false,

    /**
     * 错误信息（如果有）
     */
    val error: String? = null
)

/**
 * 对话历史消息数据传输对象
 */
@Serializable
data class ChatMessage(
    /**
     * 会话ID
     */
    val sessionId: String?,

    /**
     * 消息内容
     */
    val message: String,

    /**
     * 消息角色（user/assistant）
     */
    val role: String,

    /**
     * 消息时间戳
     */
    val timestamp: String
)

/**
 * 对话会话数据传输对象
 */
@Serializable
data class ChatSession(
    /**
     * 会话ID
     */
    val sessionId: String,

    /**
     * 对话标题
     */
    val title: String,

    /**
     * 创建时间
     */
    val createdAt: String,

    /**
     * 消息列表（获取会话详情时包含，列表时为null）
     */
    val messages: List<ChatMessage>? = null
)

/**
 * 会话列表响应
 * 后端返回包装在SaResult中的ChatSession数组
 */
typealias SessionsResponse = List<ChatSession>

/**
 * 会话详情响应
 * 后端返回包装在SaResult中的ChatSession对象
 */
typealias SessionDetailResponse = ChatSession

/**
 * 删除会话响应
 */
@Serializable
data class DeleteSessionResponse(
    /**
     * 响应状态码
     */
    val code: Int,

    /**
     * 响应消息
     */
    val msg: String,

    /**
     * 响应数据（通常为null）
     */
    val data: String? = null
)

/**
 * 获取可用模型响应
 * 后端直接返回字符串数组，包装在SaResult的data字段中
 */
typealias ModelsResponse = List<String>