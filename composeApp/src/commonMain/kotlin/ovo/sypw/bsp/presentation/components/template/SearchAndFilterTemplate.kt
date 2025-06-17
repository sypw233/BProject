package ovo.sypw.bsp.presentation.components.template

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.presentation.components.IconButton
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 通用搜索和筛选模板组件
 * @param T 筛选状态的数据类型
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchAndFilterTemplate(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchPlaceholder: String = "搜索...",
    filterState: T,
    onFilterChange: (T) -> Unit,
    onToggleFilterExpanded: () -> Unit,
    onClearAllFilters: () -> Unit,
    hasActiveFilters: Boolean,
    isFilterExpanded: Boolean,
    layoutConfig: ResponsiveLayoutConfig,
    filterContent: @Composable (T, (T) -> Unit, ResponsiveLayoutConfig) -> Unit,
    activeFiltersContent: @Composable ((T) -> Unit) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isSmallScreen = layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // 搜索栏和筛选按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 搜索框
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                searchPlaceholder = searchPlaceholder,
                modifier = Modifier.weight(1f).height(48.dp)
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
                        if (!isSmallScreen) {
                            Text("筛选")
                        }
                        // 活跃筛选条件指示器
                        if (hasActiveFilters) {
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
                selected = isFilterExpanded,
                modifier = Modifier.height(48.dp)
            )

            // 清空所有筛选条件按钮
            if (hasActiveFilters || searchQuery.isNotEmpty()) {
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
        if (hasActiveFilters) {
            Spacer(modifier = Modifier.height(8.dp))
            activeFiltersContent(onFilterChange)
        }

        // 筛选面板
        AnimatedVisibility(
            visible = isFilterExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            FilterPanel(
                filterState = filterState,
                onFilterChange = onFilterChange,
                layoutConfig = layoutConfig,
                filterContent = filterContent,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}


@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchPlaceholder: String = "搜索...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier,
        placeholder = { Text(searchPlaceholder, style = MaterialTheme.typography.bodyMedium) },
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
    )
}

/**
 * 通用筛选面板
 * @param T 筛选状态的数据类型
 */
@Composable
fun <T> FilterPanel(
    filterState: T,
    onFilterChange: (T) -> Unit,
    layoutConfig: ResponsiveLayoutConfig,
    filterContent: @Composable (T, (T) -> Unit, ResponsiveLayoutConfig) -> Unit,
    modifier: Modifier = Modifier
) {
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

        filterContent(filterState, onFilterChange, layoutConfig)
    }
}

/**
 * 筛选区域容器
 */
@Composable
fun FilterSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
        content()
    }
}

/**
 * 筛选选项行
 */
@Composable
fun FilterOptionsRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        content = content
    )
}

/**
 * 筛选选项懒加载行
 */
@Composable
fun FilterOptionsLazyRow(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        content = content
    )
}

/**
 * 活跃筛选条件显示组件
 */
@Composable
fun ActiveFiltersRow(
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

/**
 * 可移除的筛选标签
 */
@Composable
fun RemovableFilterChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onRemove,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(label)
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "移除",
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        selected = true,
        modifier = modifier
    )
}

/**
 * 日期范围输入组件
 */
@Composable
fun DateRangeInput(
    startDate: String?,
    endDate: String?,
    onStartDateChange: (String?) -> Unit,
    onEndDateChange: (String?) -> Unit,
    startPlaceholder: String = "开始日期",
    endPlaceholder: String = "结束日期",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = startDate ?: "",
                onValueChange = { newValue ->
                    onStartDateChange(newValue.takeIf { it.isNotBlank() })
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text(startPlaceholder) },
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
                textStyle = MaterialTheme.typography.bodySmall
            )

            OutlinedTextField(
                value = endDate ?: "",
                onValueChange = { newValue ->
                    onEndDateChange(newValue.takeIf { it.isNotBlank() })
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text(endPlaceholder) },
                singleLine = true,
                shape = RoundedCornerShape(6.dp),
                textStyle = MaterialTheme.typography.bodySmall
            )
        }

    }
}

/**
 * 下拉选择组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownSelector(
    selectedItem: T?,
    items: List<T>,
    onItemSelected: (T?) -> Unit,
    itemLabel: (T?) -> String,
    placeholder: String = "请选择",
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = itemLabel(selectedItem),
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(6.dp),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // 全部选项
            DropdownMenuItem(
                text = { Text(placeholder) },
                onClick = {
                    onItemSelected(null)
                    expanded = false
                }
            )

            // 具体选项
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemLabel(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}