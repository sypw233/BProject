package ovo.sypw.bsp.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.RequestLogDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.RequestLogRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 请求日志管理用例类
 * 封装请求日志相关的业务逻辑，提供给表现层使用
 */
class RequestLogUseCase(
    private val requestLogRepository: RequestLogRepository
) {

    companion object {
        private const val TAG = "RequestLogUseCase"
    }

    /**
     * 获取请求日志分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param userId 用户ID筛选（可选）
     * @param username 用户名筛选（可选，模糊查询）
     * @param requestMethod 请求方法筛选（可选）
     * @param requestUrl 请求URL筛选（可选，模糊查询）
     * @param responseStatus 响应状态码筛选（可选）
     * @param ipAddress IP地址筛选（可选，模糊查询）
     * @param createdAtStart 创建时间范围开始（可选）
     * @param createdAtEnd 创建时间范围结束（可选）
     * @return Flow<NetworkResult<PageResultDto<RequestLogDto>>>
     */
    fun getRequestLogPage(
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
    ): Flow<NetworkResult<PageResultDto<RequestLogDto>>> = flow {
        Logger.d(TAG, "开始获取请求日志分页列表: current=$current, size=$size")

        emit(NetworkResult.Loading)

        try {
            val result = requestLogRepository.getRequestLogPage(
                current = current,
                size = size,
                userId = userId,
                username = username,
                requestMethod = requestMethod,
                requestUrl = requestUrl,
                responseStatus = responseStatus,
                ipAddress = ipAddress,
                createdAtStart = createdAtStart,
                createdAtEnd = createdAtEnd
            )

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "获取请求日志分页列表成功: ${result.data.records.size}条记录")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "获取请求日志分页列表失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "获取请求日志分页列表异常: ${e.message}")
            emit(NetworkResult.Error(e, "获取请求日志列表失败: ${e.message}"))
        }
    }

    /**
     * 根据ID获取请求日志详情
     * @param id 日志ID
     * @return Flow<NetworkResult<RequestLogDto>>
     */
    fun getRequestLogById(id: Int): Flow<NetworkResult<RequestLogDto>> = flow {
        Logger.d(TAG, "开始获取请求日志详情: id=$id")

        emit(NetworkResult.Loading)

        try {
            val result = requestLogRepository.getRequestLogById(id)

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "获取请求日志详情成功: ${result.data.id}")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "获取请求日志详情失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "获取请求日志详情异常: ${e.message}")
            emit(NetworkResult.Error(e, "获取请求日志详情失败: ${e.message}"))
        }
    }

    /**
     * 清理指定时间之前的请求日志
     * @param beforeDate 清理此日期之前的日志
     * @return Flow<NetworkResult<String>>
     */
    fun cleanRequestLogs(beforeDate: String): Flow<NetworkResult<String>> = flow {
        Logger.d(TAG, "开始清理请求日志: beforeDate=$beforeDate")

        emit(NetworkResult.Loading)

        try {
            val result = requestLogRepository.cleanRequestLogs(beforeDate)

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "清理请求日志成功")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "清理请求日志失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "清理请求日志异常: ${e.message}")
            emit(NetworkResult.Error(e, "清理请求日志失败: ${e.message}"))
        }
    }

    /**
     * 导出请求日志数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID（可选）
     * @param requestMethod 请求方法（可选）
     * @param responseStatus 响应状态码（可选）
     * @return Flow<NetworkResult<ByteArray>>
     */
    fun exportRequestLogs(
        startDate: String,
        endDate: String,
        userId: Int? = null,
        requestMethod: String? = null,
        responseStatus: Int? = null
    ): Flow<NetworkResult<ByteArray>> = flow {
        Logger.d(TAG, "开始导出请求日志: startDate=$startDate, endDate=$endDate")

        emit(NetworkResult.Loading)

        try {
            val result = requestLogRepository.exportRequestLogs(
                startDate = startDate,
                endDate = endDate,
                userId = userId,
                requestMethod = requestMethod,
                responseStatus = responseStatus
            )

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "导出请求日志成功")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "导出请求日志失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "导出请求日志异常: ${e.message}")
            emit(NetworkResult.Error(e, "导出请求日志失败: ${e.message}"))
        }
    }

    /**
     * 获取请求日志统计信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return Flow<NetworkResult<Map<String, Any>>>
     */
    fun getRequestLogStatistics(
        startDate: String,
        endDate: String
    ): Flow<NetworkResult<Map<String, Any>>> = flow {
        Logger.d(TAG, "开始获取请求日志统计信息: startDate=$startDate, endDate=$endDate")

        emit(NetworkResult.Loading)

        try {
            val result = requestLogRepository.getRequestLogStatistics(
                startDate = startDate,
                endDate = endDate
            )

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "获取请求日志统计信息成功")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "获取请求日志统计信息失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "获取请求日志统计信息异常: ${e.message}")
            emit(NetworkResult.Error(e, "获取请求日志统计信息失败: ${e.message}"))
        }
    }

    /**
     * 验证日期格式
     * @param date 日期字符串
     * @return 是否为有效日期格式
     */
    private fun isValidDateFormat(date: String): Boolean {
        return try {
            // 这里可以添加具体的日期格式验证逻辑
            // 例如：yyyy-MM-dd HH:mm:ss 格式
            date.isNotBlank() && date.length >= 10
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 验证分页参数
     * @param current 当前页码
     * @param size 每页大小
     * @return 是否为有效参数
     */
    private fun isValidPagingParams(current: Int, size: Int): Boolean {
        return current > 0 && size > 0 && size <= 100 // 限制每页最大100条
    }
}