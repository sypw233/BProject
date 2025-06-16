package ovo.sypw.bsp.domain.repository

import ovo.sypw.bsp.data.model.StatisticsData

/**
 * 统计数据仓库接口
 */
interface StatisticsRepository {
    
    /**
     * 获取仪表板统计数据
     * @return 统计数据
     */
    suspend fun getDashboardStatistics(): StatisticsData
}