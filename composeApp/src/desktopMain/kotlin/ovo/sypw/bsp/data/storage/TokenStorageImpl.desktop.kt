package ovo.sypw.bsp.data.storage

import java.io.File
import java.util.Properties

/**
 * Desktop平台的Token存储实现
 * 使用Properties文件存储token
 */
actual class TokenStorageImpl : TokenStorage {
    
    companion object {
        private const val PREFS_FILE_NAME = "auth.properties"
        private const val KEY_TOKEN = "access_token"
    }
    
    private val prefsFile: File by lazy {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".bproject")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        File(appDir, PREFS_FILE_NAME)
    }
    
    private fun loadProperties(): Properties {
        val properties = Properties()
        if (prefsFile.exists()) {
            prefsFile.inputStream().use { input ->
                properties.load(input)
            }
        }
        return properties
    }
    
    private fun saveProperties(properties: Properties) {
        prefsFile.outputStream().use { output ->
            properties.store(output, "Auth preferences")
        }
    }
    
    /**
     * 保存访问令牌
     * @param token 访问令牌
     */
    override suspend fun saveToken(token: String) {
        val properties = loadProperties()
        properties.setProperty(KEY_TOKEN, token)
        saveProperties(properties)
    }
    
    /**
     * 获取访问令牌
     * @return 访问令牌，如果不存在则返回null
     */
    override suspend fun getToken(): String? {
        val properties = loadProperties()
        return properties.getProperty(KEY_TOKEN)
    }
    
    /**
     * 清除访问令牌
     */
    override suspend fun clearToken() {
        val properties = loadProperties()
        properties.remove(KEY_TOKEN)
        saveProperties(properties)
    }
    
    /**
     * 检查是否已登录（是否存在有效token）
     * @return 是否已登录
     */
    override suspend fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}