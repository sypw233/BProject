package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.ClassDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.presentation.components.ManagementPageState
import ovo.sypw.bsp.presentation.components.ManagementPageActions
import ovo.sypw.bsp.presentation.components.ManagementPageTemplate
import ovo.sypw.bsp.presentation.viewmodel.admin.ClassViewModel
import org.koin.compose.koinInject
import ovo.sypw.bsp.presentation.components.SearchBar
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig

/**
 * 班级管理页面
 * 支持响应式布局，在不同屏幕尺寸下显示不同的界面
 */
@Composable
fun ClassManagementTab(
    layoutConfig: ResponsiveLayoutConfig
) {
    val viewModel: ClassViewModel = koinInject()
    val classState by viewModel.classState.collectAsState()
    val searchQuery by viewModel.classSearchQuery.collectAsState()


    // 创建状态适配器
    val pageState = object : ManagementPageState<ClassDto> {
        override val isLoading: Boolean = classState.isLoading
        override val items: List<ClassDto> = classState.classes
        override val pageInfo: PageResultDto<ClassDto>? = classState.pageInfo
        override val errorMessage: String? = classState.errorMessage
    }

    // 创建操作适配器
    val pageActions = object : ManagementPageActions {
        override fun refresh() = viewModel.refreshClasses()
        override fun loadData(current: Int, size: Int) = viewModel.loadClasses(current, size)
        override fun showAddDialog() = viewModel.showAddClassDialog()
    }

    // 使用通用管理页面模板
    ManagementPageTemplate(
        state = pageState,
        actions = pageActions,
        title = "班级管理",
        emptyMessage = "暂无班级数据",
        refreshText = "刷新数据",
        addText = "添加班级",
        layoutConfig = layoutConfig,
        itemContent = { classDto ->
            ClassCard(
                classDto = classDto,
                onEdit = { viewModel.showEditClassDialog(classDto) },
                onDelete = { viewModel.deleteClass(classDto.id) },
                layoutConfig = layoutConfig
            )
        },
        dialogContent = {
            ClassDialog(viewModel = viewModel)
        },
        searchAndFilterContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                SearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::updateClassSearchQuery,
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
 * 班级卡片组件
 */
@Composable
private fun ClassCard(
    classDto: ClassDto,
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
            // 班级标题和操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = classDto.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "年级: ${classDto.grade}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑班级",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除班级",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}