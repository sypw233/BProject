package ovo.sypw.bsp.presentation.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.RequestMethod
import ovo.sypw.bsp.data.dto.ResponseStatusCategory
import ovo.sypw.bsp.presentation.components.template.ActiveFiltersRow
import ovo.sypw.bsp.presentation.components.template.DateRangeInput
import ovo.sypw.bsp.presentation.components.template.FilterOptionsLazyRow
import ovo.sypw.bsp.presentation.components.template.FilterSection
import ovo.sypw.bsp.presentation.components.template.RemovableFilterChip
import ovo.sypw.bsp.presentation.components.template.SearchAndFilterTemplate
import ovo.sypw.bsp.presentation.viewmodel.admin.RequestLogFilterState
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 请求日志搜索和筛选组件 - 使用通用模板
 */
@Composable
fun RequestLogSearchAndFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterState: RequestLogFilterState,
    onToggleFilterExpanded: () -> Unit,
    onFilterChange: (RequestLogFilterState) -> Unit,
    onClearAllFilters: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig,
    modifier: Modifier = Modifier
) {
    SearchAndFilterTemplate(
        searchQuery = searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchPlaceholder = "搜索请求URL...",
        filterState = filterState,
        onFilterChange = onFilterChange,
        onToggleFilterExpanded = onToggleFilterExpanded,
        onClearAllFilters = onClearAllFilters,
        hasActiveFilters = filterState.hasActiveFilters(),
        isFilterExpanded = filterState.isFilterExpanded,
        layoutConfig = layoutConfig,
        filterContent = { state, onChange, config ->
            RequestLogFilterContent(
                filterState = state,
                onFilterChange = onChange,
                layoutConfig = config
            )
        },
        activeFiltersContent = { onChange ->
            RequestLogActiveFilters(
                filterState = filterState,
                onRemoveFilter = { filterType ->
                    when (filterType) {
                        "username" -> onChange(filterState.copy(selectedUsername = null))
                        "requestMethod" -> onChange(filterState.copy(selectedRequestMethod = null))
                        "responseStatus" -> onChange(filterState.copy(selectedResponseStatus = null))
                        "responseStatusCategory" -> onChange(
                            filterState.copy(
                                selectedResponseStatusCategory = null
                            )
                        )

                        "ipAddress" -> onChange(filterState.copy(selectedIpAddress = null))
                        "createdAt" -> onChange(
                            filterState.copy(
                                createdAtStart = null,
                                createdAtEnd = null
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
 * 请求日志筛选内容
 */
@Composable
fun RequestLogFilterContent(
    filterState: RequestLogFilterState,
    onFilterChange: (RequestLogFilterState) -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    val isSmallScreen = layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT

    if (isSmallScreen) {
        // 小屏幕垂直布局
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RequestLogUsernameFilter(filterState, onFilterChange)
            RequestLogMethodFilter(filterState, onFilterChange)
            RequestLogResponseStatusFilter(filterState, onFilterChange)
            RequestLogIpAddressFilter(filterState, onFilterChange)
            RequestLogCreatedAtFilter(filterState, onFilterChange)
        }
    } else {
        // 大屏幕网格布局
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    RequestLogUsernameFilter(filterState, onFilterChange)
                }
                Box(modifier = Modifier.weight(1f)) {
                    RequestLogMethodFilter(filterState, onFilterChange)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    RequestLogResponseStatusFilter(filterState, onFilterChange)
                }
                Box(modifier = Modifier.weight(1f)) {
                    RequestLogIpAddressFilter(filterState, onFilterChange)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    RequestLogCreatedAtFilter(filterState, onFilterChange)
                }
                Box(modifier = Modifier.weight(1f)) {
                    // 空白占位
                }
            }
        }
    }
}

/**
 * 用户名筛选
 */
@Composable
fun RequestLogUsernameFilter(
    filterState: RequestLogFilterState,
    onFilterChange: (RequestLogFilterState) -> Unit
) {
    FilterSection(title = "用户名") {
        OutlinedTextField(
            label = { Text("用户名") },
            value = filterState.selectedUsername ?: "",
            onValueChange = { newValue ->
                onFilterChange(filterState.copy(selectedUsername = newValue.takeIf { it.isNotBlank() }))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 请求方法筛选
 */
@Composable
fun RequestLogMethodFilter(
    filterState: RequestLogFilterState,
    onFilterChange: (RequestLogFilterState) -> Unit
) {
    FilterSection(title = "请求方法") {
        FilterOptionsLazyRow {
            item {
                FilterChip(
                    onClick = {
                        onFilterChange(filterState.copy(selectedRequestMethod = null))
                    },
                    label = { Text("全部") },
                    selected = filterState.selectedRequestMethod == null
                )
            }
            items(RequestMethod.values().toList()) { method ->
                FilterChip(
                    onClick = {
                        onFilterChange(filterState.copy(selectedRequestMethod = method))
                    },
                    label = { Text(method.value) },
                    selected = filterState.selectedRequestMethod == method
                )
            }
        }
    }
}

/**
 * 响应状态筛选
 */
@Composable
fun RequestLogResponseStatusFilter(
    filterState: RequestLogFilterState,
    onFilterChange: (RequestLogFilterState) -> Unit
) {
    FilterSection(title = "响应状态") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // 直接输入状态码
            OutlinedTextField(
                label = { Text("状态码") },
                value = filterState.selectedResponseStatus?.toString() ?: "",
                onValueChange = { newValue ->
                    val statusCode = newValue.toIntOrNull()
                    onFilterChange(
                        filterState.copy(
                            selectedResponseStatus = statusCode,
                            selectedResponseStatusCategory = null // 清除分类筛选
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("如: 200, 404, 500") }
            )

            // 状态码分类筛选
            Text(
                "或选择分类:",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
            FilterOptionsLazyRow {
                item {
                    FilterChip(
                        onClick = {
                            onFilterChange(
                                filterState.copy(
                                    selectedResponseStatusCategory = null,
                                    selectedResponseStatus = null
                                )
                            )
                        },
                        label = { Text("全部") },
                        selected = filterState.selectedResponseStatusCategory == null && filterState.selectedResponseStatus == null
                    )
                }
                items(ResponseStatusCategory.values().toList()) { category ->
                    FilterChip(
                        onClick = {
                            onFilterChange(
                                filterState.copy(
                                    selectedResponseStatusCategory = category,
                                    selectedResponseStatus = null // 清除具体状态码
                                )
                            )
                        },
                        label = { Text(category.displayName) },
                        selected = filterState.selectedResponseStatusCategory == category
                    )
                }
            }
        }
    }
}

/**
 * IP地址筛选
 */
@Composable
fun RequestLogIpAddressFilter(
    filterState: RequestLogFilterState,
    onFilterChange: (RequestLogFilterState) -> Unit
) {
    FilterSection(title = "IP地址") {
        OutlinedTextField(
            label = { Text("IP地址") },
            value = filterState.selectedIpAddress ?: "",
            onValueChange = { newValue ->
                onFilterChange(filterState.copy(selectedIpAddress = newValue.takeIf { it.isNotBlank() }))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * 创建时间筛选
 */
@Composable
fun RequestLogCreatedAtFilter(
    filterState: RequestLogFilterState,
    onFilterChange: (RequestLogFilterState) -> Unit
) {
    FilterSection(title = "创建时间") {
        DateRangeInput(
            startDate = filterState.createdAtStart,
            endDate = filterState.createdAtEnd,
            onStartDateChange = { newValue ->
                onFilterChange(filterState.copy(createdAtStart = newValue))
            },
            onEndDateChange = { newValue ->
                onFilterChange(filterState.copy(createdAtEnd = newValue))
            },
            startPlaceholder = "开始日期",
            endPlaceholder = "结束日期"
        )
    }
}

/**
 * 请求日志活跃筛选条件显示
 */
@Composable
fun RequestLogActiveFilters(
    filterState: RequestLogFilterState,
    onRemoveFilter: (String) -> Unit
) {
    ActiveFiltersRow {
        // 用户名筛选
        filterState.selectedUsername?.let { username ->
            item {
                RemovableFilterChip(
                    label = "用户名: $username",
                    onRemove = { onRemoveFilter("username") }
                )
            }
        }

        // 请求方法筛选
        filterState.selectedRequestMethod?.let { method ->
            item {
                RemovableFilterChip(
                    label = "请求方法: ${method.value}",
                    onRemove = { onRemoveFilter("requestMethod") }
                )
            }
        }

        // 具体响应状态码筛选
        filterState.selectedResponseStatus?.let { status ->
            item {
                RemovableFilterChip(
                    label = "状态码: $status",
                    onRemove = { onRemoveFilter("responseStatus") }
                )
            }
        }

        // 响应状态分类筛选
        filterState.selectedResponseStatusCategory?.let { category ->
            item {
                RemovableFilterChip(
                    label = "响应状态: ${category.displayName}",
                    onRemove = { onRemoveFilter("responseStatusCategory") }
                )
            }
        }

        // IP地址筛选
        filterState.selectedIpAddress?.let { ipAddress ->
            item {
                RemovableFilterChip(
                    label = "IP地址: $ipAddress",
                    onRemove = { onRemoveFilter("ipAddress") }
                )
            }
        }

        // 创建时间筛选
        if (!filterState.createdAtStart.isNullOrBlank() || !filterState.createdAtEnd.isNullOrBlank()) {
            item {
                val dateRange = buildString {
                    append("创建时间: ")
                    if (!filterState.createdAtStart.isNullOrBlank()) {
                        append(filterState.createdAtStart)
                    }
                    if (!filterState.createdAtStart.isNullOrBlank() && !filterState.createdAtEnd.isNullOrBlank()) {
                        append(" ~ ")
                    }
                    if (!filterState.createdAtEnd.isNullOrBlank()) {
                        append(filterState.createdAtEnd)
                    }
                }
                RemovableFilterChip(
                    label = dateRange,
                    onRemove = { onRemoveFilter("createdAt") }
                )
            }
        }
    }
}

/**
 * RequestLogFilterState扩展函数
 */
fun RequestLogFilterState.hasActiveFilters(): Boolean {
    return selectedUserId != null ||
            !selectedUsername.isNullOrBlank() ||
            selectedRequestMethod != null ||
            selectedResponseStatus != null ||
            selectedResponseStatusCategory != null ||
            !selectedIpAddress.isNullOrBlank() ||
            !createdAtStart.isNullOrBlank() ||
            !createdAtEnd.isNullOrBlank()
}