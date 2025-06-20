package ovo.sypw.bsp.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Pie

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
                // 计算总值用于百分比计算
                val totalValue = data.sumOf { it.value }
                
                val pieData = data.mapIndexed { index, item ->
                    Pie(
                        label = item.name,
                        data = item.value.toDouble(),
                        color = getChartColor(index),
                        selectedColor = getChartColor(index).copy(alpha = 0.8f)
                    )
                }

                // 饼图容器
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PieChart(
                        modifier = Modifier
                            .size(220.dp)
                            .padding(12.dp),
                        data = pieData,
                        onPieClick = { pie ->
                            // 可以在这里处理点击事件
                        },
                        selectedScale = 1.1f,
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 数据详情图例
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "数据详情",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        data.forEachIndexed { index, item ->
                            val percentage = if (totalValue > 0) (item.value.toFloat() / totalValue * 100) else 0f
                            
                            StatisticItem(
                                label = item.name,
                                value = "${item.value} (${ percentage}%)",
                                color = getChartColor(index)
                            )
                            
                            if (index < data.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
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

                // 使用棕色系配色方案
                val barColors = listOf(
                    Color(0xFF8B4513), // 深棕色
                    Color(0xFFA0522D), // 中棕色
                    Color(0xFFCD853F), // 浅棕色
                    Color(0xFFD2691E), // 橙棕色
                    Color(0xFF654321), // 深咖啡色
                    Color(0xFF8B7355)  // 灰棕色
                )

                // 创建柱状图数据
                val barsData = data.categories.mapIndexed { index, category ->
                    Bars(
                        label = category,
                        values = listOf(
                            Bars.Data(
                                label = category,
                                value = data.values.getOrNull(index)?.toDouble() ?: 0.0,
                                color = androidx.compose.ui.graphics.SolidColor(barColors[index % barColors.size])
                            )
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                ) {
                    RowChart(
                        modifier = Modifier.fillMaxWidth(),
                        data = barsData,
                        barProperties = BarProperties(
                            spacing = 3.dp,
                        ),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }


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
 * 获取图表颜色 - 现代化配色方案
 * @param index 索引
 * @return 颜色
 */
private fun getChartColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF6366F1), // 现代紫色
        Color(0xFF10B981), // 翠绿色
        Color(0xFFF59E0B), // 琥珀色
        Color(0xFFEF4444), // 珊瑚红
        Color(0xFF8B5CF6), // 薰衣草紫
        Color(0xFF06B6D4), // 天蓝色
        Color(0xFFEC4899), // 玫瑰粉
        Color(0xFF84CC16), // 青柠绿
        Color(0xFF6B7280), // 石墨灰
        Color(0xFF14B8A6), // 青绿色
        Color(0xFFF97316), // 橙色
        Color(0xFF3B82F6)  // 蓝色
    )
    return colors[index % colors.size]
}