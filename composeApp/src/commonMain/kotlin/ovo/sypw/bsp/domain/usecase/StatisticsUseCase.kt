package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.model.StatisticsData
import ovo.sypw.bsp.domain.repository.StatisticsRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 统计数据用例
 */
class StatisticsUseCase(
    private val repository: StatisticsRepository
) {
    
    /**
     * 获取仪表板统计数据
     * @return 统计数据
     */
    suspend fun getDashboardStatistics(): StatisticsData {
        Logger.d("StatisticsUseCase", "获取仪表板统计数据")
        
        return try {
            val data = repository.getDashboardStatistics()
            Logger.d("StatisticsUseCase", "获取统计数据成功")
            data
        } catch (e: Exception) {
            Logger.e("StatisticsUseCase", "获取统计数据失败", e)
            throw e
        }
    }
}