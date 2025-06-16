package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.AnnouncementDto
import ovo.sypw.bsp.data.dto.AnnouncementCreateDto
import ovo.sypw.bsp.data.dto.AnnouncementUpdateDto
import ovo.sypw.bsp.data.dto.AnnouncementType
import ovo.sypw.bsp.data.dto.AnnouncementStatus
import ovo.sypw.bsp.data.dto.AnnouncementPriority
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.AnnouncementUseCase
import ovo.sypw.bsp.utils.Logger
import ovo.sypw.bsp.utils.PagingManager
import ovo.sypw.bsp.utils.PagingUtils

/**
 * 公告管理ViewModel
 * 负责公告相关的状态管理和业务逻辑
 */
class AnnouncementViewModel(
    private val announcementUseCase: AnnouncementUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "AnnouncementViewModel"
    }

    // 公告管理状态
    private val _announcementState = MutableStateFlow(AnnouncementState())
    val announcementState: StateFlow<AnnouncementState> = _announcementState.asStateFlow()

    // 公告Dialog状态
    private val _announcementDialogState = MutableStateFlow(AnnouncementDialogState())
    val announcementDialogState: StateFlow<AnnouncementDialogState> = _announcementDialogState.asStateFlow()

    // 公告搜索关键词
    private val _announcementSearchQuery = MutableStateFlow("")
    val announcementSearchQuery: StateFlow<String> = _announcementSearchQuery.asStateFlow()

    // 公告筛选状态
    private val _announcementFilterState = MutableStateFlow(AnnouncementFilterState())
    val announcementFilterState: StateFlow<AnnouncementFilterState> = _announcementFilterState.asStateFlow()

    // 公告分页数据流
    private var _announcementPagingManager: PagingManager<AnnouncementDto>? = null
    val announcementPagingData: StateFlow<PagingData<AnnouncementDto>>
        get() = getAnnouncementPagingManager().pagingData

    /**
     * 获取公告分页管理器
     */
    fun getAnnouncementPagingManager(): PagingManager<AnnouncementDto> {
        if (_announcementPagingManager == null) {
            _announcementPagingManager = PagingUtils.createPagingManager(
                loadData = { page, pageSize ->
                    val filterState = _announcementFilterState.value
                    announcementUseCase.getAnnouncementPage(
                        current = page,
                        size = pageSize,
                        title = _announcementSearchQuery.value.takeIf { it.isNotBlank() },
                        status = filterState.selectedStatus,
                        priority = filterState.selectedPriority,
                        type = filterState.selectedType,
                        publishTimeStart = filterState.publishTimeStart,
                        publishTimeEnd = filterState.publishTimeEnd
                    )
                }
            )
        }
        return _announcementPagingManager!!
    }

    /**
     * 更新搜索关键词并刷新分页数据
     * @param query 搜索关键词
     */
    fun updateAnnouncementSearchQuery(query: String) {
        _announcementSearchQuery.value = query
        // 重新创建分页器以应用新的搜索条件
        _announcementPagingManager = null
        // 立即加载数据
        loadAnnouncements()
    }

    /**
     * 清空搜索条件
     */
    fun clearAnnouncementSearch() {
        updateAnnouncementSearchQuery("")
    }

    /**
     * 更新筛选条件
     */
    fun updateAnnouncementFilter(filterState: AnnouncementFilterState) {
        _announcementFilterState.value = filterState
        // 重新创建分页器以应用新的筛选条件
        _announcementPagingManager = null
        // 立即加载数据
        loadAnnouncements()
    }

    /**
     * 切换筛选面板展开状态
     */
    fun toggleFilterExpanded() {
        _announcementFilterState.value = _announcementFilterState.value.copy(
            isFilterExpanded = !_announcementFilterState.value.isFilterExpanded
        )
    }

    /**
     * 清空所有筛选条件
     */
    fun clearAllFilters() {
        _announcementFilterState.value = AnnouncementFilterState()
        _announcementSearchQuery.value = ""
        // 重新创建分页器以应用清空的条件
        _announcementPagingManager = null
        // 立即加载数据
        loadAnnouncements()
    }

    /**
     * 设置状态筛选
     */
    fun setStatusFilter(status: Int?) {
        _announcementFilterState.value = _announcementFilterState.value.copy(selectedStatus = status)
        _announcementPagingManager = null
        // 立即加载数据
        loadAnnouncements()
    }

    /**
     * 设置优先级筛选
     */
    fun setPriorityFilter(priority: Int?) {
        _announcementFilterState.value = _announcementFilterState.value.copy(selectedPriority = priority)
        _announcementPagingManager = null
        // 立即加载数据
        loadAnnouncements()
    }

    /**
     * 设置公告类型筛选
     */
    fun setTypeFilter(type: Int?) {
        _announcementFilterState.value = _announcementFilterState.value.copy(selectedType = type)
        _announcementPagingManager = null
        // 立即加载数据
        loadAnnouncements()
    }

    /**
     * 设置发布日期范围筛选
     */
    fun setPublishDateFilter(startDate: String?, endDate: String?) {
        _announcementFilterState.value = _announcementFilterState.value.copy(
            publishTimeStart = startDate,
            publishTimeEnd = endDate
        )
        _announcementPagingManager = null
        // 立即加载数据
        loadAnnouncements()
    }

    /**
     * 刷新公告数据
     */
    fun refreshAnnouncements() {
        // 重新创建分页器以刷新数据
        _announcementPagingManager = null
        // 同时保持原有的加载方法以兼容现有代码
        loadAnnouncements()
    }

    /**
     * 加载公告分页数据
     * @param current 当前页码
     * @param size 每页大小
     * @param title 公告标题（可选，用于搜索）
     */
    fun loadAnnouncements(
        current: Int = 1,
        size: Int = 9,
        title: String? = null
    ) {
        viewModelScope.launch {
            _announcementState.value = _announcementState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val filterState = _announcementFilterState.value
            val searchQuery = title ?: _announcementSearchQuery.value.takeIf { it.isNotBlank() }

            when (val result = announcementUseCase.getAnnouncementPage(
                current = current,
                size = size,
                title = searchQuery,
                type = filterState.selectedType,
                priority = filterState.selectedPriority,
                status = filterState.selectedStatus,
                publishTimeStart = filterState.publishTimeStart,
                publishTimeEnd = filterState.publishTimeEnd,
                creatorId = null
            )) {
                is NetworkResult.Success -> {
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        announcements = result.data.records,
                        pageInfo = result.data,
                        errorMessage = null
                    )
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "公告数据加载失败: ${result.message}")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * 创建公告
     * @param announcementCreateDto 公告创建数据
     */
    fun createAnnouncement(announcementCreateDto: AnnouncementCreateDto) {
        Logger.d(TAG, "创建公告: title=${announcementCreateDto.title}")

        viewModelScope.launch {
            _announcementState.value = _announcementState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            when (val result = announcementUseCase.createAnnouncement(announcementCreateDto)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "公告创建成功")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载公告列表
                    loadAnnouncements()
                    // 关闭对话框
                    hideAnnouncementDialog()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "公告创建失败: ${result.message}")
                    _announcementDialogState.value = _announcementDialogState.value.copy(
                        errorMessage = result.message
                    )
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * 更新公告
     * @param announcementUpdateDto 公告更新数据
     */
    fun updateAnnouncement(announcementUpdateDto: AnnouncementUpdateDto) {
        Logger.d(TAG, "更新公告: id=${announcementUpdateDto.id}, title=${announcementUpdateDto.title}")

        viewModelScope.launch {
            _announcementState.value = _announcementState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            when (val result = announcementUseCase.updateAnnouncement(announcementUpdateDto)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "公告更新成功")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载公告列表
                    loadAnnouncements()
                    // 关闭对话框
                    hideAnnouncementDialog()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "公告更新失败: ${result.message}")
                    _announcementDialogState.value = _announcementDialogState.value.copy(
                        errorMessage = result.message
                    )
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * 删除公告
     * @param id 公告ID
     */
    fun deleteAnnouncement(id: Int?) {
        Logger.d(TAG, "删除公告: id=$id")

        viewModelScope.launch {
            _announcementState.value = _announcementState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            when (val result = announcementUseCase.deleteAnnouncement(id ?: 0)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "公告删除成功")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载公告列表
                    loadAnnouncements()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "公告删除失败: ${result.message}")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * 批量删除公告
     * @param ids 公告ID列表
     */
    fun batchDeleteAnnouncements(ids: List<Int>) {
        Logger.d(TAG, "批量删除公告: ids=$ids")

        viewModelScope.launch {
            _announcementState.value = _announcementState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            when (val result = announcementUseCase.batchDeleteAnnouncements(ids)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "公告批量删除成功")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载公告列表
                    loadAnnouncements()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "公告批量删除失败: ${result.message}")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * 发布公告
     * @param id 公告ID
     */
    fun publishAnnouncement(id: Int?) {
        Logger.d(TAG, "发布公告: id=$id")

        viewModelScope.launch {
            _announcementState.value = _announcementState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            when (val result = announcementUseCase.publishAnnouncement(id ?: 0)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "公告发布成功")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载公告列表
                    loadAnnouncements()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "公告发布失败: ${result.message}")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * 下线公告
     * @param id 公告ID
     */
    fun unpublishAnnouncement(id: Int?) {
        Logger.d(TAG, "下线公告: id=$id")

        viewModelScope.launch {
            _announcementState.value = _announcementState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            when (val result = announcementUseCase.offlineAnnouncement(id ?: 0)) {
                is NetworkResult.Success<*> -> {
                    Logger.i(TAG, "公告下线成功")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载公告列表
                    loadAnnouncements()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "公告下线失败: ${result.message}")
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _announcementState.value = _announcementState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * 清除错误消息
     */
    fun clearAnnouncementError() {
        _announcementState.value = _announcementState.value.copy(
            errorMessage = null
        )
    }

    // Dialog相关方法
    /**
     * 显示添加公告对话框
     */
    fun showAddAnnouncementDialog() {
        _announcementDialogState.value = AnnouncementDialogState(
            isVisible = true,
            isEditMode = false
        )
    }

    /**
     * 显示编辑公告对话框
     */
    fun showEditAnnouncementDialog(announcement: AnnouncementDto) {
        _announcementDialogState.value = AnnouncementDialogState(
            isVisible = true,
            isEditMode = true,
            announcementId = announcement.id,
            title = announcement.title,
            content = announcement.content,
            type = announcement.type,
            status = announcement.status,
            priority = announcement.priority
        )
    }

    /**
     * 隐藏公告对话框
     */
    fun hideAnnouncementDialog() {
        _announcementDialogState.value = AnnouncementDialogState()
    }

    /**
     * 更新公告标题
     */
    fun updateAnnouncementTitle(title: String) {
        _announcementDialogState.value = _announcementDialogState.value.copy(
            title = title,
            errorMessage = null
        )
    }

    /**
     * 更新公告内容
     */
    fun updateAnnouncementContent(content: String) {
        _announcementDialogState.value = _announcementDialogState.value.copy(
            content = content,
            errorMessage = null
        )
    }

    /**
     * 更新公告类型
     */
    fun updateAnnouncementType(type: Int) {
        _announcementDialogState.value = _announcementDialogState.value.copy(
            type = type,
            errorMessage = null
        )
    }

    /**
     * 更新公告状态
     */
    fun updateAnnouncementStatus(status: Int) {
        _announcementDialogState.value = _announcementDialogState.value.copy(
            status = status,
            errorMessage = null
        )
    }

    /**
     * 更新公告优先级
     */
    fun updateAnnouncementPriority(priority: Int) {
        _announcementDialogState.value = _announcementDialogState.value.copy(
            priority = priority,
            errorMessage = null
        )
    }

    /**
     * 保存公告
     */
    fun saveAnnouncement() {
        val dialogState = _announcementDialogState.value
        
        if (dialogState.isEditMode) {
            // 编辑模式
            val updateDto = AnnouncementUpdateDto(
                id = dialogState.announcementId ?: 0,
                title = dialogState.title,
                content = dialogState.content,
                priority = dialogState.priority,
                type = dialogState.type,
                status = dialogState.status,
            )
            updateAnnouncement(updateDto)
        } else {
            // 添加模式
            val createDto = AnnouncementCreateDto(
                title = dialogState.title,
                content = dialogState.content,
                type = dialogState.type,
                status = dialogState.status,
                priority = dialogState.priority,
                creatorId = 1 // TODO: 从当前用户获取
            )
            createAnnouncement(createDto)
        }
    }
}

/**
 * 公告管理状态
 */
data class AnnouncementState(
    val isLoading: Boolean = false,
    val announcements: List<AnnouncementDto> = emptyList(),
    val pageInfo: PageResultDto<AnnouncementDto>? = null,
    val errorMessage: String? = null
)

/**
 * 公告Dialog状态
 */
data class AnnouncementDialogState(
    val isVisible: Boolean = false,
    val isEditMode: Boolean = false,
    val announcementId: Int? = null,
    val title: String = "",
    val content: String = "",
    val type: Int = AnnouncementType.NOTIFICATION.value,
    val status: Int = AnnouncementStatus.PUBLISHED.value,
    val priority: Int = AnnouncementPriority.NORMAL.value,
    val errorMessage: String? = null
)

/**
 * 公告筛选状态
 */
data class AnnouncementFilterState(
    val selectedStatus: Int? = null,
    val selectedPriority: Int? = null,
    val selectedType: Int? = null,
    val publishTimeStart: String? = null,
    val publishTimeEnd: String? = null,
    val isFilterExpanded: Boolean = false
) {
    /**
     * 检查是否有活跃的筛选条件
     */
    fun hasActiveFilters(): Boolean {
        return selectedStatus != null ||
                selectedPriority != null ||
                selectedType != null ||
                !publishTimeStart.isNullOrBlank() ||
                !publishTimeEnd.isNullOrBlank()
    }

    /**
     * 获取状态名称
     */
    fun getStatusName(status: Int): String {
        return when (status) {
            0 -> "草稿"
            1 -> "已发布"
            2 -> "已下线"
            else -> "未知状态"
        }
    }
}