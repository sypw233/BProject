package ovo.sypw.bsp.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.dto.Options.jobOptions
import ovo.sypw.bsp.presentation.viewmodel.EmployeeFilterState
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 员工搜索和筛选组件
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    val isSmallScreen = layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        // 搜索栏和筛选按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 搜索框
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f).height(48.dp),
                placeholder = { Text("搜索员工姓名...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "清空搜索",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            
            // 筛选按钮
            FilterChip(
                onClick = onToggleFilterExpanded,
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "筛选",
                            modifier = Modifier.size(18.dp)
                        )
                        Text("筛选")
                        if (filterState.hasActiveFilters()) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                },
                selected = filterState.isFilterExpanded,
                modifier = Modifier.height(48.dp)
            )
            
            // 清空所有筛选条件按钮
            if (filterState.hasActiveFilters() || searchQuery.isNotEmpty()) {
                OutlinedButton(
                    onClick = onClearAllFilters,
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ClearAll,
                        contentDescription = "清空",
                        modifier = Modifier.size(18.dp)
                    )
                    if (!isSmallScreen) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("清空")
                    }
                }
            }
        }
        
        // 活跃筛选条件显示
        if (filterState.hasActiveFilters()) {
            Spacer(modifier = Modifier.height(8.dp))
            ActiveFiltersDisplay(
                filterState = filterState,
                departments = departments,
                onRemoveFilter = { filterType ->
                    when (filterType) {
                        "gender" -> onFilterChange(filterState.copy(selectedGender = null))
                        "job" -> onFilterChange(filterState.copy(selectedJob = null))
                        "department" -> onFilterChange(filterState.copy(selectedDepartmentId = null))
                        "entryDate" -> onFilterChange(filterState.copy(entryDateStart = null, entryDateEnd = null))
                    }
                }
            )
        }
        
        // 筛选面板
        AnimatedVisibility(
            visible = filterState.isFilterExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            FilterPanel(
                filterState = filterState,
                onFilterChange = onFilterChange,
                departments = departments,
                layoutConfig = layoutConfig,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

/**
 * 活跃筛选条件显示
 */
@Composable
fun ActiveFiltersDisplay(
    filterState: EmployeeFilterState,
    departments: List<DepartmentDto>,
    onRemoveFilter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 性别筛选
        filterState.selectedGender?.let { gender ->
            item {
                FilterChip(
                    onClick = { onRemoveFilter("gender") },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("性别: ${filterState.getGenderName(gender)}")
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "移除",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    selected = true
                )
            }
        }
        
        // 职位筛选
        filterState.selectedJob?.let { job ->
            item {
                FilterChip(
                    onClick = { onRemoveFilter("job") },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("职位: ${filterState.getJobName(job)}")
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "移除",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    selected = true
                )
            }
        }
        
        // 部门筛选
        filterState.selectedDepartmentId?.let { deptId ->
            val department = departments.find { it.id == deptId }
            item {
                FilterChip(
                    onClick = { onRemoveFilter("department") },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("部门: ${department?.name ?: "未知"}")
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "移除",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    selected = true
                )
            }
        }
        
        // 入职日期筛选
        if (!filterState.entryDateStart.isNullOrBlank() || !filterState.entryDateEnd.isNullOrBlank()) {
            item {
                FilterChip(
                    onClick = { onRemoveFilter("entryDate") },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
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
                            Text(dateRange)
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "移除",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    selected = true
                )
            }
        }
    }
}

/**
 * 筛选面板
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit,
    departments: List<DepartmentDto>,
    layoutConfig: ResponsiveLayoutConfig,
    modifier: Modifier = Modifier
) {
    val isSmallScreen = layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "筛选条件",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        if (isSmallScreen) {
            // 小屏幕垂直布局
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GenderFilterSection(filterState, onFilterChange)
                JobFilterSection(filterState, onFilterChange)
                DepartmentFilterSection(filterState, onFilterChange, departments)
                EntryDateFilterSection(filterState, onFilterChange)
            }
        } else {
            // 大屏幕网格布局
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GenderFilterSection(filterState, onFilterChange)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        JobFilterSection(filterState, onFilterChange)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DepartmentFilterSection(filterState, onFilterChange, departments)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        EntryDateFilterSection(filterState, onFilterChange)
                    }
                }
            }
        }
    }
}

/**
 * 性别筛选区域
 */
@Composable
fun GenderFilterSection(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "性别",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
 * 职位筛选区域
 */
@Composable
fun JobFilterSection(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "职位",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )



        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
 * 部门筛选区域
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentFilterSection(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit,
    departments: List<DepartmentDto>
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "部门",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = departments.find { it.id == filterState.selectedDepartmentId }?.name ?: "全部部门",
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("全部部门") },
                    onClick = {
                        onFilterChange(filterState.copy(selectedDepartmentId = null))
                        expanded = false
                    }
                )
                departments.forEach { department ->
                    DropdownMenuItem(
                        text = { Text(department.name) },
                        onClick = {
                            onFilterChange(filterState.copy(selectedDepartmentId = department.id))
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * 入职日期筛选区域
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDateFilterSection(
    filterState: EmployeeFilterState,
    onFilterChange: (EmployeeFilterState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "入职日期",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = filterState.entryDateStart ?: "",
                onValueChange = { newValue ->
                    onFilterChange(
                        filterState.copy(entryDateStart = newValue.takeIf { it.isNotBlank() })
                    )
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("开始日期", style = MaterialTheme.typography.bodySmall) },
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
                textStyle = MaterialTheme.typography.bodySmall
            )
            
            OutlinedTextField(
                value = filterState.entryDateEnd ?: "",
                onValueChange = { newValue ->
                    onFilterChange(
                        filterState.copy(entryDateEnd = newValue.takeIf { it.isNotBlank() })
                    )
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("结束日期", style = MaterialTheme.typography.bodySmall) },
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
                textStyle = MaterialTheme.typography.bodySmall
            )
        }
    }
}