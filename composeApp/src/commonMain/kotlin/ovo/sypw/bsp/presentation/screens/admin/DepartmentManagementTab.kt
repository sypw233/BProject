package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import org.koin.compose.koinInject
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.presentation.components.dialog.DepartmentDialog
import ovo.sypw.bsp.presentation.components.template.ManagementPageActions
import ovo.sypw.bsp.presentation.components.template.ManagementPageState
import ovo.sypw.bsp.presentation.components.template.ManagementPageTemplate
import ovo.sypw.bsp.presentation.components.template.SearchBar
import ovo.sypw.bsp.presentation.viewmodel.admin.DepartmentViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig

/**
 * 部门管理页面
 * 支持响应式布局，在不同屏幕尺寸下显示不同的界面
 */
@Composable
fun DepartmentManagementTab(
    layoutConfig: ResponsiveLayoutConfig
) {
    val viewModel: DepartmentViewModel = koinInject()
    val departmentState by viewModel.departmentState.collectAsState()
    val searchQuery by viewModel.departmentSearchQuery.collectAsState()


    // 创建状态适配器
    val pageState = object : ManagementPageState<DepartmentDto> {
        override val isLoading: Boolean = departmentState.isLoading
        override val items: List<DepartmentDto> = departmentState.departments
        override val pageInfo: PageResultDto<DepartmentDto>? = departmentState.pageInfo
        override val errorMessage: String? = departmentState.errorMessage
    }

    // 创建操作适配器
    val pageActions = object : ManagementPageActions {
        override fun refresh() = viewModel.refreshDepartments()
        override fun loadData(current: Int, size: Int) = viewModel.loadDepartments(current, size)
        override fun showAddDialog() = viewModel.showAddDepartmentDialog()
    }

    // 使用通用管理页面模板
    ManagementPageTemplate(
        state = pageState,
        actions = pageActions,
        title = "部门管理",
        emptyMessage = "暂无部门数据",
        refreshText = "刷新数据",
        addText = "添加部门",
        layoutConfig = layoutConfig,
        itemContent = { department ->
            DepartmentCard(
                department = department,
                onEdit = { viewModel.showEditDepartmentDialog(department) },
                onDelete = { viewModel.deleteDepartment(department.id) },
                layoutConfig = layoutConfig
            )
        },
        dialogContent = {
            DepartmentDialog(viewModel = viewModel)
        },
        searchAndFilterContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateDepartmentSearchQuery,
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

            }
        }
    )

}

/**
 * 部门卡片组件
 */
@Composable
private fun DepartmentCard(
    department: DepartmentDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    layoutConfig: ResponsiveLayoutConfig
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
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
                Text(
                    text = department.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

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
