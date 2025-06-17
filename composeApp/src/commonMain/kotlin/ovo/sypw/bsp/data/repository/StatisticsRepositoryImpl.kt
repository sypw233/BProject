package ovo.sypw.bsp.data.repository

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import ovo.sypw.bsp.data.api.StatisticsApiService
import ovo.sypw.bsp.data.model.StatisticsData
import ovo.sypw.bsp.data.model.PieChartItem
import ovo.sypw.bsp.data.model.BarChartData
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.StatisticsRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 统计数据仓库实现
 * 整合网络API和本地存储，提供完整的统计数据功能
 */
class StatisticsRepositoryImpl(
    private val apiService: StatisticsApiService,
    private val tokenStorage: TokenStorage
) : StatisticsRepository {
    
    companion object {
        private const val TAG = "StatisticsRepositoryImpl"
    }
    
    /**
     * 获取认证令牌
     * @return 认证令牌，如果未登录则返回null
     */
    private suspend fun getAuthToken(): String? {
        return tokenStorage.getAccessToken()
    }
    
    /**
     * 获取仪表板统计数据
     * @return 统计数据
     */
    override suspend fun getDashboardStatistics(): StatisticsData {
        Logger.d(TAG, "获取仪表板统计数据")
        
        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取统计数据失败: 未找到认证令牌")
            throw Exception("请先登录")
        }
        
        return when (val result = apiService.getDashboardStatistics(token)) {
             is NetworkResult.Success -> {
                 if (result.data.code == 200) {
                     Logger.d(TAG, "获取统计数据成功")
                     try {
                         result.data.data?.let { jsonElement ->
                             Json.decodeFromJsonElement<StatisticsData>(jsonElement)
                         } ?: getEmptyStatisticsData()
                     } catch (e: Exception) {
                         Logger.e(TAG, "解析统计数据失败", e)
                         getEmptyStatisticsData()
                     }
                 } else {
                     Logger.e(TAG, "获取统计数据失败: ${result.data.msg}")
                     throw Exception(result.data.msg)
                 }
             }
             is NetworkResult.Error -> {
                 Logger.e(TAG, "获取统计数据失败: ${result.message}")
                 throw result.exception ?: Exception(result.message)
             }

            NetworkResult.Idle -> TODO()
            NetworkResult.Loading -> TODO()
        }
    }
    
    /**
     * 获取空的统计数据
     * @return 空的统计数据
     */
    private fun getEmptyStatisticsData(): StatisticsData {
        return StatisticsData(
            studentStatusChart = emptyList(),
            employeeJobChart = emptyList(),
            classStudentChart = BarChartData(emptyList(), emptyList()),
            departmentEmployeeChart = BarChartData(emptyList(), emptyList()),
            studentGenderChart = emptyList(),
            employeeEntryYearChart = BarChartData(emptyList(), emptyList()),
            studentJoinYearChart = BarChartData(emptyList(), emptyList())
        )
    }
}