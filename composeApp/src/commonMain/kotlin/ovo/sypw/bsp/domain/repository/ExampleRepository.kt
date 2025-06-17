package ovo.sypw.bsp.domain.repository


import ovo.sypw.bsp.data.dto.result.NetworkResult
import kotlinx.coroutines.flow.Flow

/**
 * 示例Repository接口
 * 定义用户相关的数据访问方法
 */
interface ExampleRepository {


    /**
     * 获取示例数据（用于API测试）
     * @return NetworkResult<String>
     */
    suspend fun getExampleData(): NetworkResult<String>
    
    /**
     * 发送示例数据（用于API测试）
     * @param data 要发送的数据
     * @return NetworkResult<String>
     */
    suspend fun postExampleData(data: String): NetworkResult<String>
}