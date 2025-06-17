package ovo.sypw.bsp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.ClassDto
import ovo.sypw.bsp.presentation.viewmodel.admin.StudentFilterState
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 学生搜索和筛选组件 - 使用通用模板
 */
@Composable
fun StudentSearchAndFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterState: StudentFilterState,
    onFilterChange: (StudentFilterState) -> Unit,
    onToggleFilterExpanded: () -> Unit,
    onClearAllFilters: () -> Unit,
    classes: List<ClassDto>,
    layoutConfig: ResponsiveLayoutConfig,
    modifier: Modifier = Modifier
) {
    SearchAndFilterTemplate(
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchPlaceholder = "搜索学生姓名或学号...",
        filterState = filterState,
        onFilterChange = onFilterChange,
        onToggleFilterExpanded = onToggleFilterExpanded,
        onClearAllFilters = onClearAllFilters,
        hasActiveFilters = filterState.hasActiveFilters(),
        isFilterExpanded = filterState.isFilterExpanded,
        layoutConfig = layoutConfig,
        filterContent = { state, onChange, config ->
            StudentFilterContent(
                filterState = state,
                onFilterChange = onChange,
                classes = classes,
                layoutConfig = config
            )
        },
        activeFiltersContent = { onChange ->
            StudentActiveFilters(
                filterState = filterState,
                classes = classes,
                onRemoveFilter = { filterType ->
                    when (filterType) {
                        "gender" -> onChange(filterState.copy(selectedGender = null))
                        "status" -> onChange(filterState.copy(selectedStatus = null))
                        "class" -> onChange(filterState.copy(selectedClassId = null))
                        "enrollmentDate" -> onChange(filterState.copy(joinDateStart = null, joinDateEnd = null))
                        else -> {}
                    }
                }
            )
        },
        modifier = modifier
    )
}

/**
 * 学生筛选内容
 */
@Composable
fun StudentFilterContent(
    filterState: StudentFilterState,
    onFilterChange: (StudentFilterState) -> Unit,
    classes: List<ClassDto>,
    layoutConfig: ResponsiveLayoutConfig
) {
    val isSmallScreen = layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT
    
    if (isSmallScreen) {
        // 小屏幕垂直布局
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StudentGenderFilter(filterState, onFilterChange)
            StudentStatusFilter(filterState, onFilterChange)
            StudentClassFilter(filterState, onFilterChange, classes)
            StudentAgeFilter(filterState, onFilterChange)
            StudentEnrollmentDateFilter(filterState, onFilterChange)
        }
    } else {
        // 大屏幕网格布局
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StudentGenderFilter(filterState, onFilterChange)
                }
                Box(modifier = Modifier.weight(1f)) {
                    StudentStatusFilter(filterState, onFilterChange)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StudentClassFilter(filterState, onFilterChange, classes)
                }
                Box(modifier = Modifier.weight(1f)) {
                    StudentAgeFilter(filterState, onFilterChange)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StudentEnrollmentDateFilter(filterState, onFilterChange)
                }
                Box(modifier = Modifier.weight(1f)) {
                    // 空白占位
                }
            }
        }
    }
}

/**
 * 学生性别筛选
 */
@Composable
fun StudentGenderFilter(
    filterState: StudentFilterState,
    onFilterChange: (StudentFilterState) -> Unit
) {
    FilterSection(title = "性别") {
        FilterOptionsRow {
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedGender = null))
                },
                label = { Text("全部") },
                selected = filterState.selectedGender == null
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedGender = 1))
                },
                label = { Text("男") },
                selected = filterState.selectedGender == 1
            )
            FilterChip(
                onClick = {
                    onFilterChange(filterState.copy(selectedGender = 2))
                },
                label = { Text("女") },
                selected = filterState.selectedGender == 2
            )
        }
    }
}

/**
 * 学生状态筛选
 */
@Composable
fun StudentStatusFilter(
    filterState: StudentFilterState,
    onFilterChange: (StudentFilterState) -> Unit
) {
    val statusOptions = mapOf(
        1 to "在读",
        2 to "已毕业",
        3 to "休学",
        4 to "退学"
    )
    
    FilterSection(title = "状态") {
        FilterOptionsLazyRow {
            items(statusOptions.entries.toList()) { (statusId, statusName) ->
                FilterChip(
                    onClick = {
                        onFilterChange(filterState.copy(selectedStatus = statusId))
                    },
                    label = { Text(statusName) },
                    selected = filterState.selectedStatus == statusId
                )
            }
        }
    }
}

/**
 * 学生班级筛选
 */
@Composable
fun StudentClassFilter(
    filterState: StudentFilterState,
    onFilterChange: (StudentFilterState) -> Unit,
    classes: List<ClassDto>
) {
    FilterSection(title = "班级") {
        DropdownSelector(
            selectedItem = classes.find { it.id == filterState.selectedClassId },
            items = classes,
            onItemSelected = { classItem ->
                onFilterChange(filterState.copy(selectedClassId = classItem?.id))
            },
            itemLabel = { classItem -> classItem?.name ?: "全部班级" },
            placeholder = "全部班级"
        )
    }
}



/**
 * 学生年龄筛选
 */
@Composable
fun StudentAgeFilter(
    filterState: StudentFilterState,
    onFilterChange: (StudentFilterState) -> Unit
) {
    FilterSection(title = "出生日期") {
        DateRangeInput(
            startDate = filterState.birthDateStart,
            endDate = filterState.birthDateEnd,
            onStartDateChange = { newValue ->
                onFilterChange(filterState.copy(birthDateStart = newValue))
            },
            onEndDateChange = { newValue ->
                onFilterChange(filterState.copy(birthDateEnd = newValue))
            },
            startPlaceholder = "开始日期",
            endPlaceholder = "结束日期"
        )
    }
}

/**
 * 学生入学日期筛选
 */
@Composable
fun StudentEnrollmentDateFilter(
    filterState: StudentFilterState,
    onFilterChange: (StudentFilterState) -> Unit
) {
    FilterSection(title = "入学日期") {
        DateRangeInput(
            startDate = filterState.joinDateStart,
            endDate = filterState.joinDateEnd,
            onStartDateChange = { newValue ->
                onFilterChange(filterState.copy(joinDateStart = newValue))
            },
            onEndDateChange = { newValue ->
                onFilterChange(filterState.copy(joinDateEnd = newValue))
            },
            startPlaceholder = "开始日期",
            endPlaceholder = "结束日期"
        )
    }
}

/**
 * 学生活跃筛选条件显示
 */
@Composable
fun StudentActiveFilters(
    filterState: StudentFilterState,
    classes: List<ClassDto>,
    onRemoveFilter: (String) -> Unit
) {
    ActiveFiltersRow {
        // 性别筛选
        filterState.selectedGender?.let { gender ->
            item {
                RemovableFilterChip(
                    label = "性别: ${filterState.getGenderName(gender)}",
                    onRemove = { onRemoveFilter("gender") }
                )
            }
        }
        
        // 状态筛选
        filterState.selectedStatus?.let { status ->
            item {
                RemovableFilterChip(
                    label = "状态: ${filterState.getStatusName(status)}",
                    onRemove = { onRemoveFilter("status") }
                )
            }
        }
        
        // 班级筛选
        filterState.selectedClassId?.let { classId ->
            val classItem = classes.find { it.id == classId }
            classItem?.let {
                item {
                    RemovableFilterChip(
                        label = "班级: ${it.name}",
                        onRemove = { onRemoveFilter("class") }
                    )
                }
            }
        }
        

        
        // 入学日期筛选
        if (!filterState.joinDateStart.isNullOrBlank() || !filterState.joinDateEnd.isNullOrBlank()) {
            item {
                val dateRange = buildString {
                    append("入学日期: ")
                    if (!filterState.joinDateStart.isNullOrBlank()) {
                        append(filterState.joinDateStart)
                    }
                    if (!filterState.joinDateStart.isNullOrBlank() && !filterState.joinDateEnd.isNullOrBlank()) {
                        append(" ~ ")
                    }
                    if (!filterState.joinDateEnd.isNullOrBlank()) {
                        append(filterState.joinDateEnd)
                    }
                }
                RemovableFilterChip(
                    label = dateRange,
                    onRemove = { onRemoveFilter("enrollmentDate") }
                )
            }
        }
    }
}