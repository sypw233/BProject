package ovo.sypw.bsp.domain.repository

import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.RequestLogDto
import ovo.sypw.bsp.data.dto.result.NetworkResult

/**
 * 请求日志管理仓库接口
 * 定义请求日志管理相关的业务操作
 */
interface RequestLogRepository : BaseRepository {

    /**
     * 获取请求日志分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param userId 用户ID（可选，用于筛选）
     * @param username 用户名（可选，用于筛选）
     * @param requestMethod 请求方法（可选，用于筛选）
     * @param requestUrl 请求URL（可选，用于筛选）
     * @param responseStatus 响应状态码（可选，用于筛选）
     * @param ipAddress IP地址（可选，用于筛选）
     * @param createdAtStart 创建时间开始（可选，用于筛选）
     * @param createdAtEnd 创建时间结束（可选，用于筛选）
     * @return 请求日志分页数据结果
     */
    suspend fun getRequestLogPage(
        current: Int = 1,
        size: Int = 10,
        userId: Int? = null,
        username: String? = null,
        requestMethod: String? = null,
        requestUrl: String? = null,
        responseStatus: Int? = null,
        ipAddress: String? = null,
        createdAtStart: String? = null,
        createdAtEnd: String? = null
    ): NetworkResult<PageResultDto<RequestLogDto>>

    /**
     * 根据ID获取请求日志详情
     * @param id 日志ID
     * @return 请求日志详情数据结果
     */
    suspend fun getRequestLogById(id: Int): NetworkResult<RequestLogDto>

    /**
     * 清理指定时间之前的请求日志
     * @param beforeDate 清理此日期之前的日志
     * @return 清理结果
     */
    suspend fun cleanRequestLogs(beforeDate: String): NetworkResult<String>

    /**
     * 导出请求日志数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID（可选）
     * @param requestMethod 请求方法（可选）
     * @param responseStatus 响应状态码（可选）
     * @return 导出文件的字节数组
     */
    suspend fun exportRequestLogs(
        startDate: String,
        endDate: String,
        userId: Int? = null,
        requestMethod: String? = null,
        responseStatus: Int? = null
    ): NetworkResult<ByteArray>

    /**
     * 获取请求日志统计信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计信息
     */
    suspend fun getRequestLogStatistics(
        startDate: String,
        endDate: String
    ): NetworkResult<Map<String, Any>>
}