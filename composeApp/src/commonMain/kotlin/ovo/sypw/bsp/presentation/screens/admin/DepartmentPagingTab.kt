package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.paging.PagingLoadState
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.utils.PagingListState
import ovo.sypw.bsp.utils.PagingListUtils
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.rememberPagingListState

/**
 * 部门管理分页Tab
 * 使用自定义分页工具实现完整的分页查询功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentPagingTab(
    viewModel: AdminViewModel,
    layoutConfig: ResponsiveLayoutConfig,
    modifier: Modifier = Modifier
) {
    // 获取分页数据和状态
    val pagingData by viewModel.departmentPagingData.collectAsState()
    val searchQuery by viewModel.departmentSearchQuery.collectAsState()
    
    // 创建分页列表状态
    val pagingListState = rememberPagingListState<DepartmentDto>(
        pagingManager = viewModel.getDepartmentPagingManager()
    )
    val loadState by pagingListState.loadState.collectAsState()
    
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
    ) {
        // 页面标题
        Text(
            text = "部门管理 (分页版)",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // 搜索和操作区域
        DepartmentSearchAndActions(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::updateDepartmentSearchQuery,
            onClearSearch = viewModel::clearDepartmentSearch,
            onAddDepartment = viewModel::showAddDepartmentDialog,
            onRefresh = { 
                viewModel.refreshDepartments()
            },
            isLoading = loadState == PagingLoadState.Loading,
            layoutConfig = layoutConfig
        )
        
        // 分页信息显示
        PagingInfoCard(
            pagingData = pagingData,
            loadState = loadState
        )
        
        // 部门列表
        DepartmentPagingList(
            pagingListState = pagingListState,
            onEditDepartment = viewModel::showEditDepartmentDialog,
            onDeleteDepartment = { id: Long ->
                viewModel.deleteDepartment(id.toInt())
            },
            layoutConfig = layoutConfig,
            modifier = Modifier.weight(1f)
        )
        
        // 分页控制栏
        if (pagingData.totalPages > 1) {
            DepartmentPagingControls(
                pagingData = pagingData,
                loadState = loadState,
                onLoadMore = { },
                onRefresh = { },
                onGoToPage = { page ->
                    // 实现跳转到指定页面的逻辑
                }
            )
        }
    }
    
    // 部门Dialog
    DepartmentDialog(viewModel = viewModel)
    
    // 初始化加载
    LaunchedEffect(Unit) {
        if (pagingData.items.isEmpty() && loadState == PagingLoadState.Idle) {
            viewModel.loadDepartments()
        }
    }
}

/**
 * 搜索和操作区域
 */
@Composable
private fun DepartmentSearchAndActions(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onAddDepartment: () -> Unit,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    layoutConfig: ResponsiveLayoutConfig
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(layoutConfig.cardPadding),
            verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing)
        ) {
            // 搜索框
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    label = { Text("搜索部门") },
                    placeholder = { Text("输入部门名称") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "清除搜索"
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { /* 搜索已通过onValueChange触发 */ }
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            // 操作按钮
            if (layoutConfig.useFullWidthButtons) {
                // 紧凑型：垂直排列
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRefresh,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("刷新数据")
                    }
                    
                    OutlinedButton(
                        onClick = onAddDepartment,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("添加部门")
                    }
                }
            } else {
                // 中等型和扩展型：水平排列
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRefresh,
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("刷新数据")
                    }
                    
                    OutlinedButton(
                        onClick = onAddDepartment
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("添加部门")
                    }
                }
            }
        }
    }
}

/**
 * 分页信息卡片
 */
@Composable
private fun PagingInfoCard(
    pagingData: ovo.sypw.bsp.data.paging.PagingData<DepartmentDto>,
    loadState: PagingLoadState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 分页信息
            Text(
                text = PagingListUtils.formatPagingInfo(pagingData),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // 加载状态
            Text(
                text = PagingListUtils.getLoadStateText(loadState),
                style = MaterialTheme.typography.bodySmall,
                color = when (loadState) {
                    is PagingLoadState.Error -> MaterialTheme.colorScheme.error
                    is PagingLoadState.Loading -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * 部门分页列表
 */
@Composable
private fun DepartmentPagingList(
    pagingListState: PagingListState<DepartmentDto>,
    onEditDepartment: (DepartmentDto) -> Unit,
    onDeleteDepartment: (Long) -> Unit,
    layoutConfig: ResponsiveLayoutConfig,
    modifier: Modifier = Modifier
) {
    val pagingData by pagingListState.pagingData.collectAsState()
    val loadState by pagingListState.loadState.collectAsState()
    
    Box(modifier = modifier.fillMaxSize()) {
        if (PagingListUtils.shouldShowEmpty(pagingData, loadState)) {
            // 空状态
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "暂无部门数据",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "点击添加部门按钮创建第一个部门",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(layoutConfig.verticalSpacing),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // 部门列表项
                items(
                    items = pagingData.items,
                    key = { department: DepartmentDto -> department.id ?: 0 }
                ) { department ->
                    DepartmentPagingCard(
                        department = department,
                        onEdit = { onEditDepartment(department) },
                        onDelete = { onDeleteDepartment(department.id?.toLong() ?: 0L) },
                        layoutConfig = layoutConfig
                    )
                }
                
                // 加载更多指示器
                if (PagingListUtils.shouldShowLoadMore(pagingData, loadState)) {
                    item {
                        LoadMoreIndicator(
                            onLoadMore = { },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // 错误重试
                if (PagingListUtils.shouldShowRetry(loadState)) {
                    item {
                        ErrorRetryCard(
                            error = (loadState as PagingLoadState.Error).error,
                            onRetry = { },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * 部门分页卡片
 */
@Composable
private fun DepartmentPagingCard(
    department: DepartmentDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(layoutConfig.cardPadding)
        ) {
            // 部门标题和操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = department.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: ${department.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑部门",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除部门",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * 分页控制栏
 */
@Composable
private fun DepartmentPagingControls(
    pagingData: ovo.sypw.bsp.data.paging.PagingData<DepartmentDto>,
    loadState: PagingLoadState,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onGoToPage: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 页面信息
            Text(
                text = "第 ${pagingData.currentPage} / ${pagingData.totalPages} 页",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // 控制按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 刷新按钮
                IconButton(
                    onClick = onRefresh,
                    enabled = loadState != PagingLoadState.Loading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "刷新"
                    )
                }
                
                // 加载更多按钮
                if (pagingData.hasNextPage) {
                    Button(
                        onClick = onLoadMore,
                        enabled = loadState != PagingLoadState.Loading
                    ) {
                        if (loadState == PagingLoadState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("加载更多")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 加载更多指示器
 */
@Composable
private fun LoadMoreIndicator(
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "正在加载更多...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 错误重试卡片
 */
@Composable
private fun ErrorRetryCard(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = "加载失败: $error",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("重试")
            }
        }
    }
}