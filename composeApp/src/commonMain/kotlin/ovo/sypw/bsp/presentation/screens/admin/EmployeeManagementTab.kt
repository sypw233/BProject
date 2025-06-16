package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import coil3.compose.AsyncImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.EmployeeDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.presentation.components.ManagementPageState
import ovo.sypw.bsp.presentation.components.ManagementPageActions
import ovo.sypw.bsp.presentation.components.ManagementPageTemplate
import ovo.sypw.bsp.presentation.viewmodel.EmployeeViewModel
import org.koin.compose.koinInject
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils
import ovo.sypw.bsp.presentation.components.EmployeeSearchAndFilter
import ovo.sypw.bsp.presentation.screens.admin.EmployeeDialog

/**
 * 员工管理页面
 * 支持响应式布局，在不同屏幕尺寸下显示不同的界面
 * 参考部门管理的实现，使用通用管理页面模板
 */
@Composable
fun EmployeeManagementTab(
    layoutConfig: ResponsiveLayoutConfig
) {
    val viewModel: EmployeeViewModel = koinInject()
    val employeeState by viewModel.employeeState.collectAsState()
    val searchQuery by viewModel.employeeSearchQuery.collectAsState()
    val filterState by viewModel.employeeFilterState.collectAsState()
    val departments by viewModel.departments.collectAsState()
    
    // 加载部门数据
    LaunchedEffect(Unit) {
        viewModel.loadDepartments()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 搜索和筛选组件
        EmployeeSearchAndFilter(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::updateEmployeeSearchQuery,
            filterState = filterState,
            onFilterChange = viewModel::updateEmployeeFilter,
            onToggleFilterExpanded = viewModel::toggleFilterExpanded,
            onClearAllFilters = viewModel::clearAllFilters,
            departments = departments,
            layoutConfig=layoutConfig
        )
        
        // 员工列表
        Box(modifier = Modifier.weight(1f)) {
            // 创建状态适配器
            val pageState = object : ManagementPageState<EmployeeDto> {
                override val isLoading: Boolean = employeeState.isLoading
                override val items: List<EmployeeDto> = employeeState.employees
                override val pageInfo: PageResultDto<EmployeeDto>? = employeeState.pageInfo
                override val errorMessage: String? = employeeState.errorMessage
            }
            
            // 创建操作适配器
            val pageActions = object : ManagementPageActions {
                override fun refresh() = viewModel.refreshEmployees()
                override fun loadData(current: Int, size: Int) {
                    // 使用当前的搜索和筛选条件加载数据
                    val currentQuery = searchQuery.takeIf { it.isNotBlank() }
                    viewModel.loadEmployees(current, size, currentQuery)
                }
                override fun showAddDialog() = viewModel.showAddEmployeeDialog()
            }
            
            // 使用通用管理页面模板
            ManagementPageTemplate(
                state = pageState,
                actions = pageActions,
                title = "员工列表",
                emptyMessage = "暂无员工数据",
                refreshText = "刷新数据",
                addText = "添加员工",
                layoutConfig = layoutConfig,
                itemContent = { employee ->
                    EmployeeCard(
                        employee = employee,
                        onEdit = { viewModel.showEditEmployeeDialog(employee) },
                        onDelete = { viewModel.deleteEmployee(employee.id) },
                        layoutConfig = layoutConfig
                    )
                },
                dialogContent = {
                    val dialogState by viewModel.employeeDialogState.collectAsState()
                    EmployeeDialog(
                        employeeViewModel = viewModel,
                        dialogState = dialogState,
                        onDismiss = { viewModel.hideEmployeeDialog() }
                    )
                }
            )
        }
    }
}

/**
 * 员工卡片组件
 * 显示员工基本信息和操作按钮
 */
@Composable
private fun EmployeeCard(
    employee: EmployeeDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // 固定卡片高度，确保一致性
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(layoutConfig.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像区域
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!employee.avatar.isNullOrEmpty()) {
                    AsyncImage(
                        model = employee.avatar,
                        contentDescription = "员工头像",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "默认头像",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 员工信息区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // 员工姓名
                Text(
                    text = employee.realName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 用户名
                Text(
                    text = "用户名: ${employee.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 性别和职位信息 - 根据屏幕尺寸调整布局
                if (layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT) {
                    // 小屏幕：垂直排列
                    Column {
                        Text(
                            text = "性别: ${if (employee.gender == 1) "男" else "女"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Text(
                            text = "职位: ${getJobName(employee.job)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                } else {
                    // 大屏幕：水平排列
                    Row {
                        Text(
                            text = "性别: ${if (employee.gender == 1) "男" else "女"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "职位: ${getJobName(employee.job)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 入职日期
                employee.entryDate?.let { entryDate ->
                    Text(
                        text = "入职日期: $entryDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // 操作按钮区域
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑员工",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除员工",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * 获取职位名称
 * @param job 职位代码
 * @return 职位名称
 */
private fun getJobName(job: Int): String {
    return when (job) {
        1 -> "班长"
        2 -> "讲师"
        3 -> "学工主管"
        4 -> "教研主管"
        5 -> "咨询师"
        else -> "未知职位"
    }
}