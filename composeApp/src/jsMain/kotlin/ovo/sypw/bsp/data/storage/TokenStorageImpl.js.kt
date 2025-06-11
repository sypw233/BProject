package ovo.sypw.bsp.data.storage

import kotlinx.browser.localStorage

/**
 * Web平台的Token存储实现
 * 使用localStorage存储token
 */
actual class TokenStorageImpl : TokenStorage {
    
    companion object {
        private const val KEY_TOKEN = "access_token"
    }
    
    /**
     * 保存访问令牌
     * @param token 访问令牌
     */
    override suspend fun saveToken(token: String) {
        localStorage.setItem(KEY_TOKEN, token)
    }
    
    /**
     * 获取访问令牌
     * @return 访问令牌，如果不存在则返回null
     */
    override suspend fun getToken(): String? {
        return localStorage.getItem(KEY_TOKEN)
    }
    
    /**
     * 清除访问令牌
     */
    override suspend fun clearToken() {
        localStorage.removeItem(KEY_TOKEN)
    }
    
    /**
     * 检查是否已登录（是否存在有效token）
     * @return 是否已登录
     */
    override suspend fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}