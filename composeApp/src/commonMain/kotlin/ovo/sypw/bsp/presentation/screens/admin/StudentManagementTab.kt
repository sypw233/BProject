package ovo.sypw.bsp.presentation.screens.admin

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import coil3.compose.AsyncImage
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ovo.sypw.bsp.data.dto.StudentDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.presentation.components.ManagementPageState
import ovo.sypw.bsp.presentation.components.ManagementPageActions
import ovo.sypw.bsp.presentation.components.ManagementPageTemplate
import ovo.sypw.bsp.presentation.viewmodel.StudentViewModel
import org.koin.compose.koinInject
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig
import ovo.sypw.bsp.utils.ResponsiveUtils
import ovo.sypw.bsp.presentation.components.StudentSearchAndFilter

/**
 * 学生管理页面
 * 支持响应式布局，在不同屏幕尺寸下显示不同的界面
 * 参考员工管理的实现，使用通用管理页面模板
 */
@Composable
fun StudentManagementTab(
    layoutConfig: ResponsiveLayoutConfig
) {
    val viewModel: StudentViewModel = koinInject()
    val studentState by viewModel.studentState.collectAsState()
    val searchQuery by viewModel.studentSearchQuery.collectAsState()
    val filterState by viewModel.studentFilterState.collectAsState()
    val classes by viewModel.classes.collectAsState()
    
    // 加载班级数据
    LaunchedEffect(Unit) {
        viewModel.loadClasses()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // 学生列表
        Box(modifier = Modifier.weight(1f)) {
            // 创建状态适配器
            val pageState = object : ManagementPageState<StudentDto> {
                override val isLoading: Boolean = studentState.isLoading
                override val items: List<StudentDto> = studentState.students
                override val pageInfo: PageResultDto<StudentDto>? = studentState.pageInfo
                override val errorMessage: String? = studentState.errorMessage
            }
            
            // 创建操作适配器
            val pageActions = object : ManagementPageActions {
                override fun refresh() = viewModel.refreshStudents()
                override fun loadData(current: Int, size: Int) {
                    // 使用当前的搜索和筛选条件加载数据
                    val currentQuery = searchQuery.takeIf { it.isNotBlank() }
                    viewModel.loadStudents(current, size, currentQuery)
                }
                override fun showAddDialog() = viewModel.showAddStudentDialog()
            }
            
            // 使用通用管理页面模板
            ManagementPageTemplate(
                state = pageState,
                actions = pageActions,
                title = "学生列表",
                emptyMessage = "暂无学生数据",
                refreshText = "刷新数据",
                addText = "添加学生",
                layoutConfig = layoutConfig,
                itemContent = { student ->
                    StudentCard(
                        student = student,
                        onEdit = { viewModel.showEditStudentDialog(student) },
                        onDelete = { viewModel.deleteStudent(student.id) },
                        layoutConfig = layoutConfig
                    )
                },
                dialogContent = {
                    val dialogState by viewModel.studentDialogState.collectAsState()
                    StudentDialog(
                        studentViewModel = viewModel,
                        dialogState = dialogState,
                        onDismiss = { viewModel.hideStudentDialog() }
                    )
                },
                searchAndFilterContent = {
                    // 搜索和筛选组件
                    StudentSearchAndFilter(
                        searchQuery = searchQuery,
                        onSearchQueryChange = viewModel::updateStudentSearchQuery,
                        filterState = filterState,
                        onFilterChange = viewModel::updateStudentFilter,
                        onToggleFilterExpanded = viewModel::toggleFilterExpanded,
                        onClearAllFilters = viewModel::clearAllFilters,
                        classes = classes,
                        layoutConfig = layoutConfig
                    )
                }
            )
        }
    }
}

/**
 * 学生卡片组件
 * 显示学生基本信息和操作按钮
 */
@Composable
private fun StudentCard(
    student: StudentDto,
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
            // 学生信息区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // 学生姓名
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
                
                // 性别和年龄信息 - 根据屏幕尺寸调整布局
                if (layoutConfig.screenSize == ResponsiveUtils.ScreenSize.COMPACT) {
                    // 小屏幕：垂直排列
                    Column {
                        Text(
                            text = "性别: ${getGenderName(student.gender)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )

                    }
                } else {
                    // 大屏幕：水平排列
                    Row {
                        Text(
                            text = "性别: ${getGenderName(student.gender)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 班级和状态信息
                Row {
                    student.classId.let { classId ->
                        Text(
                            text = "班级: $classId",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = getStatusName(student.status),
                        style = MaterialTheme.typography.bodySmall,
                        color = getStatusColor(student.status),
                        maxLines = 1
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
                        contentDescription = "编辑学生",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除学生",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * 获取性别名称
 * @param gender 性别代码
 * @return 性别名称
 */
private fun getGenderName(gender: Int): String {
    return when (gender) {
        1 -> "男"
        2 -> "女"
        else -> "未知"
    }
}

/**
 * 获取状态名称
 * @param status 状态代码
 * @return 状态名称
 */
private fun getStatusName(status: Int): String {
    return when (status) {
        1 -> "在读"
        2 -> "已毕业"
        3 -> "休学"
        4 -> "退学"
        else -> "未知"
    }
}

/**
 * 获取状态颜色
 * @param status 状态代码
 * @return 状态颜色
 */
@Composable
private fun getStatusColor(status: Int): Color {
    return when (status) {
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.secondary
        3 -> MaterialTheme.colorScheme.tertiary
        4 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}