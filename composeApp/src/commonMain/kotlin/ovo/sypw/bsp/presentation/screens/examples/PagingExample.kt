package ovo.sypw.bsp.presentation.screens.examples

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.data.paging.PagingLoadState
import ovo.sypw.bsp.domain.usecase.DepartmentUseCase
import ovo.sypw.bsp.utils.*

/**
 * 分页使用示例
 * 展示如何使用自定义分页工具类
 */
@Composable
fun PagingExampleScreen(
    departmentUseCase: DepartmentUseCase,
    modifier: Modifier = Modifier
) {
    // 创建分页管理器
    val pagingManager = remember {
        PagingUtils.createPagingManager<DepartmentDto>(
            loadData = { page, pageSize ->
                departmentUseCase.getDepartmentPage(
                    current = page,
                    size = pageSize,
                    name = null
                )
            },
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 20,
                prefetchDistance = 5
            )
        )
    }
    
    // 创建分页列表状态
    val pagingListState = rememberPagingListState(
        pagingManager = pagingManager,
        autoInitialize = true
    )
    
    // 收集分页数据和加载状态
    val pagingData by pagingListState.pagingData.collectAsState()
    val loadState by pagingListState.loadState.collectAsState()
    
    // 协程作用域
    val scope = rememberCoroutineScope()
    
    // LazyColumn状态
    val listState = rememberLazyListState()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 顶部操作栏
        PagingToolbar(
            pagingData = pagingData,
            loadState = loadState,
            onRefresh = {
                scope.launch {
                    pagingListState.refresh()
                }
            },
            onRetry = {
                scope.launch {
                    pagingListState.retry()
                }
            }
        )
        
        // 分页列表
        PagingLazyColumn(
            pagingListState = pagingListState,
            listState = listState,
            modifier = Modifier.weight(1f)
        ) { item, index ->
            DepartmentItem(
                department = item,
                index = index
            )
        }
        
        // 底部分页信息
        PagingBottomBar(
            pagingData = pagingData,
            loadState = loadState
        )
    }
}

/**
 * 分页工具栏
 */
@Composable
private fun PagingToolbar(
    pagingData: PagingData<DepartmentDto>,
    loadState: PagingLoadState,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
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
                style = MaterialTheme.typography.bodyMedium
            )
            
            // 操作按钮
            Row {
                // 刷新按钮
                Button(
                    onClick = onRefresh,
                    enabled = loadState != PagingLoadState.Loading
                ) {
                    Text("刷新")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 重试按钮（仅在错误时显示）
                if (PagingListUtils.shouldShowRetry(loadState)) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("重试")
                    }
                }
            }
        }
    }
}

/**
 * 分页懒加载列表
 */
@Composable
private fun <T> PagingLazyColumn(
    pagingListState: PagingListState<T>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    itemContent: @Composable (item: T, index: Int) -> Unit
) {
    val pagingData by pagingListState.pagingData.collectAsState()
    val loadState by pagingListState.loadState.collectAsState()
    val scope = rememberCoroutineScope()
    
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 数据项
        items(
            count = pagingData.items.size,
            key = { index -> index }
        ) { index ->
            val item = pagingListState.getItem(index)
            if (item != null) {
                itemContent(item, index)
                
                // 检查预加载
                LaunchedEffect(index) {
                    pagingListState.checkPreload(index)
                }
            }
        }
        
        // 加载更多指示器
        if (PagingListUtils.shouldShowLoadMore(pagingData, loadState)) {
            item {
                LoadMoreIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // 错误重试
        if (PagingListUtils.shouldShowRetry(loadState)) {
            item {
                ErrorRetryItem(
                    error = (loadState as PagingLoadState.Error).error,
                    onRetry = {
                        scope.launch {
                            pagingListState.retry()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // 空状态
        if (PagingListUtils.shouldShowEmpty(pagingData, loadState)) {
            item {
                EmptyStateItem(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 部门项组件
 */
@Composable
private fun DepartmentItem(
    department: DepartmentDto,
    index: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "#${index + 1} ${department.name}",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "ID: ${department.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 加载更多指示器
 */
@Composable
private fun LoadMoreIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "加载更多...",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * 错误重试项
 */
@Composable
private fun ErrorRetryItem(
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "加载失败",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("重试")
            }
        }
    }
}

/**
 * 空状态项
 */
@Composable
private fun EmptyStateItem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "暂无数据",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "请稍后再试或刷新页面",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 分页底部栏
 */
@Composable
private fun PagingBottomBar(
    pagingData: PagingData<DepartmentDto>,
    loadState: PagingLoadState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 页码信息
            Text(
                text = "第 ${pagingData.currentPage} / ${pagingData.totalPages} 页",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // 状态信息
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