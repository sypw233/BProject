package ovo.sypw.bsp.data.repository

import ovo.sypw.bsp.data.api.ExampleApiService
import ovo.sypw.bsp.data.api.UserInfo
import ovo.sypw.bsp.data.api.CreateUserRequest
import ovo.sypw.bsp.data.api.UpdateUserRequest
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
     * 获取用户信息
     * @param userId 用户ID
     * @return Flow<NetworkResult<UserInfo>>
     */
    override fun getUserInfo(userId: String): Flow<NetworkResult<UserInfo>> {
        return performNetworkCall {
            apiService.getUserInfo(userId)
        }
    }
    
    /**
     * 获取用户列表
     * @param page 页码
     * @param pageSize 每页大小
     * @return Flow<NetworkResult<List<UserInfo>>>
     */
    override fun getUserList(
        page: Int,
        pageSize: Int
    ): Flow<NetworkResult<List<UserInfo>>> {
        return performNetworkCall {
            apiService.getUserList(page, pageSize)
        }
    }
    
    /**
     * 创建用户
     * @param userRequest 用户创建请求
     * @return Flow<NetworkResult<UserInfo>>
     */
    override fun createUser(userRequest: CreateUserRequest): Flow<NetworkResult<UserInfo>> {
        return performNetworkCall {
            apiService.createUser(userRequest)
        }
    }
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateRequest 更新请求
     * @return Flow<NetworkResult<UserInfo>>
     */
    override fun updateUser(
        userId: String,
        updateRequest: UpdateUserRequest
    ): Flow<NetworkResult<UserInfo>> {
        return performNetworkCall {
            apiService.updateUser(userId, updateRequest)
        }
    }
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return Flow<NetworkResult<DeleteResult>>
     */
    override fun deleteUser(userId: String): Flow<NetworkResult<DeleteResult>> {
        return performNetworkCall {
            apiService.deleteUser(userId)
        }
    }
    
    /**
     * 获取用户信息（带缓存示例）
     * @param userId 用户ID
     * @param forceRefresh 是否强制刷新
     * @return Flow<NetworkResult<UserInfo>>
     */
    override fun getUserInfoWithCache(
        userId: String,
        forceRefresh: Boolean
    ): Flow<NetworkResult<UserInfo>> {
        return performNetworkCallWithCache(
            cacheCall = {
                // 这里应该从本地缓存获取数据
                // 示例：从数据库或SharedPreferences获取
                null // 暂时返回null，实际项目中应该实现缓存逻辑
            },
            networkCall = {
                apiService.getUserInfo(userId)
            },
            saveCall = { userInfo ->
                // 这里应该保存数据到本地缓存
                // 示例：保存到数据库或SharedPreferences
            },
            shouldFetch = { cachedData ->
                forceRefresh || cachedData == null
            }
        )
    }


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