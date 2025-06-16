package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.AnnouncementDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.presentation.components.*
import ovo.sypw.bsp.presentation.components.ManagementPageTemplate
import ovo.sypw.bsp.presentation.components.ManagementPageState
import ovo.sypw.bsp.presentation.components.ManagementPageActions
import ovo.sypw.bsp.presentation.viewmodel.AnnouncementViewModel
import ovo.sypw.bsp.data.dto.AnnouncementStatus
import org.koin.compose.koinInject
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 公告管理页面
 * 支持响应式布局，在不同屏幕尺寸下显示不同的界面
 * 参考员工管理的实现，使用通用管理页面模板
 */
@Composable
fun AnnouncementManagementTab(
    layoutConfig: ResponsiveLayoutConfig
) {
    val viewModel: AnnouncementViewModel = koinInject()
    val announcementState by viewModel.announcementState.collectAsState()
    val searchQuery by viewModel.announcementSearchQuery.collectAsState()
    val filterState by viewModel.announcementFilterState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // 公告列表
        Box(modifier = Modifier.weight(1f)) {
            // 创建状态适配器
            val pageState = object : ManagementPageState<AnnouncementDto> {
                override val isLoading: Boolean = announcementState.isLoading
                override val items: List<AnnouncementDto> = announcementState.announcements
                override val pageInfo: PageResultDto<AnnouncementDto>? = announcementState.pageInfo
                override val errorMessage: String? = announcementState.errorMessage
            }
            
            // 创建操作适配器
            val pageActions = object : ManagementPageActions {
                override fun refresh() = viewModel.refreshAnnouncements()
                override fun loadData(current: Int, size: Int) {
                    // 使用当前的搜索和筛选条件加载数据
                    val currentQuery = searchQuery.takeIf { it.isNotBlank() }
                    viewModel.loadAnnouncements(current, size, currentQuery)
                }
                override fun showAddDialog() = viewModel.showAddAnnouncementDialog()
            }
            
            // 使用管理页面模板
            ManagementPageTemplate(
                state = pageState,
                actions = pageActions,
                title = "公告列表",
                emptyMessage = "暂无公告数据",
                refreshText = "刷新数据",
                addText = "添加公告",
                layoutConfig = layoutConfig,
                itemContent = { announcement ->
                    AnnouncementCard(
                        announcement = announcement,
                        onEdit = { viewModel.showEditAnnouncementDialog(announcement) },
                        onDelete = { viewModel.deleteAnnouncement(announcement.id) },
                        onPublish = { viewModel.publishAnnouncement(announcement.id) },
                        onUnpublish = { viewModel.unpublishAnnouncement(announcement.id) },
                        layoutConfig = layoutConfig
                    )
                },
                dialogContent = {
                    val dialogState by viewModel.announcementDialogState.collectAsState()
                    AnnouncementDialog(
                        announcementViewModel = viewModel,
                        dialogState = dialogState,
                        onDismiss = { viewModel.hideAnnouncementDialog() }
                    )
                },
                searchAndFilterContent = {
                    // 搜索和筛选组件
                    AnnouncementSearchAndFilter(
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::updateAnnouncementSearchQuery,
                        filterState = filterState,
                        onFilterChange = viewModel::updateAnnouncementFilter,
                        onToggleFilterExpanded = viewModel::toggleFilterExpanded,
                        onClearAllFilters = viewModel::clearAllFilters,
                        layoutConfig = layoutConfig
                    )
                }
            )
        }
    }
}

/**
 * 公告卡片组件
 * 显示公告基本信息和操作按钮
 */
@Composable
private fun AnnouncementCard(
    announcement: AnnouncementDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPublish: () -> Unit,
    onUnpublish: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp), // 固定卡片高度，确保一致性
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(layoutConfig.cardPadding)
        ) {
            // 标题和状态行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 公告标题
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 状态标签
                AnnouncementStatusChip(
                    status = announcement.status,
                    priority = announcement.priority
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 公告内容预览
            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 时间信息和操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // 时间信息
                Column {
                    announcement.createTime?.let { createTime ->
                        Text(
                            text = "创建: $createTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    announcement.publishTime?.let { publishTime ->
                        Text(
                            text = "发布: $publishTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // 操作按钮
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 编辑按钮
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑公告",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    // 发布/下线按钮
                    if (announcement.status == AnnouncementStatus.PUBLISHED.value) {
                        // 已发布状态，显示下线按钮
                        IconButton(
                            onClick = onUnpublish,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "下线公告",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        // 草稿或下线状态，显示发布按钮
                        IconButton(
                            onClick = onPublish,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "发布公告",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    // 删除按钮
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除公告",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 公告状态标签组件
 */
@Composable
private fun AnnouncementStatusChip(
    status: Int,
    priority: Int
) {
    val (statusText, statusColor) = when (status) {
        0 -> "草稿" to MaterialTheme.colorScheme.outline
        1 -> "已发布" to MaterialTheme.colorScheme.primary
        2 -> "已下线" to MaterialTheme.colorScheme.secondary
        else -> "未知" to MaterialTheme.colorScheme.outline
    }
    
    val priorityText = when (priority) {
        1 -> "普通"
        2 -> "重要"
        3 -> "紧急"
        else -> "普通"
    }
    
    Column(
        horizontalAlignment = Alignment.End
    ) {
        // 状态标签
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp)),
            color = statusColor.copy(alpha = 0.1f)
        ) {
            Text(
                text = statusText,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = statusColor
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 优先级标签
        if (priority > 1) {
            val priorityColor = when (priority) {
                2 -> MaterialTheme.colorScheme.tertiary
                3 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.outline
            }
            
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp)),
                color = priorityColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = priorityText,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = priorityColor
                )
            }
        }
    }
}