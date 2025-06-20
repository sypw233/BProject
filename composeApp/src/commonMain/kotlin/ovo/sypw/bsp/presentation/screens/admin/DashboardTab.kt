package ovo.sypw.bsp.presentation.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import ovo.sypw.bsp.presentation.viewmodel.admin.StatisticsViewModel

/**
 * 仪表板标签页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTab(
    modifier: Modifier = Modifier,
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
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 饼图区域
                    item {
                        Text(
                            text = "分布统计",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 300.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.height(800.dp)
                        ) {
                            items(
                                listOf(
                                    "学生状态分布" to state.data!!.studentStatusChart,
                                    "学生性别分布" to state.data!!.studentGenderChart,
                                    "员工职位分布" to state.data!!.employeeJobChart
                                )
                            ) { (title, data) ->
                                CustomPieChart(
                                    title = title,
                                    data = data
                                )
                            }
                        }
                    }

                    // 柱状图区域
                    item {
                        Text(
                            text = "数量统计",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                        )
                    }

                    // 使用网格布局显示柱状图，提供更好的响应式体验
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 400.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.height(1000.dp)
                        ) {
                            items(
                                listOf(
                                    "各班级学生数量" to state.data!!.classStudentChart,
                                    "各部门员工数量" to state.data!!.departmentEmployeeChart,
                                    "员工入职年份统计" to state.data!!.employeeEntryYearChart,
                                    "学生入学年份统计" to state.data!!.studentJoinYearChart
                                )
                            ) { (title, data) ->
                                CustomBarChart(
                                    title = title,
                                    data = data,
                                    modifier = Modifier.fillMaxWidth()
                                )

                            }

                        }
                    }
                }
            }
        }
    }
}