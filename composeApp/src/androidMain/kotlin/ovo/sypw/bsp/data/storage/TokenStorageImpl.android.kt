package ovo.sypw.bsp.data.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Android平台的Token存储实现
 * 使用SharedPreferences存储token
 */
actual class TokenStorageImpl(private val context: Context) : TokenStorage {
    
    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_TOKEN = "access_token"
    }
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * 保存访问令牌
     * @param token 访问令牌
     */
    override suspend fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }
    
    /**
     * 获取访问令牌
     * @return 访问令牌，如果不存在则返回null
     */
    override suspend fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
    
    /**
     * 清除访问令牌
     */
    override suspend fun clearToken() {
        sharedPreferences.edit()
            .remove(KEY_TOKEN)
            .apply()
    }
    
    /**
     * 检查是否已登录（是否存在有效token）
     * @return 是否已登录
     */
    override suspend fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}