package ovo.sypw.bsp.data.api

import kotlinx.serialization.Serializable
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult

/**
 * 示例API服务类
 * 演示如何继承BaseApiService并实现具体的API调用
 */
class ExampleApiService : BaseApiService() {

    /**
     * 获取微博热搜数据（需要认证）
     * @param token 认证令牌
     * @return 微博热搜数据
     */
    suspend fun getWeiboData(token: String): NetworkResult<SaResult> {
        val resWeibo: NetworkResult<SaResult> = getWithToken(
            endpoint = "weibohot",
            token = token
        )
        return resWeibo
    }

    /**
     * 获取示例数据（用于API测试）
     * @return 示例数据字符串
     */
    suspend fun getExampleData(): String {
        // 模拟API调用，返回示例数据
        return "这是来自服务器的GET响应数据，时间戳: ${
            kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        }"
    }

    /**
     * 发送示例数据（用于API测试）
     * @param data 要发送的数据
     * @return 服务器响应
     */
    suspend fun postExampleData(data: String): String {
        // 模拟API调用，返回处理结果
        return "服务器已接收数据: '$data'，处理时间: ${
            kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        }"
    }
}


/**
 * 删除结果数据类
 */
@Serializable
data class DeleteResult(
    val success: Boolean,
    val message: String
)