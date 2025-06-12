# 跨平台Token存储功能

本模块提供了完整的跨平台Token存储解决方案，支持Android、iOS、Desktop和Web平台。

## 功能特性

- 🔐 **安全存储**: 各平台使用最佳实践进行数据存储
- 🌐 **跨平台支持**: 统一API，支持所有KMP目标平台
- 🚀 **异步操作**: 所有存储操作都是异步的，不会阻塞UI线程
- 🔄 **自动同步**: 数据变更会自动持久化到本地存储
- 🧩 **依赖注入**: 完全集成Koin依赖注入框架

## 平台实现

### Android
- 使用 `SharedPreferences` 进行数据存储
- 数据存储在应用私有目录，安全可靠
- 支持多进程访问

### iOS
- 使用 `NSUserDefaults` 进行数据存储
- 遵循iOS数据存储最佳实践
- 自动同步到iCloud（如果启用）

### Desktop
- 使用 `Properties` 文件进行数据存储
- 存储在用户主目录的 `.bsp` 文件夹中
- 跨操作系统兼容（Windows、macOS、Linux）

### Web
- 使用浏览器的 `localStorage` API
- 数据持久化存储在浏览器中
- 支持所有现代浏览器

## 使用方法

### 1. 基本使用

```kotlin
// 注入TokenStorage
class MyRepository(private val tokenStorage: TokenStorage) {
    
    // 保存登录信息
    suspend fun saveLoginInfo(token: String, userId: String) {
        tokenStorage.saveAccessToken(token)
        tokenStorage.saveUserId(userId)
    }
    
    // 获取访问令牌
    suspend fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }
    
    // 检查登录状态
    suspend fun isLoggedIn(): Boolean {
        return tokenStorage.hasValidToken()
    }
    
    // 登出
    suspend fun logout() {
        tokenStorage.clearTokens()
    }
}
```

### 2. 在ViewModel中使用

```kotlin
class AuthViewModel(private val tokenStorage: TokenStorage) : ViewModel() {
    
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoggedIn.value = tokenStorage.hasValidToken()
        }
    }
    
    fun login(token: String) {
        viewModelScope.launch {
            tokenStorage.saveAccessToken(token)
            _isLoggedIn.value = true
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            tokenStorage.clearTokens()
            _isLoggedIn.value = false
        }
    }
}
```

### 3. 在Compose UI中使用

```kotlin
@Composable
fun LoginScreen(authViewModel: AuthViewModel = koinViewModel()) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    if (isLoggedIn) {
        Text("已登录")
        Button(onClick = { authViewModel.logout() }) {
            Text("登出")
        }
    } else {
        Button(onClick = { 
            authViewModel.login("your_access_token") 
        }) {
            Text("登录")
        }
    }
}
```

## API 参考

### TokenStorage 接口

```kotlin
interface TokenStorage {
    suspend fun saveAccessToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun saveRefreshToken(refreshToken: String)
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
    suspend fun hasValidToken(): Boolean
    suspend fun saveUserId(userId: String)
    suspend fun getUserId(): String?
    suspend fun saveUserInfo(userInfo: String)
    suspend fun getUserInfo(): String?
}
```

### LocalStorage 接口

```kotlin
expect class LocalStorage {
    suspend fun saveString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun saveBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    suspend fun saveInt(key: String, value: Int)
    suspend fun getInt(key: String, defaultValue: Int = 0): Int
    suspend fun saveLong(key: String, value: Long)
    suspend fun getLong(key: String, defaultValue: Long = 0L): Long
    suspend fun remove(key: String)
    suspend fun clear()
    suspend fun contains(key: String): Boolean
}
```

## 依赖注入配置

存储模块已经集成到应用的依赖注入配置中：

```kotlin
// 在 AppModule.kt 中
val appModule = module {
    includes(storageModule)
    // ... 其他模块
}

// 在 StorageModule.kt 中
val storageModule = module {
    single<LocalStorage> { createLocalStorage() }
    single<TokenStorage> { TokenStorageImpl(get()) }
}
```

## 注意事项

1. **线程安全**: 所有存储操作都是线程安全的
2. **异常处理**: 建议在使用时添加适当的异常处理
3. **数据加密**: 如需要额外的安全性，可以在存储前对敏感数据进行加密
4. **存储限制**: Web平台的localStorage有大小限制（通常5-10MB）
5. **数据迁移**: 升级应用时注意数据格式的兼容性

## 扩展功能

可以根据需要扩展以下功能：

- Token自动刷新机制
- 数据加密/解密
- 数据备份和恢复
- 多用户支持
- 数据同步到云端

## 测试

建议为存储功能编写单元测试：

```kotlin
class TokenStorageTest {
    
    @Test
    fun testSaveAndGetToken() = runTest {
        val localStorage = MockLocalStorage()
        val tokenStorage = TokenStorageImpl(localStorage)
        
        val token = "test_token"
        tokenStorage.saveAccessToken(token)
        
        val retrievedToken = tokenStorage.getAccessToken()
        assertEquals(token, retrievedToken)
    }
}
```