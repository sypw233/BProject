package ovo.sypw.bsp.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey

import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils

/**
 * 部门管理Tab - 分页版本
 * 使用Paging库实现分页加载
 * 
 * @param viewModel AdminViewModel实例
 * @param layoutConfig 响应式布局配置
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentManagementPagingTab(
    viewModel: AdminViewModel,
    layoutConfig: ResponsiveLayoutConfig
) {
    val searchQuery by viewModel.departmentSearchQuery.collectAsState()
    val departmentPagingItems = viewModel.departmentPagingData.collectAsLazyPagingItems()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ResponsiveUtils.Padding.getContentPadding(layoutConfig.screenSize))
    ) {
        // 标题和添加按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "部门管理 (分页版)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = { viewModel.showAddDepartmentDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("添加部门")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 搜索框
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateDepartmentSearchQuery(it) },
            label = { Text("搜索部门") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "搜索")
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearDepartmentSearch() }
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "清空")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                }
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 分页列表
        DepartmentPagingList(
            pagingItems = departmentPagingItems,
            layoutConfig = layoutConfig,
            onEditDepartment = { department ->
                viewModel.showEditDepartmentDialog(department)
            },
            onDeleteDepartment = { departmentId ->
                viewModel.deleteDepartment(departmentId)
            }
        )
    }
    
    // 部门对话框
    DepartmentDialog(viewModel = viewModel)
}

/**
 * 部门分页列表组件
 */
@Composable
private fun DepartmentPagingList(
    pagingItems: LazyPagingItems<DepartmentDto>,
    layoutConfig: ResponsiveLayoutConfig,
    onEditDepartment: (DepartmentDto) -> Unit,
    onDeleteDepartment: (Int?) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { it.id ?: 0 }
        ) { index ->
            val department = pagingItems[index]
            if (department != null) {
                DepartmentPagingCard(
                    department = department,
                    layoutConfig = layoutConfig,
                    onEdit = { onEditDepartment(department) },
                    onDelete = { onDeleteDepartment(department.id) }
                )
            }
        }
        
        // 处理加载状态
        when (val loadState = pagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is LoadState.Error -> {
                item {
                    ErrorItem(
                        message = loadState.error.message ?: "加载失败",
                        onRetry = { pagingItems.retry() }
                    )
                }
            }
            is LoadState.NotLoading -> {
                // 正常状态，不需要额外处理
            }

        }
    }
    
    // 处理初始加载状态
    when (val refreshState = pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is LoadState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = refreshState.error.message ?: "加载失败",
                    onRetry = { pagingItems.refresh() }
                )
            }
        }
        is LoadState.NotLoading -> {
            // 正常状态，显示列表
        }
    }
}

/**
 * 部门卡片组件 - 分页版本
 */
@Composable
private fun DepartmentPagingCard(
    department: DepartmentDto,
    layoutConfig: ResponsiveLayoutConfig,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = if (layoutConfig.screenSize == ResponsiveUtils.ScreenSize.EXPANDED) 600.dp else 400.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ResponsiveUtils.Padding.getCardPadding(layoutConfig.screenSize)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = department.name ?: "未知部门",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
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
                        Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * 错误项组件
 */
@Composable
private fun ErrorItem(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRetry) {
                Text("重试")
            }
        }
    }
}