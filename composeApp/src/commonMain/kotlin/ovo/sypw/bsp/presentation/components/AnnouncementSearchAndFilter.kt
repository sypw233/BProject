package ovo.sypw.bsp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.presentation.viewmodel.AnnouncementFilterState
import ovo.sypw.bsp.data.dto.AnnouncementStatus
import ovo.sypw.bsp.data.dto.AnnouncementType
import ovo.sypw.bsp.data.dto.AnnouncementPriority
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 公告搜索和筛选组件 - 使用通用模板
 */
@Composable
fun AnnouncementSearchAndFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterState: AnnouncementFilterState,
    onFilterChange: (AnnouncementFilterState) -> Unit,
    onToggleFilterExpanded: () -> Unit,
    onClearAllFilters: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig,
    modifier: Modifier = Modifier
) {
    SearchAndFilterTemplate(
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchPlaceholder = "搜索公告标题...",
        filterState = filterState,
        onFilterChange = onFilterChange,
        onToggleFilterExpanded = onToggleFilterExpanded,
        onClearAllFilters = onClearAllFilters,
        hasActiveFilters = filterState.hasActiveFilters(),
        isFilterExpanded = filterState.isFilterExpanded,
        layoutConfig = layoutConfig,
        filterContent = { state, onChange, config ->
            AnnouncementFilterContent(
                filterState = state,
                onFilterChange = onChange,
                layoutConfig = config
            )
        },
        activeFiltersContent = { onChange ->
            AnnouncementActiveFilters(
                filterState = filterState,
                onRemoveFilter = { filterType ->
                    when (filterType) {
                        "status" -> onChange(filterState.copy(selectedStatus = null))
                        "priority" -> onChange(filterState.copy(selectedPriority = null))
                        "type" -> onChange(filterState.copy(selectedType = null))
                        "publishDate" -> onChange(
                            filterState.copy(
                                publishTimeStart = null,
                                publishTimeEnd = null
                            )
                        )

                        else -> {}
                    }
                }
            )
        },
        modifier = modifier
    )
}

/**
 * 公告筛选内容
 */
@Composable
fun AnnouncementFilterContent(
    filterState: AnnouncementFilterState,
    onFilterChange: (AnnouncementFilterState) -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    val isSmallScreen = layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT

    if (isSmallScreen) {
        // 小屏幕垂直布局
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AnnouncementStatusFilter(filterState, onFilterChange)
            AnnouncementPriorityFilter(filterState, onFilterChange)
            AnnouncementTypeFilter(filterState, onFilterChange)
            AnnouncementPublishDateFilter(filterState, onFilterChange)
        }
    } else {
        // 大屏幕网格布局
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AnnouncementStatusFilter(filterState, onFilterChange)
                }
                Box(modifier = Modifier.weight(1f)) {
                    AnnouncementPriorityFilter(filterState, onFilterChange)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AnnouncementTypeFilter(filterState, onFilterChange)
                }
                Box(modifier = Modifier.weight(1f)) {
                    AnnouncementPublishDateFilter(filterState, onFilterChange)
                }
            }
        }
    }
}

/**
 * 公告状态筛选
 */
@Composable
fun AnnouncementStatusFilter(
    filterState: AnnouncementFilterState,
    onFilterChange: (AnnouncementFilterState) -> Unit
) {
    FilterSection(title = "状态") {
        FilterOptionsRow {
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedStatus = null))
                },
                label = { Text("全部") },
                selected = filterState.selectedStatus == null
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedStatus = AnnouncementStatus.DRAFT.value))
                },
                label = { Text(AnnouncementStatus.DRAFT.displayName) },
                selected = filterState.selectedStatus == AnnouncementStatus.DRAFT.value
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedStatus = AnnouncementStatus.PUBLISHED.value))
                },
                label = { Text(AnnouncementStatus.PUBLISHED.displayName) },
                selected = filterState.selectedStatus == AnnouncementStatus.PUBLISHED.value
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedStatus = AnnouncementStatus.OFFLINE.value))
                },
                label = { Text(AnnouncementStatus.OFFLINE.displayName) },
                selected = filterState.selectedStatus == AnnouncementStatus.OFFLINE.value
            )
        }
    }
}

/**
 * 公告优先级筛选
 */
@Composable
fun AnnouncementPriorityFilter(
    filterState: AnnouncementFilterState,
    onFilterChange: (AnnouncementFilterState) -> Unit
) {
    FilterSection(title = "优先级") {
        FilterOptionsRow {
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedPriority = null))
                },
                label = { Text("全部") },
                selected = filterState.selectedPriority == null
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedPriority = AnnouncementPriority.NORMAL.value))
                },
                label = { Text(AnnouncementPriority.NORMAL.displayName) },
                selected = filterState.selectedPriority == AnnouncementPriority.NORMAL.value
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedPriority = AnnouncementPriority.IMPORTANT.value))
                },
                label = { Text(AnnouncementPriority.IMPORTANT.displayName) },
                selected = filterState.selectedPriority == AnnouncementPriority.IMPORTANT.value
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedPriority = AnnouncementPriority.URGENT.value))
                },
                label = { Text(AnnouncementPriority.URGENT.displayName) },
                selected = filterState.selectedPriority == AnnouncementPriority.URGENT.value
            )
        }
    }
}

/**
 * 公告类型筛选
 */
@Composable
fun AnnouncementTypeFilter(
    filterState: AnnouncementFilterState,
    onFilterChange: (AnnouncementFilterState) -> Unit
) {
    FilterSection(title = "公告类型") {
        FilterOptionsRow {
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedType = null))
                },
                label = { Text("全部") },
                selected = filterState.selectedType == null
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedType = AnnouncementType.NOTIFICATION.value))
                },
                label = { Text(AnnouncementType.NOTIFICATION.displayName) },
                selected = filterState.selectedType == AnnouncementType.NOTIFICATION.value
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedType = AnnouncementType.ACTIVITY.value))
                },
                label = { Text(AnnouncementType.ACTIVITY.displayName) },
                selected = filterState.selectedType == AnnouncementType.ACTIVITY.value
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedType = AnnouncementType.SYSTEM.value))
                },
                label = { Text(AnnouncementType.SYSTEM.displayName) },
                selected = filterState.selectedType == AnnouncementType.SYSTEM.value
            )
        }
    }
}

/**
 * 公告发布日期筛选
 */
@Composable
fun AnnouncementPublishDateFilter(
    filterState: AnnouncementFilterState,
    onFilterChange: (AnnouncementFilterState) -> Unit
) {
    FilterSection(title = "发布日期") {
        DateRangeInput(
            startDate = filterState.publishTimeStart,
            endDate = filterState.publishTimeEnd,
            onStartDateChange = { newValue ->
                onFilterChange(filterState.copy(publishTimeStart = newValue))
            },
            onEndDateChange = { newValue ->
                onFilterChange(filterState.copy(publishTimeEnd = newValue))
            },
            startPlaceholder = "开始日期",
            endPlaceholder = "结束日期"
        )
    }
}

/**
 * 公告活跃筛选条件显示
 */
@Composable
fun AnnouncementActiveFilters(
    filterState: AnnouncementFilterState,
    onRemoveFilter: (String) -> Unit
) {
    ActiveFiltersRow {
        // 状态筛选
        filterState.selectedStatus?.let { status ->
            item {
                RemovableFilterChip(
                    label = "状态: ${filterState.getStatusName(status)}",
                    onRemove = { onRemoveFilter("status") }
                )
            }
        }

        // 优先级筛选
        filterState.selectedPriority?.let { priority ->
            item {
                val priorityName = when (priority) {
                    AnnouncementPriority.NORMAL.value -> AnnouncementPriority.NORMAL.displayName
                    AnnouncementPriority.IMPORTANT.value -> AnnouncementPriority.IMPORTANT.displayName
                    AnnouncementPriority.URGENT.value -> AnnouncementPriority.URGENT.displayName
                    else -> "未知"
                }
                RemovableFilterChip(
                    label = "优先级: $priorityName",
                    onRemove = { onRemoveFilter("priority") }
                )
            }
        }

        // 类型筛选
        filterState.selectedType?.let { type ->
            item {
                val typeName = when (type) {
                    AnnouncementType.NOTIFICATION.value -> AnnouncementType.NOTIFICATION.displayName
                    AnnouncementType.ACTIVITY.value -> AnnouncementType.ACTIVITY.displayName
                    AnnouncementType.SYSTEM.value -> AnnouncementType.SYSTEM.displayName
                    else -> "未知"
                }
                RemovableFilterChip(
                    label = "类型: $typeName",
                    onRemove = { onRemoveFilter("type") }
                )
            }
        }

        // 发布日期筛选
        if (!filterState.publishTimeStart.isNullOrBlank() || !filterState.publishTimeEnd.isNullOrBlank()) {
            item {
                val dateRange = buildString {
                    append("发布日期: ")
                    if (!filterState.publishTimeStart.isNullOrBlank()) {
                        append(filterState.publishTimeStart)
                    }
                    if (!filterState.publishTimeStart.isNullOrBlank() && !filterState.publishTimeEnd.isNullOrBlank()) {
                        append(" ~ ")
                    }
                    if (!filterState.publishTimeEnd.isNullOrBlank()) {
                        append(filterState.publishTimeEnd)
                    }
                }
                RemovableFilterChip(
                    label = dateRange,
                    onRemove = { onRemoveFilter("publishDate") }
                )
            }
        }
    }
}