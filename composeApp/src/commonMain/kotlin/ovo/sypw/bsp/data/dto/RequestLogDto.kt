package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 请求日志数据传输对象
 */
@Serializable
data class RequestLogDto(
    /**
     * 日志ID
     */
    val id: Int,

    /**
     * 用户ID
     */
    val userId: Int,

    /**
     * 用户名
     */
    val username: String,

    /**
     * 请求方法 (GET, POST, PUT, DELETE等)
     */
    val requestMethod: String,

    /**
     * 请求URL
     */
    val requestUrl: String,

    /**
     * 请求参数
     */
    val requestParams: String? = null,

    /**
     * 请求体
     */
    val requestBody: String? = null,

    /**
     * 响应状态码
     */
    val responseStatus: Int,

    /**
     * 响应时间（毫秒）
     */
    val responseTime: Int,

    /**
     * IP地址
     */
    val ipAddress: String,

    /**
     * 用户代理
     */
    val userAgent: String,

    /**
     * 创建时间
     */
    val createdAt: String
)

/**
 * 请求日志筛选条件DTO
 */
@Serializable
data class RequestLogFilterDto(
    /**
     * 用户ID筛选
     */
    val userId: Int? = null,

    /**
     * 用户名筛选
     */
    val username: String? = null,

    /**
     * 请求方法筛选
     */
    val requestMethod: String? = null,

    /**
     * 请求URL筛选
     */
    val requestUrl: String? = null,

    /**
     * 响应状态码筛选
     */
    val responseStatus: Int? = null,

    /**
     * IP地址筛选
     */
    val ipAddress: String? = null,

    /**
     * 创建时间开始
     */
    val createdAtStart: String? = null,

    /**
     * 创建时间结束
     */
    val createdAtEnd: String? = null
)

/**
 * 请求方法枚举
 */
enum class RequestMethod(val value: String, val displayName: String) {
    GET("GET", "GET"),
    POST("POST", "POST"),
    PUT("PUT", "PUT"),
    DELETE("DELETE", "DELETE"),
    PATCH("PATCH", "PATCH"),
    HEAD("HEAD", "HEAD"),
    OPTIONS("OPTIONS", "OPTIONS");

    companion object {
        /**
         * 获取所有请求方法选项
         */
        fun getAllOptions(): List<RequestMethod> = values().toList()

        /**
         * 根据值获取请求方法
         */
        fun fromValue(value: String): RequestMethod? = values().find { it.value == value }
    }
}

/**
 * HTTP状态码分类
 */
enum class ResponseStatusCategory(val range: IntRange, val displayName: String, val color: String) {
    SUCCESS(200..299, "成功", "#4CAF50"),
    REDIRECT(300..399, "重定向", "#FF9800"),
    CLIENT_ERROR(400..499, "客户端错误", "#F44336"),
    SERVER_ERROR(500..599, "服务器错误", "#9C27B0");

    companion object {
        /**
         * 根据状态码获取分类
         */
        fun fromStatusCode(statusCode: Int): ResponseStatusCategory? {
            return values().find { statusCode in it.range }
        }

        /**
         * 获取所有状态码分类选项
         */
        fun getAllOptions(): List<ResponseStatusCategory> = values().toList()
    }
}