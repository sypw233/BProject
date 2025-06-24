package ovo.sypw.bsp.presentation.viewmodel.admin

import com.hoc081098.kmp.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.RequestLogDto
import ovo.sypw.bsp.data.dto.RequestMethod
import ovo.sypw.bsp.data.dto.ResponseStatusCategory
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.domain.usecase.RequestLogUseCase
import ovo.sypw.bsp.utils.Logger
import ovo.sypw.bsp.utils.PagingManager
import ovo.sypw.bsp.utils.PagingUtils

/**
 * 请求日志管理ViewModel
 * 负责处理请求日志相关的业务逻辑，包括查询、筛选、详情查看等
 */
class RequestLogViewModel(
    private val requestLogUseCase: RequestLogUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "RequestLogViewModel"
    }

    // 请求日志管理状态
    private val _requestLogState = MutableStateFlow(RequestLogState())
    val requestLogState: StateFlow<RequestLogState> = _requestLogState.asStateFlow()

    // 请求日志详情Dialog状态
    private val _requestLogDetailState = MutableStateFlow(RequestLogDetailState())
    val requestLogDetailState: StateFlow<RequestLogDetailState> =
        _requestLogDetailState.asStateFlow()

    // 请求日志搜索关键词
    private val _requestLogSearchQuery = MutableStateFlow("")
    val requestLogSearchQuery: StateFlow<String> = _requestLogSearchQuery.asStateFlow()

    // 请求日志筛选状态
    private val _requestLogFilterState = MutableStateFlow(RequestLogFilterState())
    val requestLogFilterState: StateFlow<RequestLogFilterState> =
        _requestLogFilterState.asStateFlow()

    // 请求日志分页数据流
    private var _requestLogPagingManager: PagingManager<RequestLogDto>? = null
    val requestLogPagingData: StateFlow<PagingData<RequestLogDto>>
        get() = getRequestLogPagingManager().pagingData

    /**
     * 切换筛选面板展开状态
     */
    fun toggleFilterExpanded() {
        Logger.d("当前筛选面板展开状态: ${_requestLogFilterState.value.isFilterExpanded}")
        _requestLogFilterState.value = _requestLogFilterState.value.copy(
            isFilterExpanded = !_requestLogFilterState.value.isFilterExpanded
        )
    }

    /**
     * 获取请求日志分页管理器
     */
    fun getRequestLogPagingManager(): PagingManager<RequestLogDto> {
        if (_requestLogPagingManager == null) {
            _requestLogPagingManager = PagingUtils.createPagingManager(
                loadData = { page, pageSize ->
                    val filterState = _requestLogFilterState.value
                    requestLogUseCase.getRequestLogPage(
                        current = page,
                        size = pageSize,
                        userId = filterState.selectedUserId,
                        username = filterState.selectedUsername?.takeIf { it.isNotBlank() },
                        requestMethod = filterState.selectedRequestMethod?.value,
                        requestUrl = _requestLogSearchQuery.value.takeIf { it.isNotBlank() },
                        responseStatus = filterState.selectedResponseStatus,
                        ipAddress = filterState.selectedIpAddress?.takeIf { it.isNotBlank() },
                        createdAtStart = filterState.createdAtStart,
                        createdAtEnd = filterState.createdAtEnd
                    ) as NetworkResult<PageResultDto<RequestLogDto>>
                }
            )
        }
        return _requestLogPagingManager!!
    }

    /**
     * 更新搜索关键词并刷新分页数据
     * @param query 搜索关键词（用于搜索请求URL）
     */
    fun updateRequestLogSearchQuery(query: String) {
        _requestLogSearchQuery.value = query
        // 重新创建分页器以应用新的搜索条件
        _requestLogPagingManager = null
        // 立即加载数据
        loadRequestLogs()
    }

    /**
     * 更新筛选条件并刷新分页数据
     * @param filterState 新的筛选状态
     */
    fun updateRequestLogFilter(filterState: RequestLogFilterState) {
        _requestLogFilterState.value = filterState
        // 重新创建分页器以应用新的筛选条件
        _requestLogPagingManager = null
        // 立即加载数据
        loadRequestLogs()
    }

    /**
     * 加载请求日志列表
     * @param current 当前页码
     * @param size 每页大小
     * @param searchQuery 搜索关键词
     */
    fun loadRequestLogs(
        current: Int = 1,
        size: Int = 10,
        searchQuery: String? = null
    ) {
        viewModelScope.launch {
            _requestLogState.value =
                _requestLogState.value.copy(isLoading = true, errorMessage = null)

            try {
                val filterState = _requestLogFilterState.value
                val query = searchQuery ?: _requestLogSearchQuery.value

                requestLogUseCase.getRequestLogPage(
                    current = current,
                    size = size,
                    userId = filterState.selectedUserId,
                    username = filterState.selectedUsername?.takeIf { it.isNotBlank() },
                    requestMethod = filterState.selectedRequestMethod?.value,
                    requestUrl = query.takeIf { it.isNotBlank() },
                    responseStatus = filterState.selectedResponseStatus,
                    ipAddress = filterState.selectedIpAddress?.takeIf { it.isNotBlank() },
                    createdAtStart = filterState.createdAtStart,
                    createdAtEnd = filterState.createdAtEnd
                ).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _requestLogState.value = _requestLogState.value.copy(
                                isLoading = false,
                                requestLogs = result.data.records,
                                pageInfo = result.data,
                                errorMessage = null
                            )
                            Logger.d(TAG, "请求日志列表加载成功: ${result.data.records.size}条记录")
                        }

                        is NetworkResult.Error -> {
                            _requestLogState.value = _requestLogState.value.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                            Logger.e(TAG, "请求日志列表加载失败: ${result.message}")
                        }

                        is NetworkResult.Loading -> {
                            _requestLogState.value = _requestLogState.value.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }

                        else -> {
                            // 处理其他状态
                        }
                    }
                }
            } catch (e: Exception) {
                _requestLogState.value = _requestLogState.value.copy(
                    isLoading = false,
                    errorMessage = "加载请求日志列表失败: ${e.message}"
                )
                Logger.e(TAG, "加载请求日志列表异常: ${e.message}")
            }
        }
    }

    /**
     * 刷新请求日志列表
     */
    fun refreshRequestLogs() {
        Logger.d(TAG, "刷新请求日志列表")
        loadRequestLogs()
    }

    /**
     * 显示请求日志详情
     * @param requestLog 请求日志对象
     */
    fun showRequestLogDetail(requestLog: RequestLogDto) {
        Logger.d(TAG, "显示请求日志详情: ${requestLog.id}")
        _requestLogDetailState.value = _requestLogDetailState.value.copy(
            isVisible = true,
            requestLog = requestLog,
            isLoading = false,
            errorMessage = null
        )
    }

    /**
     * 根据ID加载请求日志详情
     * @param id 日志ID
     */
    fun loadRequestLogDetail(id: Int) {
        viewModelScope.launch {
            _requestLogDetailState.value = _requestLogDetailState.value.copy(
                isVisible = true,
                isLoading = true,
                errorMessage = null
            )

            try {
                requestLogUseCase.getRequestLogById(id).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _requestLogDetailState.value = _requestLogDetailState.value.copy(
                                isLoading = false,
                                requestLog = result.data,
                                errorMessage = null
                            )
                            Logger.d(TAG, "请求日志详情加载成功: ${result.data.id}")
                        }

                        is NetworkResult.Error -> {
                            _requestLogDetailState.value = _requestLogDetailState.value.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                            Logger.e(TAG, "请求日志详情加载失败: ${result.message}")
                        }

                        is NetworkResult.Loading -> {
                            _requestLogDetailState.value = _requestLogDetailState.value.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }

                        else -> {
                            // 处理其他状态
                        }
                    }
                }
            } catch (e: Exception) {
                _requestLogDetailState.value = _requestLogDetailState.value.copy(
                    isLoading = false,
                    errorMessage = "加载请求日志详情失败: ${e.message}"
                )
                Logger.e(TAG, "加载请求日志详情异常: ${e.message}")
            }
        }
    }

    /**
     * 隐藏请求日志详情Dialog
     */
    fun hideRequestLogDetail() {
        Logger.d(TAG, "隐藏请求日志详情")
        _requestLogDetailState.value = RequestLogDetailState()
    }

    /**
     * 清理指定时间之前的请求日志
     * @param beforeDate 清理此日期之前的日志
     */
    fun cleanRequestLogs(beforeDate: String) {
        viewModelScope.launch {
            try {
                requestLogUseCase.cleanRequestLogs(beforeDate).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            Logger.d(TAG, "请求日志清理成功")
                            // 清理成功后刷新列表
                            refreshRequestLogs()
                        }

                        is NetworkResult.Error -> {
                            _requestLogState.value = _requestLogState.value.copy(
                                errorMessage = "清理请求日志失败: ${result.message}"
                            )
                            Logger.e(TAG, "请求日志清理失败: ${result.message}")
                        }

                        else -> {
                            // 处理其他状态
                        }
                    }
                }
            } catch (e: Exception) {
                _requestLogState.value = _requestLogState.value.copy(
                    errorMessage = "清理请求日志失败: ${e.message}"
                )
                Logger.e(TAG, "清理请求日志异常: ${e.message}")
            }
        }
    }

    /**
     * 重置筛选条件
     */
    fun resetFilter() {
        Logger.d(TAG, "重置筛选条件")
        _requestLogFilterState.value = RequestLogFilterState()
        _requestLogSearchQuery.value = ""
        // 重新创建分页器
        _requestLogPagingManager = null
        // 刷新数据
        refreshRequestLogs()
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _requestLogState.value = _requestLogState.value.copy(errorMessage = null)
    }
}

/**
 * 请求日志管理状态
 */
data class RequestLogState(
    val isLoading: Boolean = false,
    val requestLogs: List<RequestLogDto> = emptyList(),
    val pageInfo: PageResultDto<RequestLogDto>? = null,
    val errorMessage: String? = null
)

/**
 * 请求日志详情Dialog状态
 */
data class RequestLogDetailState(
    val isVisible: Boolean = false,
    val isLoading: Boolean = false,
    val requestLog: RequestLogDto? = null,
    val errorMessage: String? = null
)

/**
 * 请求日志筛选状态
 */
data class RequestLogFilterState(
    val selectedUserId: Int? = null,
    val selectedUsername: String? = null,
    val selectedRequestMethod: RequestMethod? = null,
    val selectedResponseStatus: Int? = null,
    val selectedResponseStatusCategory: ResponseStatusCategory? = null,
    val selectedIpAddress: String? = null,
    val createdAtStart: String? = null,
    val createdAtEnd: String? = null,
    val isFilterExpanded: Boolean = false
)