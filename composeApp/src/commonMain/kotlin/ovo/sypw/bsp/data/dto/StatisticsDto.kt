package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 统计数据模型
 */
@Serializable
data class StatisticsData(
    val totalStudents: Int,
    val totalEmployees: Int,
    val totalClasses: Int,
    val totalDepartments: Int,
    val studentStatusChart: List<PieChartItem>,
    val employeeJobChart: List<PieChartItem>,
    val classStudentChart: BarChartData,
    val departmentEmployeeChart: BarChartData,
    val studentGenderChart: List<PieChartItem>,
    val employeeEntryYearChart: BarChartData,
    val studentJoinYearChart: BarChartData
)

/**
 * 饼图数据项
 */
@Serializable
data class PieChartItem(
    val name: String,
    val value: Int
)

/**
 * 柱状图数据
 */
@Serializable
data class BarChartData(
    val categories: List<String>,
    val values: List<Int>
)