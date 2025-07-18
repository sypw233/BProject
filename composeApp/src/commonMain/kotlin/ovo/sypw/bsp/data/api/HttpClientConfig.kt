package ovo.sypw.bsp.data.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * HTTP客户端配置类
 * 提供统一的Ktor客户端配置
 */
object HttpClientConfig {

    /**
     * 创建配置好的HTTP客户端
     * @return 配置完成的HttpClient实例
     */
    fun createHttpClient(): HttpClient {
        return HttpClient {
            // 安装内容协商插件，用于JSON序列化
            install(ContentNegotiation) {
                json(Json {
                    // 忽略未知字段
                    ignoreUnknownKeys = true
                    // 允许结构化映射键
                    allowStructuredMapKeys = true
                    // 美化输出（仅调试时使用）
                    prettyPrint = true
                    // 使用默认值
                    useAlternativeNames = false
                })
            }

            // 安装日志插件
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
                filter { request ->
                    request.url.host.contains("api")
                }
            }

            // 安装超时插件
            install(HttpTimeout) {
                requestTimeoutMillis = NetworkConfig.REQUEST_TIMEOUT
                connectTimeoutMillis = NetworkConfig.CONNECT_TIMEOUT
                socketTimeoutMillis = NetworkConfig.SOCKET_TIMEOUT
            }

            // 安装默认请求插件
            install(DefaultRequest) {
                // 设置默认请求头
                header("Content-Type", NetworkConfig.CONTENT_TYPE)
                header("Accept", NetworkConfig.CONTENT_TYPE)
                header("User-Agent", NetworkConfig.USER_AGENT)

                // 设置基础URL
                url(NetworkConfig.BASE_URL)
            }

            // 安装HTTP重定向插件
            install(HttpRedirect) {
                checkHttpMethod = false
                allowHttpsDowngrade = false
            }
        }
    }

    /**
     * 创建专用于AI流式响应的HTTP客户端
     * 保留ContentNegotiation用于请求体序列化，但配置为不干扰响应流
     * @return 配置完成的HttpClient实例
     */
    fun createAIHttpClient(): HttpClient {
        return HttpClient {
            // 安装ContentNegotiation插件用于请求体序列化
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    // 不美化输出以减少干扰
                    prettyPrint = false
                })
            }

            // 安装日志插件
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
                filter { request ->
                    request.url.host.contains("api")
                }
            }

            // 设置expectSuccess为false，允许手动处理响应状态
            expectSuccess = false

            // 安装超时插件 - 流式响应需要无限超时
            install(HttpTimeout) {
                requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                connectTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            }

            // 安装默认请求插件
            install(DefaultRequest) {
                // 设置默认请求头
                header("Content-Type", NetworkConfig.CONTENT_TYPE)
                header("Accept", "text/plain, */*") // 支持流式文本响应
                header("User-Agent", NetworkConfig.USER_AGENT)
                header("Cache-Control", "no-cache") // 禁用缓存确保实时流式传输

                // 设置基础URL
                url(NetworkConfig.BASE_URL)
            }

            // 安装HTTP重定向插件
//            install(HttpRedirect) {
//                checkHttpMethod = false
//                allowHttpsDowngrade = false
//            }
        }
    }

    /**
     * 创建用于调试的HTTP客户端（包含详细日志）
     * @return 配置完成的HttpClient实例
     */
    fun createDebugHttpClient(): HttpClient {
        return HttpClient {
            // 安装内容协商插件
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    allowStructuredMapKeys = true
                    prettyPrint = true
                    useAlternativeNames = false
                })
            }

            // 安装详细日志插件
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            // 安装超时插件
            install(HttpTimeout) {
                requestTimeoutMillis = NetworkConfig.REQUEST_TIMEOUT
                connectTimeoutMillis = NetworkConfig.CONNECT_TIMEOUT
                socketTimeoutMillis = NetworkConfig.SOCKET_TIMEOUT
            }

            // 安装默认请求插件
            install(DefaultRequest) {
                header("Content-Type", NetworkConfig.CONTENT_TYPE)
                header("Accept", NetworkConfig.CONTENT_TYPE)
                header("User-Agent", NetworkConfig.USER_AGENT)
                url(NetworkConfig.BASE_URL)
            }
        }
    }
}