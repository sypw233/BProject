package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.EmployeeDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.presentation.components.ManagementPageState
import ovo.sypw.bsp.presentation.components.ManagementPageActions
import ovo.sypw.bsp.presentation.components.ManagementPageTemplate
import ovo.sypw.bsp.presentation.viewmodel.EmployeeViewModel
import org.koin.compose.koinInject
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig

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
        override fun loadData(current: Int, size: Int) = viewModel.loadEmployees(current, size)
        override fun showAddDialog() = viewModel.showAddEmployeeDialog()
    }
    
    // 使用通用管理页面模板
    ManagementPageTemplate(
        state = pageState,
        actions = pageActions,
        title = "员工管理",
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(layoutConfig.cardPadding)
        ) {
            // 员工标题和操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // 员工姓名
                    Text(
                        text = employee.realName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // 用户名
                    Text(
                        text = "用户名: ${employee.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // 性别和职位信息
                    Row {
                        Text(
                            text = "性别: ${if (employee.gender == 1) "男" else "女"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "职位: ${getJobName(employee.job)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // 入职日期
                    employee.entryDate?.let { entryDate ->
                        Text(
                            text = "入职日期: $entryDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
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