package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.domain.model.NetworkResult
import kotlinx.serialization.Serializable

/**
 * 示例API服务类
 * 演示如何继承BaseApiService并实现具体的API调用
 */
class ExampleApiService : BaseApiService() {
    
    /**
     * 获取用户信息示例
     * @param userId 用户ID
     * @return 用户信息
     */
    suspend fun getUserInfo(userId: String): NetworkResult<UserInfo> {
        return get(
            endpoint = "users/$userId"
        )
    }
    
    /**
     * 获取用户列表示例
     * @param page 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    suspend fun getUserList(
        page: Int = 1,
        pageSize: Int = 20
    ): NetworkResult<List<UserInfo>> {
        return get(
            endpoint = "users",
            parameters = mapOf(
                "page" to page,
                "pageSize" to pageSize
            )
        )
    }
    
    /**
     * 创建用户示例
     * @param userRequest 用户创建请求
     * @return 创建的用户信息
     */
    suspend fun createUser(userRequest: CreateUserRequest): NetworkResult<UserInfo> {
        return post(
            endpoint = "users",
            body = userRequest
        )
    }
    
    /**
     * 更新用户信息示例
     * @param userId 用户ID
     * @param updateRequest 更新请求
     * @return 更新后的用户信息
     */
    suspend fun updateUser(
        userId: String,
        updateRequest: UpdateUserRequest
    ): NetworkResult<UserInfo> {
        return put(
            endpoint = "users/$userId",
            body = updateRequest
        )
    }
    
    /**
     * 删除用户示例
     * @param userId 用户ID
     * @return 删除结果
     */
    suspend fun deleteUser(userId: String): NetworkResult<DeleteResult> {
        return delete(
            endpoint = "users/$userId"
        )
    }
    suspend fun getWeiboData():NetworkResult<String>{
        val resWeibo: NetworkResult<String> = get(
            endpoint = "weibohot",
        )
        return resWeibo

    }
    /**
     * 获取示例数据（用于API测试）
     * @return 示例数据字符串
     */
    suspend fun getExampleData(): String {
        // 模拟API调用，返回示例数据
        return "这是来自服务器的GET响应数据，时间戳: ${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}"
    }

    /**
     * 发送示例数据（用于API测试）
     * @param data 要发送的数据
     * @return 服务器响应
     */
    suspend fun postExampleData(data: String): String {
        // 模拟API调用，返回处理结果
        return "服务器已接收数据: '$data'，处理时间: ${kotlinx.datetime.Clock.System.now().toEpochMilliseconds()}"
    }
}

/**
 * 用户信息数据类
 */
@Serializable
data class UserInfo(
    val id: String,
    val username: String,
    val email: String,
    val avatar: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * 创建用户请求数据类
 */
@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String,
    val avatar: String? = null
)

/**
 * 更新用户请求数据类
 */
@Serializable
data class UpdateUserRequest(
    val username: String? = null,
    val email: String? = null,
    val avatar: String? = null
)




/**
 * 删除结果数据类
 */
@Serializable
data class DeleteResult(
    val success: Boolean,
    val message: String
)