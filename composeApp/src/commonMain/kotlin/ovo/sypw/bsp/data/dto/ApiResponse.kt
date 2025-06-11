package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * API响应基础数据类
 * @param T 数据类型
 */
@Serializable
data class ApiResponse<T>(
    /**
     * 响应状态码
     */
    val code: Int,
    
    /**
     * 响应消息
     */
    val message: String,
    
    /**
     * 响应数据
     */
    val data: T? = null,
    
    /**
     * 请求是否成功
     */
    val success: Boolean = code == 200
)

/**
 * 分页响应数据类
 * @param T 列表项数据类型
 */
@Serializable
data class PageResponse<T>(
    /**
     * 数据列表
     */
    val list: List<T>,
    
    /**
     * 当前页码
     */
    val page: Int,
    
    /**
     * 每页大小
     */
    val pageSize: Int,
    
    /**
     * 总数量
     */
    val total: Long,
    
    /**
     * 总页数
     */
    val totalPages: Int,
    
    /**
     * 是否有下一页
     */
    val hasNext: Boolean = page < totalPages
)

/**
 * 错误响应数据类
 */
@Serializable
data class ErrorResponse @OptIn(ExperimentalTime::class) constructor(
    /**
     * 错误码
     */
    val errorCode: String,
    
    /**
     * 错误消息
     */
    val errorMessage: String,
    
    /**
     * 错误详情
     */
    val details: String? = null,
    
    /**
     * 时间戳
     */
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)