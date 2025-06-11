package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.ExampleRepository

/**
 * API测试页面的ViewModel
 * 负责处理API请求和状态管理
 */
class ApiTestViewModel(
    private val repository: ExampleRepository
) : ViewModel() {
    
    // 私有可变状态
    private val _uiState = MutableStateFlow(ApiTestUiState())
    
    // 公开只读状态
    val uiState: StateFlow<ApiTestUiState> = _uiState.asStateFlow()
    
    /**
     * 执行GET请求测试
     */
    fun testGetRequest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            try {
                when (val result = repository.getExampleData()) {
                    is NetworkResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            getResult = "GET请求成功: ${result.data}",
                            error = null
                        )
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "GET请求失败: ${result.message}"
                        )
                    }
                    is NetworkResult.Loading -> {
                        // 处理加载状态
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Idle -> {
                        // 处理空闲状态
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "GET请求异常: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 执行POST请求测试
     * @param data 要发送的数据
     */
    fun testPostRequest(data: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            try {
                when (val result = repository.postExampleData(data)) {
                    is NetworkResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            postResult = "POST请求成功: ${result.data}",
                            error = null
                        )
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "POST请求失败: ${result.message}"
                        )
                    }
                    is NetworkResult.Loading -> {
                        // 处理加载状态
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Idle -> {
                        // 处理空闲状态
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "POST请求异常: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * 清除所有结果
     */
    fun clearResults() {
        _uiState.value = _uiState.value.copy(
            getResult = null,
            postResult = null,
            error = null
        )
    }
}

/**
 * API测试页面的UI状态
 */
data class ApiTestUiState(
    val isLoading: Boolean = false,
    val getResult: String? = null,
    val postResult: String? = null,
    val error: String? = null
)