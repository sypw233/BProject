package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.utils.Logger

/**
 * 请求日志API服务
 * 提供请求日志相关的所有API调用方法
 */
class RequestLogApiService : BaseApiService() {

    companion object {
        private const val TAG = "RequestLogApiService"

        // API端点常量
        private const val REQUEST_LOGS_PAGE_ENDPOINT = "/request-logs/page"
    }

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
     * @param token 认证令牌
     * @return 请求日志分页数据
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
        createdAtEnd: String? = null,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取请求日志分页列表 - 页码: $current, 大小: $size")
        
        // 构建请求参数
        val parameters = mutableMapOf<String, Any>(
            "current" to current,
            "size" to size
        )
        
        // 添加可选筛选参数
        userId?.let { parameters["userId"] = it }
        username?.takeIf { it.isNotBlank() }?.let { parameters["username"] = it }
        requestMethod?.takeIf { it.isNotBlank() }?.let { parameters["requestMethod"] = it }
        requestUrl?.takeIf { it.isNotBlank() }?.let { parameters["requestUrl"] = it }
        responseStatus?.let { parameters["responseStatus"] = it }
        ipAddress?.takeIf { it.isNotBlank() }?.let { parameters["ipAddress"] = it }
        createdAtStart?.takeIf { it.isNotBlank() }?.let { parameters["createdAtStart"] = it }
        createdAtEnd?.takeIf { it.isNotBlank() }?.let { parameters["createdAtEnd"] = it }
        
        return try {
            val result = getWithToken(
                endpoint = REQUEST_LOGS_PAGE_ENDPOINT,
                token = token,
                parameters = parameters
            )
            
            when (result) {
                is NetworkResult.Success -> {
                    Logger.d(TAG, "请求日志分页列表获取成功")
                    result
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "请求日志分页列表获取失败: ${result.message}")
                    result
                }
                else -> result
            }
        } catch (e: Exception) {
            Logger.e(TAG, "请求日志分页列表获取异常: ${e.message}")
            NetworkResult.Error(e, "获取请求日志列表失败: ${e.message}")
        }
    }

    /**
     * 根据ID获取请求日志详情
     * @param id 日志ID
     * @param token 认证令牌
     * @return 请求日志详情
     */
    suspend fun getRequestLogById(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取请求日志详情 - ID: $id")
        
        return try {
            val result = getWithToken(
                endpoint = "/request-logs/$id",
                token = token
            )
            
            when (result) {
                is NetworkResult.Success -> {
                    Logger.d(TAG, "请求日志详情获取成功")
                    result
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "请求日志详情获取失败: ${result.message}")
                    result
                }
                else -> result
            }
        } catch (e: Exception) {
            Logger.e(TAG, "请求日志详情获取异常: ${e.message}")
            NetworkResult.Error(e, "获取请求日志详情失败: ${e.message}")
        }
    }

    /**
     * 清理指定时间之前的请求日志
     * @param beforeDate 清理此日期之前的日志
     * @param token 认证令牌
     * @return 清理结果
     */
    suspend fun cleanRequestLogs(
        beforeDate: String,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "清理请求日志 - 清理日期之前: $beforeDate")
        
        return try {
            val result = deleteWithToken(
                endpoint = "/request-logs/clean",
                token = token,
                parameters = mapOf("beforeDate" to beforeDate)
            )
            
            when (result) {
                is NetworkResult.Success -> {
                    Logger.d(TAG, "请求日志清理成功")
                    result
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "请求日志清理失败: ${result.message}")
                    result
                }
                else -> result
            }
        } catch (e: Exception) {
            Logger.e(TAG, "请求日志清理异常: ${e.message}")
            NetworkResult.Error(e, "清理请求日志失败: ${e.message}")
        }
    }
}