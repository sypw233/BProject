package ovo.sypw.bsp.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.content
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.utils.Logger

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
    protected val aiHttpClient: HttpClient by lazy {
        HttpClientConfig.createAIHttpClient()
    }

    /**
     * 执行GET请求
     * @param endpoint API端点
     * @param parameters 请求参数
     * @return 网络请求结果
     */
    protected suspend fun get(
        endpoint: String,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<SaResult> {
//        Logger.d("BaseApiService", "API请求URL: ${NetworkConfig.getApiUrl(endpoint)}")
        return safeApiCall {
            httpClient.get(NetworkConfig.getApiUrl(endpoint)) {
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }

    /**
     * 执行带Token的GET请求
     * @param endpoint API端点
     * @param token 认证令牌
     * @param parameters 请求参数
     * @return 网络请求结果
     */
    protected suspend fun getWithToken(
        endpoint: String,
        token: String,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<SaResult> {
        Logger.d("BaseApiService", "API请求URL: ${NetworkConfig.getApiUrl(endpoint)}")
//        Logger.d("Token::::$token")
        return safeApiCall {
            httpClient.get(NetworkConfig.getApiUrl(endpoint)) {
                header(HttpHeaders.Authorization, "Bearer $token")
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }

    /**
     * 执行带Token的文件下载GET请求
     * @param endpoint API端点
     * @param token 认证令牌
     * @param parameters 请求参数
     * @return 文件数据结果
     */
    protected suspend fun getFileWithToken(
        endpoint: String,
        token: String,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<ByteArray> {
        Logger.d("BaseApiService", "文件下载请求URL: ${NetworkConfig.getApiUrl(endpoint)}")
        return safeFileApiCall {
            httpClient.get(NetworkConfig.getApiUrl(endpoint)) {
                header(HttpHeaders.Authorization, "Bearer $token")
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
    protected suspend fun post(
        endpoint: String,
        body: Any? = null,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<SaResult> {
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
     * 发送POST请求并流式接收AI返回的纯文本
     * 参考TypeScript fetch API的流式响应处理方式
     * @param endpoint API端点
     * @param token 认证令牌
     * @param body 请求体（如AI提示词）
     * @param parameters 请求参数
     * @return Flow<String> 每次emit一个文本片段（类似TypeScript的decoder.decode）
     */
    @OptIn(InternalAPI::class)
    protected fun postWithTokenStreaming(
        endpoint: String,
        token: String,
        body: Any? = null,
        parameters: Map<String, Any> = emptyMap()
    ): Flow<String> = flow {
        Logger.d("BaseApiService", "开始流式请求: ${NetworkConfig.getApiUrl(endpoint)}")

        aiHttpClient.prepareRequest {
            url(NetworkConfig.getApiUrl(endpoint))
            method = HttpMethod.Post
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.Accept, "text/plain, text/event-stream, */*")
            header(HttpHeaders.AcceptEncoding, "identity") // 禁用压缩
            header(HttpHeaders.CacheControl, "no-cache")
            header(HttpHeaders.Connection, "keep-alive")
            contentType(ContentType.Application.Json)

            parameters.forEach { (key, value) -> parameter(key, value) }
            body?.let { setBody(it) }
        }.execute { response ->
            Logger.d("BaseApiService", "响应状态: ${response.status}")

            if (response.status != HttpStatusCode.OK) {
                throw IOException("HTTP错误: ${response.status}")
            }

            val channel = response.bodyAsChannel()
            val buffer = ByteArray(1024)

            Logger.d("BaseApiService", "开始读取流数据")

            while (!channel.isClosedForRead) {
                val bytesRead = channel.readAvailable(buffer, 0, buffer.size)
                if (bytesRead > 0) {
                    val chunk = buffer.decodeToString(0, bytesRead)
                    if (chunk.isNotEmpty()) {
                        Logger.d("BaseApiService", "接收到数据块: ${chunk.length} 字符")
                        emit(chunk)
                    }
                } else if (bytesRead == -1) {
                    Logger.d("BaseApiService", "流数据读取完成")
                    break
                }
            }
        }
    }.catch { e ->
        Logger.e("BaseApiService", "流式响应异常: ${e.message}")
        throw IOException("AI流中断: ${e.message}")
    }


    /**
     * 执行带Token的POST请求
     * @param endpoint API端点
     * @param token 认证令牌
     * @param body 请求体
     * @param parameters 请求参数
     * @return 网络请求结果
     */
    protected suspend fun postWithToken(
        endpoint: String,
        token: String,
        body: Any? = null,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<SaResult> {
        return safeApiCall {
            httpClient.post(NetworkConfig.getApiUrl(endpoint)) {
                header(HttpHeaders.Authorization, "Bearer $token")
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
    protected suspend fun put(
        endpoint: String,
        body: Any? = null,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<SaResult> {
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
     * 执行带Token的PUT请求
     * @param endpoint API端点
     * @param token 认证令牌
     * @param body 请求体
     * @param parameters 请求参数
     * @return 网络请求结果
     */
    protected suspend fun putWithToken(
        endpoint: String,
        token: String,
        body: Any? = null,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<SaResult> {
        return safeApiCall {
            httpClient.put(NetworkConfig.getApiUrl(endpoint)) {
                header(HttpHeaders.Authorization, "Bearer $token")
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
    protected suspend fun delete(
        endpoint: String,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<SaResult> {
        return safeApiCall {
            httpClient.delete(NetworkConfig.getApiUrl(endpoint)) {
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }

    /**
     * 执行带Token的DELETE请求
     * @param endpoint API端点
     * @param token 认证令牌
     * @param parameters 请求参数
     * @return 网络请求结果
     */
    protected suspend fun deleteWithToken(
        endpoint: String,
        token: String,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<SaResult> {
        return safeApiCall {
            httpClient.delete(NetworkConfig.getApiUrl(endpoint)) {
                header(HttpHeaders.Authorization, "Bearer $token")
                parameters.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }
    }

    /**
     * 安全的文件下载API调用方法
     * 统一处理文件下载请求异常和响应解析
     * @param apiCall API调用函数
     * @return 文件数据结果
     */
    protected suspend inline fun safeFileApiCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): NetworkResult<ByteArray> {
        return try {
            val response = apiCall()

            when (response.status) {
                HttpStatusCode.OK -> {
                    try {
                        val fileData = response.body<ByteArray>()
                        Logger.d("BaseApiService", "文件下载成功，大小: ${fileData.size} bytes")
                        NetworkResult.Success(fileData)
                    } catch (e: Exception) {
                        Logger.e("BaseApiService", "文件数据读取失败: ${e.message}")
                        NetworkResult.Error(
                            e,
                            "文件数据读取失败: ${e.message}"
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
                    Logger.e("BaseApiService", "文件下载失败，状态码: ${response.status}")
                    NetworkResult.Error(
                        Exception("HTTP ${response.status}"),
                        "文件下载失败，状态码: ${response.status}"
                    )
                }
            }
        } catch (e: Exception) {
            Logger.e("BaseApiService", "文件下载网络请求异常: ${e.message}")
            NetworkResult.Error(e, "网络请求失败: ${e.message}")
        }
    }

    /**
     * 安全的API调用方法
     * 统一处理网络请求异常和响应解析
     * @param apiCall API调用函数
     * @return 网络请求结果
     */
    protected suspend inline fun safeApiCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): NetworkResult<SaResult> {
        return try {
            val response = apiCall()

            when (response.status) {
                HttpStatusCode.OK -> {
                    try {
                        // 先获取原始响应文本用于调试
//                        val responseText = response.bodyAsText()
//                        Logger.d("BaseApiService", "服务器原始响应: $responseText")

                        val saResult = response.body<SaResult>()
//                        Logger.d("BaseApiService", "解析后的SaResult: $saResult")
                        NetworkResult.Success(saResult)
                    } catch (e: Exception) {
                        Logger.e("BaseApiService", "响应解析失败: ${e.message}")
                        NetworkResult.Error(
                            e,
                            "响应格式解析失败: ${e.message}"
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
                    NetworkResult.Error(
                        Exception("HTTP ${response.status.value}"),
                        "请求失败: ${response.status.description}"
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