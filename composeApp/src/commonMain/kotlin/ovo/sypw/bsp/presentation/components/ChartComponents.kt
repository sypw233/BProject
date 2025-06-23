package ovo.sypw.bsp.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Pie

import ovo.sypw.bsp.data.model.BarChartData
import ovo.sypw.bsp.data.model.PieChartItem
import ovo.sypw.bsp.utils.StringUtils.format

/**
 * 增强版饼图组件
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
    var isVisible by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(0.8f + 0.2f * animationProgress),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (data.isNotEmpty()) {
                // 计算总值用于百分比计算
                val totalValue = data.sumOf { it.value }
                
                val pieData = data.mapIndexed { index, item ->
                    Pie(
                        label = item.name,
                        data = item.value.toDouble() * animationProgress,
                        color = getChartColor(index),
                        selectedColor = getChartColor(index).copy(alpha = 0.8f)
                    )
                }

                // 饼图容器
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 背景圆环
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                    )
                                )
                            )
                    )
                    
                    PieChart(
                        modifier = Modifier.size(180.dp),
                        data = pieData,
                        onPieClick = { pie ->
                            // 可以在这里处理点击事件
                        },
                        selectedScale = 1.1f,
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
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
                        
                        // 自适应网格布局显示数据详情 - 确保每行至少两个项目
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 100.dp),
                            modifier = Modifier.heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(data) { item ->
                                val index = data.indexOf(item)
                                val percentage = if (totalValue > 0) (item.value.toFloat() / totalValue * 100) else 0f
                                
                                StatisticItem(
                                    label = item.name,
                                    value = "${item.value} (${String.format("%.1f", percentage)}%)",
                                    color = getChartColor(index)
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "暂无数据",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * 增强版柱状图组件
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
    var isVisible by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(0.8f + 0.2f * animationProgress),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (data.categories.isNotEmpty() && data.values.isNotEmpty()) {
                // 计算合适的图表高度
                val chartHeight = when {
                    data.categories.size <= 3 -> 300.dp
                    data.categories.size <= 6 -> 340.dp
                    data.categories.size <= 10 -> 380.dp
                    else -> 420.dp
                }

                // 创建柱状图数据
                val barsData = data.categories.mapIndexed { index, category ->
                    Bars(
                        label = category,
                        values = listOf(
                            Bars.Data(
                                label = category,
                                value = (data.values.getOrNull(index)?.toDouble() ?: 0.0) * animationProgress,
                                color = androidx.compose.ui.graphics.SolidColor(getChartColor(index))
                            )
                        )
                    )
                }

                // 柱状图容器
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    RowChart(
                        modifier = Modifier.fillMaxSize(),
                        data = barsData,
                        barProperties = BarProperties(
                            cornerRadius = Bars.Data.Radius.Rectangle(
                                topRight = 12.dp, 
                                topLeft = 12.dp,
                                bottomRight = 4.dp,
                                bottomLeft = 4.dp
                            ),
                            spacing = 8.dp,
                            thickness = 24.dp
                        ),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
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
                        
                        // 自适应网格布局显示数据详情 - 确保每行至少两个项目
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 100.dp),
                            modifier = Modifier.heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(data.categories.size) { index ->
                                val category = data.categories[index]
                                val value = data.values.getOrNull(index) ?: 0
                                
                                StatisticItem(
                                    label = category,
                                    value = value.toString(),
                                    color = getChartColor(index)
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "暂无数据",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ),
            color = color,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
        )
    }
}

/**
 * 获取统计卡片图标
 * @param title 标题
 * @return 对应的图标
 */
private fun getStatisticIcon(title: String): ImageVector {
    return when {
        title.contains("学生") -> Icons.Default.School
        title.contains("员工") -> Icons.Default.Work
        title.contains("班级") -> Icons.Default.Group
        title.contains("部门") -> Icons.Default.Person
        else -> Icons.Default.Person
    }
}

/**
 * 获取统计卡片渐变色
 * @param title 标题
 * @return 渐变色画刷
 */
private fun getStatisticGradient(title: String): Brush {
    return when {
        title.contains("学生") -> Brush.linearGradient(
            colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
        )
        title.contains("员工") -> Brush.linearGradient(
            colors = listOf(Color(0xFF11998e), Color(0xFF38ef7d))
        )
        title.contains("班级") -> Brush.linearGradient(
            colors = listOf(Color(0xFFf093fb), Color(0xFFf5576c))
        )
        title.contains("部门") -> Brush.linearGradient(
            colors = listOf(Color(0xFF4facfe), Color(0xFF00f2fe))
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
        )
    }
}

/**
 * 增强版统计卡片组件
 * @param title 标题
 * @param count 数量
 * @param modifier 修饰符
 */
@Composable
fun StatisticCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = getStatisticGradient(title),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getStatisticIcon(title),
                        contentDescription = title,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
            }
        }
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