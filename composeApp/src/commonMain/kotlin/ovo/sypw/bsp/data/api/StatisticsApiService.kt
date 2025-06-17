package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.utils.Logger

/**
 * 统计数据API服务
 * 提供统计相关的所有API调用方法
 */
class StatisticsApiService : BaseApiService() {
    
    companion object {
        private const val TAG = "StatisticsApiService"
        
        // API端点常量
        private const val DASHBOARD_STATISTICS_ENDPOINT = "/dashboard/statistics"
    }
    
    /**
     * 获取仪表板统计数据
     * @param token 认证令牌
     * @return 统计数据响应
     */
    suspend fun getDashboardStatistics(
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取仪表板统计数据")
        
        return getWithToken(
            endpoint = DASHBOARD_STATISTICS_ENDPOINT,
            token = token
        )
    }
}