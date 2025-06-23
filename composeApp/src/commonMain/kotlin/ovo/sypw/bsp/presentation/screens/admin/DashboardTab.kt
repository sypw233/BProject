package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hoc081098.kmp.viewmodel.koin.compose.koinKmpViewModel
import ovo.sypw.bsp.presentation.components.CustomBarChart
import ovo.sypw.bsp.presentation.components.CustomPieChart
import ovo.sypw.bsp.presentation.components.StatisticCard
import ovo.sypw.bsp.presentation.viewmodel.admin.StatisticsViewModel
import ovo.sypw.bsp.utils.ResponsiveLayoutConfig

/**
 * 仪表板标签页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTab(
    modifier: Modifier = Modifier,
    layoutConfig: ResponsiveLayoutConfig,
    viewModel: StatisticsViewModel = koinKmpViewModel()
) {
    val state by viewModel.statisticsState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 内容区域
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "加载统计数据中...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            state.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "加载失败: ${state.errorMessage}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.clearError()
                                viewModel.loadStatistics()
                            }
                        ) {
                            Text("重试")
                        }
                    }
                }
            }

            state.data != null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    // 总体概览
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "总体概览",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    StatisticCard(
                                        title = "学生总数",
                                        count = state.data!!.totalStudents,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatisticCard(
                                        title = "员工总数",
                                        count = state.data!!.totalEmployees,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    StatisticCard(
                                        title = "班级总数",
                                        count = state.data!!.totalClasses,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatisticCard(
                                        title = "部门总数",
                                        count = state.data!!.totalDepartments,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    
                    // 饼图区域
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "数据分布",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Column(
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                CustomPieChart(
                                    title = "学生状态分布",
                                    data = state.data!!.studentStatusChart
                                )
                                CustomPieChart(
                                    title = "学生性别分布",
                                    data = state.data!!.studentGenderChart
                                )
                                CustomPieChart(
                                    title = "员工职位分布",
                                    data = state.data!!.employeeJobChart
                                )
                            }
                        }
                    }
                    
                    // 柱状图区域
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "趋势分析",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Column(
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                CustomBarChart(
                                    title = "各班级学生数量",
                                    data = state.data!!.classStudentChart
                                )
                                CustomBarChart(
                                    title = "各部门员工数量",
                                    data = state.data!!.departmentEmployeeChart
                                )
                                CustomBarChart(
                                    title = "员工入职年份统计",
                                    data = state.data!!.employeeEntryYearChart
                                )
                                CustomBarChart(
                                    title = "学生入学年份统计",
                                    data = state.data!!.studentJoinYearChart
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}