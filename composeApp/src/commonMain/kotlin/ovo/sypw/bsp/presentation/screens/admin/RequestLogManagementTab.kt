package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.RequestLogDto
import ovo.sypw.bsp.data.dto.RequestMethod
import ovo.sypw.bsp.presentation.components.dialog.RequestLogDetailDialog
import ovo.sypw.bsp.presentation.components.search.RequestLogSearchAndFilter
import ovo.sypw.bsp.presentation.components.template.ManagementPageActions
import ovo.sypw.bsp.presentation.components.template.ManagementPageState
import ovo.sypw.bsp.presentation.components.template.ManagementPageTemplate
import ovo.sypw.bsp.presentation.viewmodel.admin.RequestLogViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 请求日志管理页面
 * 支持响应式布局，在不同屏幕尺寸下显示不同的界面
 * 参考学生管理的实现，使用通用管理页面模板
 */
@Composable
fun RequestLogManagementTab(
    layoutConfig: ResponsiveLayoutConfig
) {
    val viewModel: RequestLogViewModel = koinInject()
    val requestLogState by viewModel.requestLogState.collectAsState()
    val searchQuery by viewModel.requestLogSearchQuery.collectAsState()
    val filterState by viewModel.requestLogFilterState.collectAsState()
    val detailState by viewModel.requestLogDetailState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 请求日志列表
        Box(modifier = Modifier.weight(1f)) {
            // 创建状态适配器
            val pageState = object : ManagementPageState<RequestLogDto> {
                override val isLoading: Boolean = requestLogState.isLoading
                override val items: List<RequestLogDto> = requestLogState.requestLogs
                override val pageInfo: PageResultDto<RequestLogDto>? = requestLogState.pageInfo
                override val errorMessage: String? = requestLogState.errorMessage
            }

            // 创建操作适配器
            val pageActions = object : ManagementPageActions {
                override fun refresh() = viewModel.refreshRequestLogs()
                override fun loadData(current: Int, size: Int) {
                    // 使用当前的搜索和筛选条件加载数据
                    val currentQuery = searchQuery.takeIf { it.isNotBlank() }
                    viewModel.loadRequestLogs(current, size, currentQuery)
                }

                override fun showAddDialog() {
                    // 请求日志不支持添加操作
                }
            }

            // 使用通用管理页面模板
            ManagementPageTemplate(
                state = pageState,
                actions = pageActions,
                title = "请求日志列表",
                emptyMessage = "暂无请求日志数据",
                refreshText = "刷新数据",
                addText = null, // 不显示添加按钮
                layoutConfig = layoutConfig,
                itemContent = { requestLog ->
                    RequestLogCard(
                        requestLog = requestLog,
                        onViewDetail = { viewModel.showRequestLogDetail(requestLog) },
                        layoutConfig = layoutConfig
                    )
                },
                dialogContent = {
                    // 请求日志详情Dialog
                    if (detailState.isVisible) {
                        RequestLogDetailDialog(
                            detailState = detailState,
                            onDismiss = { viewModel.hideRequestLogDetail() }
                        )
                    }
                },
                searchAndFilterContent = {
                    // 搜索和筛选组件
                    RequestLogSearchAndFilter(
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::updateRequestLogSearchQuery,
                        filterState = filterState,
                        onToggleFilterExpanded = viewModel::toggleFilterExpanded,
                        onFilterChange = viewModel::updateRequestLogFilter,
                        onClearAllFilters = viewModel::resetFilter,
                        layoutConfig = layoutConfig
                    )
                }
            )
        }
    }
}

/**
 * 请求日志卡片组件
 * 显示请求日志基本信息和操作按钮
 */
@Composable
private fun RequestLogCard(
    requestLog: RequestLogDto,
    onViewDetail: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp) // 固定卡片高度，确保一致性
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(layoutConfig.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 请求日志信息区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // 请求方法和URL
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = requestLog.requestMethod,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = getRequestMethodColor(requestLog.requestMethod),
                        modifier = Modifier.width(60.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = requestLog.requestUrl,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 用户信息和响应状态 - 根据屏幕尺寸调整布局
                if (layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT) {
                    // 小屏幕：垂直排列
                    Column {
                        Text(
                            text = "用户: ${requestLog.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        Row {
                            Text(
                                text = "状态: ${requestLog.responseStatus}",
                                style = MaterialTheme.typography.bodySmall,
                                color = getResponseStatusColor(requestLog.responseStatus),
                                maxLines = 1
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = "耗时: ${requestLog.responseTime}ms",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                } else {
                    // 大屏幕：水平排列
                    Row {
                        Text(
                            text = "用户: ${requestLog.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "状态: ${requestLog.responseStatus}",
                            style = MaterialTheme.typography.bodySmall,
                            color = getResponseStatusColor(requestLog.responseStatus),
                            maxLines = 1
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "耗时: ${requestLog.responseTime}ms",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // IP地址和创建时间
                Row {
                    Text(
                        text = "IP: ${requestLog.ipAddress}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = requestLog.createdAt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // 操作按钮区域
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onViewDetail) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "查看详情",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * 获取请求方法颜色
 * @param method 请求方法
 * @return 方法颜色
 */
@Composable
private fun getRequestMethodColor(method: String): Color {
    return when (method.uppercase()) {
        "GET" -> MaterialTheme.colorScheme.primary
        "POST" -> MaterialTheme.colorScheme.secondary
        "PUT" -> MaterialTheme.colorScheme.tertiary
        "DELETE" -> MaterialTheme.colorScheme.error
        "PATCH" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

/**
 * 获取响应状态码颜色
 * @param status 响应状态码
 * @return 状态颜色
 */
@Composable
private fun getResponseStatusColor(status: Int): Color {
    return when (status) {
        in 200..299 -> MaterialTheme.colorScheme.primary // 成功
        in 300..399 -> MaterialTheme.colorScheme.secondary // 重定向
        in 400..499 -> MaterialTheme.colorScheme.tertiary // 客户端错误
        in 500..599 -> MaterialTheme.colorScheme.error // 服务器错误
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}