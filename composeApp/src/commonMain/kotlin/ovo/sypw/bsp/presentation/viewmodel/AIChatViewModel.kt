package ovo.sypw.bsp.presentation.viewmodel

import com.hoc081098.kmp.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.api.AIChatApiService
import ovo.sypw.bsp.data.dto.AIChatRequest
import ovo.sypw.bsp.data.dto.ChatMessage
import ovo.sypw.bsp.data.dto.ChatSession
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.usecase.AIChatUseCase
import ovo.sypw.bsp.utils.Logger

/**
 * AI对话ViewModel
 * 管理AI对话的状态和业务逻辑
 */
class AIChatViewModel(
    private val aiChatUseCase: AIChatUseCase,
    private val aiChatApiService: AIChatApiService,
    private val tokenStorage: TokenStorage
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


    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 当前输入的消息
    private val _inputMessage = MutableStateFlow("")
    val inputMessage: StateFlow<String> = _inputMessage.asStateFlow()

    // 流式传输状态
    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    // 当前流式传输的消息内容
    private val _streamingMessage = MutableStateFlow("")
    val streamingMessage: StateFlow<String> = _streamingMessage.asStateFlow()

    // 可用模型列表
    private val _availableModels = MutableStateFlow<List<String>>(emptyList())
    val availableModels: StateFlow<List<String>> = _availableModels.asStateFlow()

    // 当前选中的模型
    private val _selectedModel = MutableStateFlow("qwq-plus")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    // 模型加载状态
    private val _isLoadingModels = MutableStateFlow(false)
    val isLoadingModels: StateFlow<Boolean> = _isLoadingModels.asStateFlow()

    init {
        loadSessions()
        loadAvailableModels()
    }

    /**
     * 更新输入消息
     */
    fun updateInputMessage(message: String) {
        _inputMessage.value = message
    }

    /**
     * 更新选中的模型
     */
    fun updateSelectedModel(model: String) {
        _selectedModel.value = model
        Logger.i("AIChatViewModel", "切换AI模型: $model")
    }

    /**
     * 加载可用模型列表
     */
    fun loadAvailableModels() {
        viewModelScope.launch {
            _isLoadingModels.value = true
            try {
                when (val result = aiChatUseCase.getAvailableModels()) {
                    is NetworkResult.Success -> {
                        _availableModels.value = result.data
                        Logger.i("AIChatViewModel", "加载模型列表成功: ${result.data.size}个模型")

                        // 如果当前选中的模型不在可用列表中，选择第一个可用模型
                        if (_selectedModel.value !in result.data && result.data.isNotEmpty()) {
                            _selectedModel.value = result.data.first()
                        }
                    }

                    is NetworkResult.Error -> {
                        Logger.e("AIChatViewModel", "加载模型列表失败: ${result.message}")
                        _errorMessage.value = "加载模型列表失败: ${result.message}"
                    }

                    is NetworkResult.Loading -> {
                        // 保持加载状态
                    }

                    NetworkResult.Idle -> {
                        // 空闲状态
                    }
                }
            } catch (e: Exception) {
                Logger.e("AIChatViewModel", "加载模型列表异常: ${e.message}")
                _errorMessage.value = "加载模型列表失败: ${e.message}"
            } finally {
                _isLoadingModels.value = false
            }
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
     * 清除错误信息
     */
    fun clearErrorMessage() {
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

    /**
     * 发送消息（流式传输）
     */
    fun sendMessage() {
        val message = _inputMessage.value.trim()
        if (message.isBlank()) return

        viewModelScope.launch {
            _isStreaming.value = true
            _errorMessage.value = null
            _streamingMessage.value = ""

            // 记录是否为新对话
            val isNewSession = _currentSessionId.value.isNullOrBlank()

            // 添加用户消息到列表
            val userMessage = ChatMessage(
                role = "user",
                message = message,
                timestamp = "1",
                sessionId = _currentSessionId.value
            )
            _messages.value = _messages.value + userMessage

            // 清空输入框
            _inputMessage.value = ""

            // 添加AI消息占位符
            val aiMessagePlaceholder = ChatMessage(
                sessionId = _currentSessionId.value,
                role = "assistant",
                message = "",
                timestamp = "2"
            )
            _messages.value = _messages.value + aiMessagePlaceholder

            try {
                val request = AIChatRequest(
                    message = message,
                    sessionId = _currentSessionId.value,
                    model = _selectedModel.value
                )

                // 获取认证令牌
                val token = tokenStorage.getAccessToken()
                if (token.isNullOrBlank()) {
                    _errorMessage.value = "请先登录"
                    _isStreaming.value = false
                    return@launch
                }

                // 收集流式响应
                aiChatApiService.sendMessageStream(request, token)
                    .catch { exception ->
                        Logger.e("AIChatViewModel", "流式传输异常: ${exception.message}")
                        _errorMessage.value = "发送消息失败: ${exception.message}"
                        _isStreaming.value = false
                    }
                    .collect { content ->
                        _streamingMessage.value += content
                        Logger.d("目前消息$content")
                        // 更新消息列表中的最后一条AI消息
                        val currentMessages = _messages.value.toMutableList()
                        if (currentMessages.isNotEmpty() && currentMessages.last().role == "assistant") {
                            currentMessages[currentMessages.size - 1] = currentMessages.last().copy(
                                message = _streamingMessage.value
                            )
                            _messages.value = currentMessages
                        }
                    }

                // 流式传输完成后，如果是新对话，需要获取最新的sessionId
                if (isNewSession) {
                    loadSessions()
                    // 获取最新的session作为当前对话的sessionId
                    val latestSession = _sessions.value.firstOrNull()
                    if (latestSession != null) {
                        _currentSessionId.value = latestSession.sessionId
                        Logger.d(
                            "AIChatViewModel",
                            "新对话创建成功，sessionId: ${latestSession.sessionId}"
                        )
                    }
                }

            } catch (e: Exception) {
                Logger.e("AIChatViewModel", "发送消息异常: ${e.message}")
                _errorMessage.value = "发送消息失败: ${e.message}"
            } finally {
                _isStreaming.value = false
                _streamingMessage.value = ""
            }
        }
    }
}