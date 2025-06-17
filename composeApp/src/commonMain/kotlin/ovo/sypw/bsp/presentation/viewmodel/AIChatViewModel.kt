package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.AIChatRequest
import ovo.sypw.bsp.data.dto.AIChatStreamResponse
import ovo.sypw.bsp.data.dto.ChatMessage
import ovo.sypw.bsp.data.dto.ChatSession
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.usecase.AIChatUseCase
import ovo.sypw.bsp.utils.Logger

/**
 * AI对话ViewModel
 * 管理AI对话的状态和业务逻辑
 */
class AIChatViewModel(
    private val aiChatUseCase: AIChatUseCase
) : ViewModel() {

    // 当前会话ID
    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    // 当前对话消息列表
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // 会话列表
    private val _sessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val sessions: StateFlow<List<ChatSession>> = _sessions.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 发送消息状态
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 当前输入的消息
    private val _inputMessage = MutableStateFlow("")
    val inputMessage: StateFlow<String> = _inputMessage.asStateFlow()

    // 流式响应状态
    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    init {
        loadSessions()
    }

    /**
     * 更新输入消息
     */
    fun updateInputMessage(message: String) {
        _inputMessage.value = message
    }

    /**
     * 发送消息（非流式）
     */
    fun sendMessage(message: String, sessionId: String? = null) {
        if (message.isBlank()) return

        viewModelScope.launch {
            _isSending.value = true
            _errorMessage.value = null

            try {
                val request = AIChatRequest(
                    message = message,
                    sessionId = sessionId ?: _currentSessionId.value
                )

                when (val result = aiChatUseCase.sendMessage(request)) {
                    is NetworkResult.Success -> {
                        val response = result.data


                        // 添加用户消息和AI回复到消息列表
                        val userMessage = ChatMessage(
                            sessionId = null,
                            role = "user",
                            message = message,
                            timestamp = "1",
                        )
                        
                        val aiMessage = ChatMessage(
                            sessionId = null,
                            role = "assistant",
                            message = response.content,
                            timestamp = "2",
                        )

                        _messages.value = _messages.value + listOf(userMessage, aiMessage)
                        _inputMessage.value = ""
                        
                        // 刷新会话列表
                        loadSessions()
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                        Logger.e("发送消息失败: ${result.message}")
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                _errorMessage.value = "发送消息时发生错误: ${e.message}"
                Logger.e("发送消息异常", e.toString())
            } finally {
                _isSending.value = false
            }
        }
    }

    /**
     * 发送消息（流式）
     */
    fun sendMessageStream(message: String, sessionId: String? = null) {
        if (message.isBlank()) return

        viewModelScope.launch {
            _isSending.value = true
            _isStreaming.value = true
            _errorMessage.value = null

            try {
                val request = AIChatRequest(
                    message = message,
                    sessionId = sessionId ?: _currentSessionId.value
                )

                // 添加用户消息
                val userMessage = ChatMessage(
                    sessionId = null,
                    role = "user",
                    message = message,
                    timestamp = "1"
                )
                _messages.value = _messages.value + userMessage
                _inputMessage.value = ""

                // 创建AI消息占位符
                val aiMessagePlaceholder = ChatMessage(
                    sessionId = null,
                    role = "assistant",
                    message = "",
                    timestamp = "2"
                )
                _messages.value = _messages.value + aiMessagePlaceholder
                val aiMessageIndex = _messages.value.size - 1

                // 获取流式响应
                aiChatUseCase.sendMessageStream(request)
                    .catch { e ->
                        _errorMessage.value = "流式响应错误: ${e.message}"
                        Logger.e("流式响应异常", e.toString())
                    }
                    .collect { result ->
                        when (result) {
                            is NetworkResult.Success -> {
                                handleStreamResponse(result.data, aiMessageIndex)
                            }
                            is NetworkResult.Error -> {
                                _errorMessage.value = result.message
                                Logger.e("流式响应错误", result.message)
                            }
                            else -> {}
                        }
                    }
            } catch (e: Exception) {
                _errorMessage.value = "发送流式消息时发生错误: ${e.message}"
                Logger.e("发送流式消息异常", e.toString())
                // 移除占位符消息
                _messages.value = _messages.value.dropLast(1)
            } finally {
                _isSending.value = false
                _isStreaming.value = false
                // 刷新会话列表并获取最新的sessionId
                if (_currentSessionId.value == null) {
                    updateCurrentSessionId()
                }
                loadSessions()
            }
        }
    }

    /**
     * 处理流式响应
     */
    private fun handleStreamResponse(responseText: String, messageIndex: Int) {
        val currentMessages = _messages.value.toMutableList()
        if (messageIndex < currentMessages.size) {
            val currentMessage = currentMessages[messageIndex]
            
            // 更新消息内容
            val updatedMessage = currentMessage.copy(
                message = currentMessage.message + responseText
            )
            currentMessages[messageIndex] = updatedMessage
            _messages.value = currentMessages
        }
    }

    /**
     * 更新当前会话ID（从最新的会话中获取）
     */
    private suspend fun updateCurrentSessionId() {
        try {
            when (val result = aiChatUseCase.getSessions()) {
                is NetworkResult.Success -> {
                    // SessionsResponse现在直接是List<ChatSession>
                    val latestSession = result.data.firstOrNull()
                    if (latestSession != null) {
                        _currentSessionId.value = latestSession.sessionId
                        Logger.i("AIChatViewModel", "更新当前会话ID: ${latestSession.sessionId}")
                    }
                }
                else -> {
                    Logger.e("AIChatViewModel", "获取最新会话ID失败")
                }
            }
        } catch (e: Exception) {
            Logger.e("AIChatViewModel", "更新会话ID异常: ${e.message}")
        }
    }

    /**
     * 加载会话列表
     */
    fun loadSessions() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                when (val result = aiChatUseCase.getSessions()) {
                    is NetworkResult.Success -> {
                        // SessionsResponse现在直接是List<ChatSession>
                        _sessions.value = result.data
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                        Logger.e("加载会话列表失败: ${result.message}")
                    }

                    NetworkResult.Idle -> TODO()
                    NetworkResult.Loading -> TODO()
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载会话列表时发生错误: ${e.message}"
                Logger.e("加载会话列表异常", e.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 加载指定会话的消息
     */
    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                when (val result = aiChatUseCase.getSession(sessionId)) {
                    is NetworkResult.Success -> {
                        _currentSessionId.value = sessionId
                        _messages.value = result.data.messages ?: emptyList()
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                        Logger.e("加载会话失败: ${result.message}")
                    }

                    NetworkResult.Idle -> TODO()
                    NetworkResult.Loading -> TODO()
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载会话时发生错误: ${e.message}"
                Logger.e("加载会话异常", e.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 创建新会话
     */
    fun createNewSession() {
        _currentSessionId.value = null
        _messages.value = emptyList()
        _errorMessage.value = null
    }

    /**
     * 删除会话
     */
    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            _errorMessage.value = null

            try {
                when (val result = aiChatUseCase.deleteSession(sessionId)) {
                    is NetworkResult.Success -> {
                        // 如果删除的是当前会话，清空消息
                        if (_currentSessionId.value == sessionId) {
                            createNewSession()
                        }
                        // 刷新会话列表
                        loadSessions()
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                        Logger.e("删除会话失败: ${result.message}")
                    }

                    NetworkResult.Idle -> TODO()
                    NetworkResult.Loading -> TODO()
                }
            } catch (e: Exception) {
                _errorMessage.value = "删除会话时发生错误: ${e.message}"
                Logger.e("删除会话异常", e.toString())
            }
        }
    }

    /**
     * 更新会话标题
     */
    fun updateSessionTitle(sessionId: String, title: String) {
        viewModelScope.launch {
            _errorMessage.value = null

            try {
                when (val result = aiChatUseCase.updateSessionTitle(sessionId, title)) {
                    is NetworkResult.Success -> {
                        // 刷新会话列表
                        loadSessions()
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                        Logger.e("更新会话标题失败: ${result.message}")
                    }

                    NetworkResult.Idle -> TODO()
                    NetworkResult.Loading -> TODO()
                }
            } catch (e: Exception) {
                _errorMessage.value = "更新会话标题时发生错误: ${e.message}"
                Logger.e("更新会话标题异常", e.toString())
            }
        }
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * 清空所有会话
     */
    fun clearAllSessions() {
        viewModelScope.launch {
            _errorMessage.value = null

            try {
                when (val result = aiChatUseCase.clearAllSessions()) {
                    is NetworkResult.Success -> {
                        createNewSession()
                        loadSessions()
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                        Logger.e("清空所有会话失败: ${result.message}")
                    }

                    NetworkResult.Idle -> TODO()
                    NetworkResult.Loading -> TODO()
                }
            } catch (e: Exception) {
                _errorMessage.value = "清空所有会话时发生错误: ${e.message}"
                Logger.e("清空所有会话异常", e.toString())
            }
        }
    }
}