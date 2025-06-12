package ovo.sypw.bsp.data.storage

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

/**
 * Web平台的LocalStorage实现
 * 使用浏览器的localStorage API进行数据存储
 */
actual class LocalStorage {
    
    companion object {
        private const val PREFIX = "bsp_"
    }
    
    /**
     * 获取带前缀的键名
     * @param key 原始键名
     * @return 带前缀的键名
     */
    private fun getPrefixedKey(key: String): String {
        return PREFIX + key
    }
    
    /**
     * 保存字符串数据
     * @param key 键
     * @param value 值
     */
    actual suspend fun saveString(key: String, value: String) {
        try {
            localStorage[getPrefixedKey(key)] = value
        } catch (e: Exception) {
            console.error("Failed to save string to localStorage", e)
        }
    }
    
    /**
     * 获取字符串数据
     * @param key 键
     * @return 值，如果不存在则返回null
     */
    actual suspend fun getString(key: String): String? {
        return try {
            localStorage[getPrefixedKey(key)]
        } catch (e: Exception) {
            console.error("Failed to get string from localStorage", e)
            null
        }
    }
    
    /**
     * 保存布尔值数据
     * @param key 键
     * @param value 值
     */
    actual suspend fun saveBoolean(key: String, value: Boolean) {
        try {
            localStorage[getPrefixedKey(key)] = value.toString()
        } catch (e: Exception) {
            console.error("Failed to save boolean to localStorage", e)
        }
    }
    
    /**
     * 获取布尔值数据
     * @param key 键
     * @param defaultValue 默认值
     * @return 值，如果不存在则返回默认值
     */
    actual suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return try {
            val value = localStorage[getPrefixedKey(key)]
            value?.toBooleanStrictOrNull() ?: defaultValue
        } catch (e: Exception) {
            console.error("Failed to get boolean from localStorage", e)
            defaultValue
        }
    }
    
    /**
     * 保存整数数据
     * @param key 键
     * @param value 值
     */
    actual suspend fun saveInt(key: String, value: Int) {
        try {
            localStorage[getPrefixedKey(key)] = value.toString()
        } catch (e: Exception) {
            console.error("Failed to save int to localStorage", e)
        }
    }
    
    /**
     * 获取整数数据
     * @param key 键
     * @param defaultValue 默认值
     * @return 值，如果不存在则返回默认值
     */
    actual suspend fun getInt(key: String, defaultValue: Int): Int {
        return try {
            val value = localStorage[getPrefixedKey(key)]
            value?.toIntOrNull() ?: defaultValue
        } catch (e: Exception) {
            console.error("Failed to get int from localStorage", e)
            defaultValue
        }
    }
    
    /**
     * 保存长整数数据
     * @param key 键
     * @param value 值
     */
    actual suspend fun saveLong(key: String, value: Long) {
        try {
            localStorage[getPrefixedKey(key)] = value.toString()
        } catch (e: Exception) {
            console.error("Failed to save long to localStorage", e)
        }
    }
    
    /**
     * 获取长整数数据
     * @param key 键
     * @param defaultValue 默认值
     * @return 值，如果不存在则返回默认值
     */
    actual suspend fun getLong(key: String, defaultValue: Long): Long {
        return try {
            val value = localStorage[getPrefixedKey(key)]
            value?.toLongOrNull() ?: defaultValue
        } catch (e: Exception) {
            console.error("Failed to get long from localStorage", e)
            defaultValue
        }
    }
    
    /**
     * 删除指定键的数据
     * @param key 键
     */
    actual suspend fun remove(key: String) {
        try {
            localStorage.removeItem(getPrefixedKey(key))
        } catch (e: Exception) {
            console.error("Failed to remove item from localStorage", e)
        }
    }
    
    /**
     * 清除所有数据
     */
    actual suspend fun clear() {
        try {
            // 只清除带有我们前缀的项目
            val keysToRemove = mutableListOf<String>()
            for (i in 0 until localStorage.length) {
                val key = localStorage.key(i)
                if (key != null && key.startsWith(PREFIX)) {
                    keysToRemove.add(key)
                }
            }
            keysToRemove.forEach { key ->
                localStorage.removeItem(key)
            }
        } catch (e: Exception) {
            console.error("Failed to clear localStorage", e)
        }
    }
    
    /**
     * 检查是否包含指定键
     * @param key 键
     * @return 如果包含返回true，否则返回false
     */
    actual suspend fun contains(key: String): Boolean {
        return try {
            localStorage[getPrefixedKey(key)] != null
        } catch (e: Exception) {
            console.error("Failed to check key in localStorage", e)
            false
        }
    }
}