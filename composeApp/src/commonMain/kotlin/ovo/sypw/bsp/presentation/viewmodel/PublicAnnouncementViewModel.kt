package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.AnnouncementDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.AnnouncementUseCase
import ovo.sypw.bsp.utils.Logger
import ovo.sypw.bsp.utils.PagingManager
import ovo.sypw.bsp.utils.PagingUtils

/**
 * 公告显示状态数据类
 */
data class PublicAnnouncementState(
    val isLoading: Boolean = false,
    val announcements: List<AnnouncementDto> = emptyList(),
    val allAnnouncements: List<AnnouncementDto> = emptyList(), // 存储所有原始数据
    val pageInfo: PageResultDto<AnnouncementDto>? = null,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)

/**
 * 公告筛选状态数据类
 */
data class PublicAnnouncementFilterState(
    val selectedType: Int? = null,
    val selectedPriority: Int? = null,
    val isFilterExpanded: Boolean = false
)

/**
 * 公告显示ViewModel
 * 专门用于公告显示界面的状态管理和业务逻辑
 */
class PublicAnnouncementViewModel(
    private val announcementUseCase: AnnouncementUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "PublicAnnouncementViewModel"
    }

    // 公告显示状态
    private val _announcementState = MutableStateFlow(PublicAnnouncementState())
    val announcementState: StateFlow<PublicAnnouncementState> = _announcementState.asStateFlow()

    // 搜索关键词
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 筛选状态
    private val _filterState = MutableStateFlow(PublicAnnouncementFilterState())
    val filterState: StateFlow<PublicAnnouncementFilterState> = _filterState.asStateFlow()

    // 分页管理器
    private var _pagingManager: PagingManager<AnnouncementDto>? = null
    val pagingData: StateFlow<PagingData<AnnouncementDto>>
        get() = getPagingManager().pagingData

    init {
        // 初始化时加载数据
        loadAnnouncements()
    }

    /**
     * 获取分页管理器（已废弃，现在使用本地筛选）
     */
    private fun getPagingManager(): PagingManager<AnnouncementDto> {
        if (_pagingManager == null) {
            _pagingManager = PagingUtils.createPagingManager(
                loadData = { _, _ ->
                    // 直接返回当前筛选后的数据
                    val currentState = _announcementState.value
                    val pageResult = PageResultDto(
                        records = currentState.announcements,
                        total = currentState.announcements.size.toLong(),
                        size = 10,
                        current = 1,
                        pages = 1
                    )
                    NetworkResult.Success(pageResult)
                }
            )
        }
        return _pagingManager!!
    }

    /**
     * 加载公告数据
     * @param refresh 是否为刷新操作
     */
    fun loadAnnouncements(
        refresh: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                if (refresh) {
                    _announcementState.value = _announcementState.value.copy(isRefreshing = true)
                } else {
                    _announcementState.value = _announcementState.value.copy(isLoading = true)
                }

                // 调用API获取所有已发布的公告（无参数）
                val result = announcementUseCase.getPublishedAnnouncements()

                when (result) {
                    is NetworkResult.Success -> {
                        Logger.i(TAG, "加载公告数据成功: ${result.data.records.size}条记录")
                        val allAnnouncements = result.data.records
                        
                        // 应用本地筛选
                        val filteredAnnouncements = applyLocalFilters(allAnnouncements)
                        
                        _announcementState.value = _announcementState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            allAnnouncements = allAnnouncements, // 保存所有原始数据
                            announcements = filteredAnnouncements, // 显示筛选后的数据
                            pageInfo = result.data.copy(records = filteredAnnouncements),
                            errorMessage = null
                        )
                    }
                    is NetworkResult.Error -> {
                        Logger.e(TAG, "加载公告数据失败: ${result.message}")
                        _announcementState.value = _announcementState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                    else -> {
                        _announcementState.value = _announcementState.value.copy(
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
            } catch (e: Exception) {
                Logger.e(TAG, "加载公告数据异常: ${e.message}")
                _announcementState.value = _announcementState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = "加载数据时发生错误: ${e.message}"
                )
            }
        }
    }

    /**
     * 刷新公告数据
     */
    fun refreshAnnouncements() {
        Logger.d(TAG, "刷新公告数据")
        loadAnnouncements(refresh = true)
    }

    /**
     * 应用本地筛选
     * @param announcements 原始公告列表
     * @return 筛选后的公告列表
     */
    private fun applyLocalFilters(announcements: List<AnnouncementDto>): List<AnnouncementDto> {
        val searchQuery = _searchQuery.value
        val filterState = _filterState.value
        
        return announcements.filter { announcement ->
            // 搜索筛选：根据标题搜索
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                announcement.title.contains(searchQuery, ignoreCase = true)
            }
            
            // 类型筛选
            val matchesType = filterState.selectedType?.let { selectedType ->
                announcement.type == selectedType
            } ?: true
            
            // 优先级筛选
            val matchesPriority = filterState.selectedPriority?.let { selectedPriority ->
                announcement.priority == selectedPriority
            } ?: true
            
            matchesSearch && matchesType && matchesPriority
        }
    }
    
    /**
     * 应用筛选到当前数据
     */
    private fun applyFiltersToCurrentData() {
        val currentState = _announcementState.value
        val filteredAnnouncements = applyLocalFilters(currentState.allAnnouncements)
        
        _announcementState.value = currentState.copy(
            announcements = filteredAnnouncements,
            pageInfo = currentState.pageInfo?.copy(records = filteredAnnouncements)
        )
    }

    /**
     * 更新搜索关键词
     * @param query 搜索关键词
     */
    fun updateSearchQuery(query: String) {
        Logger.d(TAG, "更新搜索关键词: $query")
        _searchQuery.value = query
        // 应用筛选到当前数据
        applyFiltersToCurrentData()
    }

    /**
     * 清空搜索条件
     */
    fun clearSearch() {
        updateSearchQuery("")
    }

    /**
     * 更新筛选条件
     * @param filterState 新的筛选状态
     */
    fun updateFilter(filterState: PublicAnnouncementFilterState) {
        Logger.d(TAG, "更新筛选条件: type=${filterState.selectedType}, priority=${filterState.selectedPriority}")
        _filterState.value = filterState
        // 应用筛选到当前数据
        applyFiltersToCurrentData()
    }

    /**
     * 切换筛选面板展开状态
     */
    fun toggleFilterExpanded() {
        _filterState.value = _filterState.value.copy(
            isFilterExpanded = !_filterState.value.isFilterExpanded
        )
    }

    /**
     * 设置类型筛选
     * @param type 公告类型，null表示不筛选
     */
    fun setTypeFilter(type: Int?) {
        Logger.d(TAG, "设置类型筛选: $type")
        _filterState.value = _filterState.value.copy(selectedType = type)
        applyFiltersToCurrentData()
    }

    /**
     * 设置优先级筛选
     * @param priority 优先级，null表示不筛选
     */
    fun setPriorityFilter(priority: Int?) {
        Logger.d(TAG, "设置优先级筛选: $priority")
        _filterState.value = _filterState.value.copy(selectedPriority = priority)
        applyFiltersToCurrentData()
    }

    /**
     * 清空所有筛选条件
     */
    fun clearAllFilters() {
        Logger.d(TAG, "清空所有筛选条件")
        _filterState.value = PublicAnnouncementFilterState()
        _searchQuery.value = ""
        applyFiltersToCurrentData()
    }

    /**
     * 清除错误消息
     */
    fun clearError() {
        _announcementState.value = _announcementState.value.copy(errorMessage = null)
    }
}