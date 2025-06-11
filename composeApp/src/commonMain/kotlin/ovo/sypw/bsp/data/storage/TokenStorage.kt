package ovo.sypw.bsp.data.storage

/**
 * Token存储接口
 * 定义跨平台的token存储操作
 */
interface TokenStorage {
    
    /**
     * 保存访问令牌
     * @param token 访问令牌
     */
    suspend fun saveToken(token: String)
    
    /**
     * 获取访问令牌
     * @return 访问令牌，如果不存在则返回null
     */
    suspend fun getToken(): String?
    
    /**
     * 清除访问令牌
     */
    suspend fun clearToken()
    
    /**
     * 检查是否已登录（是否存在有效token）
     * @return 是否已登录
     */
    suspend fun isLoggedIn(): Boolean
}

/**
 * expect/actual机制的Token存储实现
 * 各平台提供具体的构造函数实现
 */
expect class TokenStorageImpl : TokenStorage