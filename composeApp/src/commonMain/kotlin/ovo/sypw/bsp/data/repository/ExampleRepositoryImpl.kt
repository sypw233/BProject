package ovo.sypw.bsp.data.repository

import ovo.sypw.bsp.data.api.ExampleApiService

import ovo.sypw.bsp.data.api.DeleteResult
import ovo.sypw.bsp.domain.repository.BaseRepository
import ovo.sypw.bsp.domain.repository.ExampleRepository
import ovo.sypw.bsp.domain.model.NetworkResult
import kotlinx.coroutines.flow.Flow

/**
 * 示例Repository实现类
 * 演示如何实现Repository接口并使用BaseRepository的通用方法
 */
class ExampleRepositoryImpl(
    private val apiService: ExampleApiService
) : ExampleRepository, BaseRepository {
    



    /**
     * 获取示例数据（用于API测试）
     * @return NetworkResult<String>
     */
    override suspend fun getExampleData(): NetworkResult<String> {
        return try {
            val response = apiService.getWeiboData()
            NetworkResult.Success("GET请求成功，返回数据: $response")
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
    
    /**
     * 发送示例数据（用于API测试）
     * @param data 要发送的数据
     * @return NetworkResult<String>
     */
    override suspend fun postExampleData(data: String): NetworkResult<String> {
        return try {
            val response = apiService.postExampleData(data)
            NetworkResult.Success("POST请求成功，返回数据: $response")
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
    }
}