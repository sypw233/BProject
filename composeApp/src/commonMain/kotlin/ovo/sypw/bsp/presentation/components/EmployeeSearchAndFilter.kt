package ovo.sypw.bsp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.dto.Options.jobOptions
import ovo.sypw.bsp.presentation.viewmodel.admin.EmployeeFilterState
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 员工搜索和筛选组件 - 使用通用模板
 */
@Composable
fun EmployeeSearchAndFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit,
    onToggleFilterExpanded: () -> Unit,
    onClearAllFilters: () -> Unit,
    departments: List<DepartmentDto>,
    layoutConfig: ResponsiveLayoutConfig,
    modifier: Modifier = Modifier
) {
    SearchAndFilterTemplate(
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchPlaceholder = "搜索员工姓名...",
        filterState = filterState,
        onFilterChange = onFilterChange,
        onToggleFilterExpanded = onToggleFilterExpanded,
        onClearAllFilters = onClearAllFilters,
        hasActiveFilters = filterState.hasActiveFilters(),
        isFilterExpanded = filterState.isFilterExpanded,
        layoutConfig = layoutConfig,
        filterContent = { state, onChange, config ->
            EmployeeFilterContent(
                filterState = state,
                onFilterChange = onChange,
                departments = departments,
                layoutConfig = config
            )
        },
        activeFiltersContent = { onChange ->
            EmployeeActiveFilters(
                filterState = filterState,
                departments = departments,
                onRemoveFilter = { filterType ->
                    when (filterType) {
                        "gender" -> onChange(filterState.copy(selectedGender = null))
                        "job" -> onChange(filterState.copy(selectedJob = null))
                        "department" -> onChange(filterState.copy(selectedDepartmentId = null))
                        "entryDate" -> onChange(filterState.copy(entryDateStart = null, entryDateEnd = null))
                        else -> {}
                    }
                }
            )
        },
        modifier = modifier
    )
}

/**
 * 员工筛选内容
 */
@Composable
fun EmployeeFilterContent(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit,
    departments: List<DepartmentDto>,
    layoutConfig: ResponsiveLayoutConfig
) {
    val isSmallScreen = layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT
    
    if (isSmallScreen) {
        // 小屏幕垂直布局
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            EmployeeGenderFilter(filterState, onFilterChange)
            EmployeeJobFilter(filterState, onFilterChange)
            EmployeeDepartmentFilter(filterState, onFilterChange, departments)
            EmployeeEntryDateFilter(filterState, onFilterChange)
        }
    } else {
        // 大屏幕网格布局
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    EmployeeGenderFilter(filterState, onFilterChange)
                }
                Box(modifier = Modifier.weight(1f)) {
                    EmployeeJobFilter(filterState, onFilterChange)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    EmployeeDepartmentFilter(filterState, onFilterChange, departments)
                }
                Box(modifier = Modifier.weight(1f)) {
                    EmployeeEntryDateFilter(filterState, onFilterChange)
                }
            }
        }
    }
}

/**
 * 员工性别筛选
 */
@Composable
fun EmployeeGenderFilter(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit
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
 * 员工职位筛选
 */
@Composable
fun EmployeeJobFilter(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit
) {
    FilterSection(title = "职位") {
        FilterOptionsLazyRow {
            items(jobOptions.entries.toList()) { (jobId, jobName) ->
                FilterChip(
                    onClick = {
                        onFilterChange(filterState.copy(selectedJob = jobId))
                    },
                    label = { Text(jobName) },
                    selected = filterState.selectedJob == jobId
                )
            }
        }
    }
}

/**
 * 员工部门筛选
 */
@Composable
fun EmployeeDepartmentFilter(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit,
    departments: List<DepartmentDto>
) {
    FilterSection(title = "部门") {
        DropdownSelector(
            selectedItem = departments.find { it.id == filterState.selectedDepartmentId },
            items = departments,
            onItemSelected = { department ->
                onFilterChange(filterState.copy(selectedDepartmentId = department?.id))
            },
            itemLabel = { department -> department?.name ?: "全部部门" },
            placeholder = "全部部门"
        )
    }
}

/**
 * 员工入职日期筛选
 */
@Composable
fun EmployeeEntryDateFilter(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit
) {
    FilterSection(title = "入职日期") {
        DateRangeInput(
            startDate = filterState.entryDateStart,
            endDate = filterState.entryDateEnd,
            onStartDateChange = { newValue ->
                onFilterChange(filterState.copy(entryDateStart = newValue))
            },
            onEndDateChange = { newValue ->
                onFilterChange(filterState.copy(entryDateEnd = newValue))
            },
            startPlaceholder = "开始日期",
            endPlaceholder = "结束日期"
        )
    }
}

/**
 * 员工活跃筛选条件显示
 */
@Composable
fun EmployeeActiveFilters(
    filterState: EmployeeFilterState,
    departments: List<DepartmentDto>,
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
        
        // 职位筛选
        filterState.selectedJob?.let { job ->
            item {
                RemovableFilterChip(
                    label = "职位: ${filterState.getJobName(job)}",
                    onRemove = { onRemoveFilter("job") }
                )
            }
        }
        
        // 部门筛选
        filterState.selectedDepartmentId?.let { departmentId ->
            val department = departments.find { it.id == departmentId }
            department?.let {
                item {
                    RemovableFilterChip(
                        label = "部门: ${it.name}",
                        onRemove = { onRemoveFilter("department") }
                    )
                }
            }
        }
        
        // 入职日期筛选
        if (!filterState.entryDateStart.isNullOrBlank() || !filterState.entryDateEnd.isNullOrBlank()) {
            item {
                val dateRange = buildString {
                    append("入职日期: ")
                    if (!filterState.entryDateStart.isNullOrBlank()) {
                        append(filterState.entryDateStart)
                    }
                    if (!filterState.entryDateStart.isNullOrBlank() && !filterState.entryDateEnd.isNullOrBlank()) {
                        append(" ~ ")
                    }
                    if (!filterState.entryDateEnd.isNullOrBlank()) {
                        append(filterState.entryDateEnd)
                    }
                }
                RemovableFilterChip(
                    label = dateRange,
                    onRemove = { onRemoveFilter("entryDate") }
                )
            }
        }
    }
}