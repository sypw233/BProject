package ovo.sypw.bsp.presentation.components

import androidx.compose.foundation.layout.*
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            
            if (data.categories.isNotEmpty() && data.values.isNotEmpty()) {
                // 计算合适的图表高度
                val chartHeight = when {
                    data.categories.size <= 3 -> 280.dp
                    data.categories.size <= 6 -> 320.dp
                    data.categories.size <= 10 -> 360.dp
                    else -> 400.dp
                }
                
                // 计算合适的柱子宽度
                val barWidth = when {
                    data.categories.size <= 3 -> 40.dp
                    data.categories.size <= 6 -> 30.dp
                    data.categories.size <= 10 -> 25.dp
                    else -> 20.dp
                }
                
                val barParameters = listOf(
                    BarParameters(
                        dataName = title,
                        data = data.values.map { it.toDouble() },
                        barColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                ) {
                    BarChart(
                        chartParameters = barParameters,
                        gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        xAxisData = data.categories,
                        isShowGrid = true,
                        animateChart = true,
                        showGridWithSpacer = true,
                        yAxisStyle = TextStyle(
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        ),
                        xAxisStyle = TextStyle(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.W500
                        ),
                        yAxisRange = (data.values.maxOrNull() ?: 10) + 5,
                        barWidth = barWidth
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "暂无数据",
                            style = MaterialTheme.typography.bodyLarge,
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
 * 获取图表颜色
 * @param index 索引
 * @return 颜色
 */
private fun getChartColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Violet
        Color(0xFF06B6D4), // Cyan
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B), // Amber
        Color(0xFFEF4444), // Red
        Color(0xFFEC4899), // Pink
        Color(0xFF84CC16), // Lime
        Color(0xFF6B7280), // Gray
        Color(0xFF14B8A6)  // Teal
    )
    return colors[index % colors.size]
}