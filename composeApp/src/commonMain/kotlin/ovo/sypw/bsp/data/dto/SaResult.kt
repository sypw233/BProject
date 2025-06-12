package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * SaResult响应数据类
 * 匹配后端实际返回的数据格式
 * 标准格式：{ "code": 200, "msg": "success", "data": {...} }
 */
@Serializable
data class SaResult(
    /**
     * 响应状态码
     */
    val code: Int,
    
    /**
     * 响应消息
     */
    val msg: String,
    
    /**
     * 响应数据，可以是任意JSON结构
     */
    val data: JsonElement? = null
)

/**
 * 扩展函数：检查SaResult是否成功
 * @return 是否成功（通常code为200表示成功）
 */
fun SaResult.isSuccess(): Boolean {
    return code == 200
}

/**
 * 获取SaResult中的数据
 * @return JsonElement类型的数据
 */
fun SaResult.getData(): JsonElement? {
    return data
}

/**
 * 检查是否为错误响应
 * @return 是否为错误
 */
fun SaResult.isError(): Boolean {
    return code != 200
}

/**
 * 获取错误消息
 * @return 错误消息
 */
fun SaResult.getErrorMessage(): String {
    return if (isError()) msg else ""
}