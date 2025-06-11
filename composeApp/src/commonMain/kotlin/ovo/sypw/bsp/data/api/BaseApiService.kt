package ovo.sypw.bsp.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ovo.sypw.bsp.data.dto.ApiResponse
import ovo.sypw.bsp.data.dto.ErrorResponse
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.data.api.NetworkConfig
import ovo.sypw.bsp.data.api.HttpClientConfig

/**
 * 基础API服务类
 * 提供通用的网络请求方法
 */
abstract class BaseApiService {
    
    /**
     * HTTP客户端实例
     */
    protected val httpClient: HttpClient by lazy {
        HttpClientConfig.createHttpClient()
    }
    
    /**
     * 执行GET请求
     * @param endpoint API端点
     * @param parameters 请求参数
     * @return 网络请求结果
     */
    protected suspend inline fun <reified T> get(
        endpoint: String,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<T> {
        println(NetworkConfig.getApiUrl(endpoint))
        return safeApiCall {
            httpClient.get(NetworkConfig.getApiUrl(endpoint)) {
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }
    
    /**
     * 执行POST请求
     * @param endpoint API端点
     * @param body 请求体
     * @param parameters 请求参数
     * @return 网络请求结果
     */
    protected suspend inline fun <reified T> post(
        endpoint: String,
        body: Any? = null,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<T> {
        return safeApiCall {
            httpClient.post(NetworkConfig.getApiUrl(endpoint)) {
                contentType(ContentType.Application.Json)
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
                body?.let { setBody(it) }
            }
        }
    }
    
    /**
     * 执行PUT请求
     * @param endpoint API端点
     * @param body 请求体
     * @param parameters 请求参数
     * @return 网络请求结果
     */
    protected suspend inline fun <reified T> put(
        endpoint: String,
        body: Any? = null,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<T> {
        return safeApiCall {
            httpClient.put(NetworkConfig.getApiUrl(endpoint)) {
                contentType(ContentType.Application.Json)
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
                body?.let { setBody(it) }
            }
        }
    }
    
    /**
     * 执行DELETE请求
     * @param endpoint API端点
     * @param parameters 请求参数
     * @return 网络请求结果
     */
    protected suspend inline fun <reified T> delete(
        endpoint: String,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<T> {
        return safeApiCall {
            httpClient.delete(NetworkConfig.getApiUrl(endpoint)) {
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }
    
    /**
     * 安全的API调用，处理异常和错误
     * @param apiCall API调用函数
     * @return 网络请求结果
     */
    protected suspend inline fun <reified T> safeApiCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): NetworkResult<T> {
        return try {
            val response = apiCall()
            
            when (response.status) {
                HttpStatusCode.OK -> {
                    val apiResponse = response.body<ApiResponse<T>>()
                    if (apiResponse.success && apiResponse.data != null) {
                        NetworkResult.Success(apiResponse.data)
                    } else {
                        NetworkResult.Error(
                            Exception(apiResponse.message),
                            apiResponse.message
                        )
                    }
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error(
                        Exception("Unauthorized"),
                        "认证失败，请重新登录"
                    )
                }
                HttpStatusCode.Forbidden -> {
                    NetworkResult.Error(
                        Exception("Forbidden"),
                        "权限不足"
                    )
                }
                HttpStatusCode.NotFound -> {
                    NetworkResult.Error(
                        Exception("Not Found"),
                        "请求的资源不存在"
                    )
                }
                HttpStatusCode.InternalServerError -> {
                    NetworkResult.Error(
                        Exception("Internal Server Error"),
                        "服务器内部错误"
                    )
                }
                else -> {
                    val errorResponse = try {
                        response.body<ErrorResponse>()
                    } catch (e: Exception) {
                        ErrorResponse(
                            errorCode = response.status.value.toString(),
                            errorMessage = response.status.description
                        )
                    }
                    NetworkResult.Error(
                        Exception(errorResponse.errorMessage),
                        errorResponse.errorMessage
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                e,
                e.message ?: "网络请求失败"
            )
        }
    }
    
    /**
     * 关闭HTTP客户端
     */
    fun close() {
        httpClient.close()
    }
}