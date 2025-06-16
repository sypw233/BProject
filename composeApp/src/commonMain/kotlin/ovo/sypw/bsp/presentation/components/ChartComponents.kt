package ovo.sypw.bsp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData

import ovo.sypw.bsp.data.model.BarChartData
import ovo.sypw.bsp.data.model.PieChartItem

/**
 * 饼图组件
 * @param title 图表标题
 * @param data 饼图数据
 * @param modifier 修饰符
 */
@Composable
fun CustomPieChart(
    title: String,
    data: List<PieChartItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (data.isNotEmpty()) {
                val pieChartData = data.mapIndexed { index, item ->
                    PieChartData(
                        partName = item.name,
                        data = item.value.toDouble(),
                        color = getChartColor(index)
                    )
                }

                PieChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    pieChartData = pieChartData,
                    ratioLineColor = Color.LightGray,
                    textRatioStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp
                    )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 柱状图组件
 * @param title 图表标题
 * @param data 柱状图数据
 * @param modifier 修饰符
 */
@Composable
fun CustomBarChart(
    title: String,
    data: BarChartData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 标题样式优化
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp
                ),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (data.categories.isNotEmpty() && data.values.isNotEmpty()) {
                // 计算合适的图表高度
                val chartHeight = when {
                    data.categories.size <= 3 -> 300.dp
                    data.categories.size <= 6 -> 340.dp
                    data.categories.size <= 10 -> 380.dp
                    else -> 420.dp
                }

                // 计算合适的柱子宽度 - 优化对齐
                val barWidth = when {
                    data.categories.size <= 3 -> 40.dp
                    data.categories.size <= 6 -> 32.dp
                    data.categories.size <= 10 -> 24.dp
                    else -> 20.dp
                }

                // 使用棕色系配色方案
                val barColors = listOf(
                    Color(0xFF8B4513), // 深棕色
                    Color(0xFFA0522D), // 中棕色
                    Color(0xFFCD853F), // 浅棕色
                    Color(0xFFD2691E), // 橙棕色
                    Color(0xFF654321), // 深咖啡色
                    Color(0xFF8B7355)  // 灰棕色
                )

                val barParameters = listOf(
                    BarParameters(
                        dataName = title,
                        data = data.values.map { it.toDouble() },
                        barColor = barColors[0] // 使用棕色
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                ) {
                    BarChart(

                        chartParameters = barParameters,
                        gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        xAxisData = data.categories,
                        isShowGrid = true,
                        animateChart = true,
                        showGridWithSpacer = false, // 关闭间距，使用默认对齐
                        yAxisStyle = TextStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        ),
                        xAxisStyle = TextStyle(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.W500
                        ),
                        yAxisRange = (data.values.maxOrNull() ?: 10) + 5,
                        barWidth = barWidth
                    )
                }
                // 使用与官方示例相同的布局方式


            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 48.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "暂无数据",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * 统计项组件
 * @param label 标签
 * @param value 数值
 * @param color 颜色
 */
@Composable
private fun StatisticItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 获取图表颜色 - 优化对比度
 * @param index 索引
 * @return 颜色
 */
private fun getChartColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF1E40AF), // 深蓝色
        Color(0xFFDC2626), // 深红色
        Color(0xFF059669), // 深绿色
        Color(0xFFD97706), // 深橙色
        Color(0xFF7C3AED), // 深紫色
        Color(0xFF0891B2), // 深青色
        Color(0xFFBE185D), // 深粉色
        Color(0xFF65A30D), // 深黄绿色
        Color(0xFF374151), // 深灰色
        Color(0xFF0F766E)  // 深蓝绿色
    )
    return colors[index % colors.size]
}