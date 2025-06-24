package ovo.sypw.bsp.domain.repository.impl

import ovo.sypw.bsp.data.api.RequestLogApiService
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.RequestLogDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.isSuccess
import ovo.sypw.bsp.data.dto.result.parseData
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.repository.RequestLogRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 请求日志管理仓库实现类
 * 整合网络API和本地存储，提供完整的请求日志管理功能
 */
class RequestLogRepositoryImpl(
    private val requestLogApiService: RequestLogApiService,
    private val tokenStorage: TokenStorage
) : RequestLogRepository {

    companion object {
        private const val TAG = "RequestLogRepositoryImpl"
    }

    /**
     * 获取认证令牌
     * @return 认证令牌，如果未登录则返回null
     */
    private suspend fun getAuthToken(): String? {
        return tokenStorage.getAccessToken()
    }

    /**
     * 获取请求日志分页列表
     */
    override suspend fun getRequestLogPage(
        current: Int,
        size: Int,
        userId: Int?,
        username: String?,
        requestMethod: String?,
        requestUrl: String?,
        responseStatus: Int?,
        ipAddress: String?,
        createdAtStart: String?,
        createdAtEnd: String?
    ): NetworkResult<PageResultDto<RequestLogDto>> {
        Logger.d(TAG, "获取请求日志分页列表: current=$current, size=$size")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取请求日志分页列表失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = requestLogApiService.getRequestLogPage(
            current,
            size,
            userId,
            username,
            requestMethod,
            requestUrl,
            responseStatus,
            ipAddress,
            createdAtStart,
            createdAtEnd,
            token
        )) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取请求日志分页列表请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val pageResult = saResult.parseData<PageResultDto<RequestLogDto>>()
                    if (pageResult != null) {
                        Logger.d(TAG, "请求日志分页数据解析成功，共 ${pageResult.total} 条记录")
                        NetworkResult.Success(pageResult)
                    } else {
                        Logger.e(TAG, "请求日志分页数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "请求日志数据格式错误"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取请求日志分页列表失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取请求日志分页列表网络请求失败: ${result.message}")
                result
            }

            else -> {
                Logger.w(TAG, "获取请求日志分页列表: 未知状态")
                NetworkResult.Error(
                    exception = Exception("未知错误"),
                    message = "获取请求日志列表失败"
                )
            }
        }
    }

    /**
     * 根据ID获取请求日志详情
     */
    override suspend fun getRequestLogById(id: Int): NetworkResult<RequestLogDto> {
        Logger.d(TAG, "获取请求日志详情: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取请求日志详情失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = requestLogApiService.getRequestLogById(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取请求日志详情请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val requestLog = saResult.parseData<RequestLogDto>()
                    if (requestLog != null) {
                        Logger.d(TAG, "请求日志详情数据解析成功")
                        NetworkResult.Success(requestLog)
                    } else {
                        Logger.e(TAG, "请求日志详情数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "请求日志详情数据格式错误"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取请求日志详情失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取请求日志详情网络请求失败: ${result.message}")
                result
            }

            else -> {
                Logger.w(TAG, "获取请求日志详情: 未知状态")
                NetworkResult.Error(
                    exception = Exception("未知错误"),
                    message = "获取请求日志详情失败"
                )
            }
        }
    }

    /**
     * 清理指定时间之前的请求日志
     */
    override suspend fun cleanRequestLogs(beforeDate: String): NetworkResult<String> {
        Logger.d(TAG, "清理请求日志: beforeDate=$beforeDate")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "清理请求日志失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = requestLogApiService.cleanRequestLogs(beforeDate, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "清理请求日志请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.d(TAG, "请求日志清理成功")
                    NetworkResult.Success("清理成功")
                } else {
                    Logger.w(TAG, "清理请求日志失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "清理请求日志网络请求失败: ${result.message}")
                result
            }

            else -> {
                Logger.w(TAG, "清理请求日志: 未知状态")
                NetworkResult.Error(
                    exception = Exception("未知错误"),
                    message = "清理请求日志失败"
                )
            }
        }
    }

    /**
     * 导出请求日志数据
     */
    override suspend fun exportRequestLogs(
        startDate: String,
        endDate: String,
        userId: Int?,
        requestMethod: String?,
        responseStatus: Int?
    ): NetworkResult<ByteArray> {
        Logger.d(TAG, "导出请求日志: startDate=$startDate, endDate=$endDate")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "导出请求日志失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        // 注意：这里需要根据实际API实现导出功能
        // 目前返回一个模拟的错误，实际项目中需要实现对应的API
        return NetworkResult.Error(
            exception = Exception("功能未实现"),
            message = "导出功能暂未实现"
        )
    }

    /**
     * 获取请求日志统计信息
     */
    override suspend fun getRequestLogStatistics(
        startDate: String,
        endDate: String
    ): NetworkResult<Map<String, Any>> {
        Logger.d(TAG, "获取请求日志统计信息: startDate=$startDate, endDate=$endDate")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取请求日志统计信息失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        // 注意：这里需要根据实际API实现统计功能
        // 目前返回一个模拟的错误，实际项目中需要实现对应的API
        return NetworkResult.Error(
            exception = Exception("功能未实现"),
            message = "统计功能暂未实现"
        )
    }
}