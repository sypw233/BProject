package ovo.sypw.bsp.domain.repository

import ovo.sypw.bsp.data.api.UserInfo
import ovo.sypw.bsp.data.api.CreateUserRequest
import ovo.sypw.bsp.data.api.UpdateUserRequest
import ovo.sypw.bsp.data.api.DeleteResult
import ovo.sypw.bsp.domain.model.NetworkResult
import kotlinx.coroutines.flow.Flow

/**
 * 示例Repository接口
 * 定义用户相关的数据访问方法
 */
interface ExampleRepository {
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return Flow<NetworkResult<UserInfo>>
     */
    fun getUserInfo(userId: String): Flow<NetworkResult<UserInfo>>
    
    /**
     * 获取用户列表
     * @param page 页码
     * @param pageSize 每页大小
     * @return Flow<NetworkResult<List<UserInfo>>>
     */
    fun getUserList(
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<NetworkResult<List<UserInfo>>>
    
    /**
     * 创建用户
     * @param userRequest 用户创建请求
     * @return Flow<NetworkResult<UserInfo>>
     */
    fun createUser(userRequest: CreateUserRequest): Flow<NetworkResult<UserInfo>>
    
    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateRequest 更新请求
     * @return Flow<NetworkResult<UserInfo>>
     */
    fun updateUser(
        userId: String,
        updateRequest: UpdateUserRequest
    ): Flow<NetworkResult<UserInfo>>
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return Flow<NetworkResult<DeleteResult>>
     */
    fun deleteUser(userId: String): Flow<NetworkResult<DeleteResult>>
    
    /**
     * 获取用户信息（带缓存）
     * @param userId 用户ID
     * @param forceRefresh 是否强制刷新
     * @return Flow<NetworkResult<UserInfo>>
     */
    fun getUserInfoWithCache(
        userId: String,
        forceRefresh: Boolean = false
    ): Flow<NetworkResult<UserInfo>>


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