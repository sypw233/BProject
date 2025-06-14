package ovo.sypw.bsp.data.api

/**
 * 网络配置类
 * 定义API基础配置信息
 * 支持平台特定实现
 */
object NetworkConfig {
    
    /**
     * API基础URL
     */
    const val BASE_URL = "https://0dab-240e-37a-209b-6900-6cec-d160-56c-ad45.ngrok-free.app"
    
    /**
     * 连接超时时间（毫秒）
     */
    const val CONNECT_TIMEOUT = 30_000L
    
    /**
     * 请求超时时间（毫秒）
     */
    const val REQUEST_TIMEOUT = 30_000L
    
    /**
     * Socket超时时间（毫秒）
     */
    const val SOCKET_TIMEOUT = 30_000L
        
    
    /**
     * 内容类型
     */
    const val CONTENT_TYPE = "application/json"
    
    /**
     * 用户代理
     */
    const val USER_AGENT = "KMP-App/1.0"
    
    /**
     * 获取完整的API URL
     * @param endpoint 端点路径
     * @return 完整的API URL
     */
    fun getApiUrl(endpoint: String): String {
        return "$BASE_URL$endpoint"
    }
}