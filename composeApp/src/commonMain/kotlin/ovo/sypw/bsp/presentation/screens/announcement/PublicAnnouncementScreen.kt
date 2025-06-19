package ovo.sypw.bsp.presentation.screens.announcement

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ovo.sypw.bsp.data.dto.AnnouncementDto
import ovo.sypw.bsp.data.dto.AnnouncementPriority
import ovo.sypw.bsp.data.dto.AnnouncementType
import ovo.sypw.bsp.presentation.viewmodel.PublicAnnouncementFilterState
import ovo.sypw.bsp.presentation.viewmodel.PublicAnnouncementViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 公告显示界面
 * 用于展示已发布的公告列表，无需登录即可查看
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicAnnouncementScreen(
    modifier: Modifier = Modifier,
    layoutConfig: ResponsiveLayoutConfig,
    onAnnouncementClick: (AnnouncementDto) -> Unit = {}
) {
    val viewModel: PublicAnnouncementViewModel = koinInject()
    val announcementState by viewModel.announcementState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterState by viewModel.filterState.collectAsState()


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 搜索和筛选区域
        PublicAnnouncementSearchAndFilter(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::updateSearchQuery,
            filterState = filterState,
            onFilterStateChange = viewModel::updateFilter,
            onToggleFilter = viewModel::toggleFilterExpanded,
            onClearFilters = viewModel::clearAllFilters,
            layoutConfig = layoutConfig
        )

        // 公告列表
        Box(modifier = Modifier.weight(1f)) {
            when {
                announcementState.isLoading && announcementState.announcements.isEmpty() -> {
                    // 初始加载状态
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "正在加载公告...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                announcementState.errorMessage != null -> {
                    // 错误状态
                    PublicAnnouncementErrorState(
                        errorMessage = announcementState.errorMessage!!,
                        onRetry = { viewModel.refreshAnnouncements() },
                        onClearError = { viewModel.clearError() }
                    )
                }

                announcementState.announcements.isEmpty() -> {
                    // 空状态
                    PublicAnnouncementEmptyState(
                        onRefresh = { viewModel.refreshAnnouncements() }
                    )
                }

                else -> {
                    // 公告列表
                    PublicAnnouncementList(
                        announcements = announcementState.announcements,
                        isRefreshing = announcementState.isRefreshing,
                        onRefresh = { viewModel.refreshAnnouncements() },
                        layoutConfig = layoutConfig,
                        onAnnouncementClick = onAnnouncementClick
                    )
                }
            }
        }
    }
}

/**
 * 公告搜索和筛选组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicAnnouncementSearchAndFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterState: PublicAnnouncementFilterState,
    onFilterStateChange: (PublicAnnouncementFilterState) -> Unit,
    onToggleFilter: () -> Unit,
    onClearFilters: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 搜索框和筛选按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("搜索公告标题...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "清空搜索"
                                )
                            }
                        }
                    },
                    singleLine = true
                )

                // 筛选按钮
                IconButton(
                    onClick = onToggleFilter
                ) {
                    Icon(
                        imageVector = if (filterState.isFilterExpanded) Icons.Default.FilterListOff else Icons.Default.FilterList,
                        contentDescription = if (filterState.isFilterExpanded) "收起筛选" else "展开筛选",
                        tint = if (filterState.selectedType != null || filterState.selectedPriority != null) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // 筛选面板
            AnimatedVisibility(visible = filterState.isFilterExpanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                    // 筛选选项
                    if (layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT) {
                        // 紧凑布局：垂直排列
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PublicAnnouncementTypeFilter(
                                selectedType = filterState.selectedType,
                                onTypeSelected = { type ->
                                    onFilterStateChange(filterState.copy(selectedType = type))
                                }
                            )

                            PublicAnnouncementPriorityFilter(
                                selectedPriority = filterState.selectedPriority,
                                onPrioritySelected = { priority ->
                                    onFilterStateChange(filterState.copy(selectedPriority = priority))
                                }
                            )
                        }
                    } else {
                        // 宽松布局：水平排列
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                PublicAnnouncementTypeFilter(
                                    selectedType = filterState.selectedType,
                                    onTypeSelected = { type ->
                                        onFilterStateChange(filterState.copy(selectedType = type))
                                    }
                                )
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                PublicAnnouncementPriorityFilter(
                                    selectedPriority = filterState.selectedPriority,
                                    onPrioritySelected = { priority ->
                                        onFilterStateChange(filterState.copy(selectedPriority = priority))
                                    }
                                )
                            }
                        }
                    }

                    // 清空筛选按钮
                    if (filterState.selectedType != null || filterState.selectedPriority != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = onClearFilters
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ClearAll,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("清空筛选")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 公告类型筛选组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicAnnouncementTypeFilter(
    selectedType: Int?,
    onTypeSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "公告类型",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedType?.let { AnnouncementType.fromValue(it)?.displayName }
                    ?: "全部类型",
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("全部类型") },
                    onClick = {
                        onTypeSelected(null)
                        expanded = false
                    }
                )

                AnnouncementType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName) },
                        onClick = {
                            onTypeSelected(type.value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * 公告优先级筛选组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicAnnouncementPriorityFilter(
    selectedPriority: Int?,
    onPrioritySelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "优先级",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedPriority?.let { AnnouncementPriority.fromValue(it)?.displayName }
                    ?: "全部优先级",
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("全部优先级") },
                    onClick = {
                        onPrioritySelected(null)
                        expanded = false
                    }
                )

                AnnouncementPriority.entries.forEach { priority ->
                    DropdownMenuItem(
                        text = { Text(priority.displayName) },
                        onClick = {
                            onPrioritySelected(priority.value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * 公告列表组件（网格布局）
 */
@Composable
fun PublicAnnouncementList(
    announcements: List<AnnouncementDto>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig,
    onAnnouncementClick: (AnnouncementDto) -> Unit = {}
) {
    // 根据屏幕尺寸确定网格列数
    val columns = when (layoutConfig.screenSize) {
        ResponsiveUtils.ScreenSize.COMPACT -> 1 // 手机：单列
        ResponsiveUtils.ScreenSize.MEDIUM -> 2  // 平板：双列
        ResponsiveUtils.ScreenSize.EXPANDED -> 3 // 桌面：三列
    }

    Column {
        // 刷新指示器
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Text(
                        text = "正在刷新...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 网格布局的公告列表
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            // 公告项
            items(
                items = announcements,
                key = { it.id ?: 0 }
            ) { announcement ->
                PublicAnnouncementCard(
                    announcement = announcement,
                    layoutConfig = layoutConfig,
                    onClick = { onAnnouncementClick(announcement) }
                )
            }
        }

        // 底部刷新按钮
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onRefresh,
                enabled = !isRefreshing
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("刷新更多")
            }
        }
    }
}

/**
 * 公告卡片组件（适配网格布局）
 */
@Composable
fun PublicAnnouncementCard(
    announcement: AnnouncementDto,
    layoutConfig: ResponsiveLayoutConfig,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp), // 固定高度，确保网格中卡片大小一致
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 上半部分：标题和内容
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 标题
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 内容预览
                Text(
                    text = announcement.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 下半部分：标签和时间
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 标签行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 优先级标签
                    AnnouncementPriority.fromValue(announcement.priority)?.let { priority ->
                        PublicAnnouncementPriorityChip(priority = priority)
                    }

                    // 类型标签
                    AnnouncementType.fromValue(announcement.type)?.let { type ->
                        PublicAnnouncementTypeChip(type = type)
                    }
                }

                // 发布时间
                announcement.publishTime?.let { publishTime ->
                    Text(
                        text = publishTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * 公告类型标签组件
 */
@Composable
fun PublicAnnouncementTypeChip(
    type: AnnouncementType
) {
    val backgroundColor = when (type) {
        AnnouncementType.SYSTEM -> MaterialTheme.colorScheme.primaryContainer
        AnnouncementType.ACTIVITY -> MaterialTheme.colorScheme.secondaryContainer
        AnnouncementType.NOTIFICATION -> MaterialTheme.colorScheme.tertiaryContainer
    }

    val contentColor = when (type) {
        AnnouncementType.SYSTEM -> MaterialTheme.colorScheme.onPrimaryContainer
        AnnouncementType.ACTIVITY -> MaterialTheme.colorScheme.onSecondaryContainer
        AnnouncementType.NOTIFICATION -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

/**
 * 公告优先级标签组件
 */
@Composable
fun PublicAnnouncementPriorityChip(
    priority: AnnouncementPriority
) {
    val backgroundColor = when (priority) {
        AnnouncementPriority.NORMAL -> MaterialTheme.colorScheme.surfaceVariant
        AnnouncementPriority.IMPORTANT -> MaterialTheme.colorScheme.secondaryContainer
        AnnouncementPriority.URGENT -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (priority) {
        AnnouncementPriority.NORMAL -> MaterialTheme.colorScheme.onSurfaceVariant
        AnnouncementPriority.IMPORTANT -> MaterialTheme.colorScheme.onSecondaryContainer
        AnnouncementPriority.URGENT -> MaterialTheme.colorScheme.onErrorContainer
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = priority.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 错误状态组件
 */
@Composable
fun PublicAnnouncementErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    onClearError: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "加载失败",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClearError,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("关闭")
                }

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        contentColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("重试")
                }
            }
        }
    }
}

/**
 * 空状态组件
 */
@Composable
fun PublicAnnouncementEmptyState(
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Announcement,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "暂无公告",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "当前没有已发布的公告",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onRefresh
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("刷新")
            }
        }
    }
}