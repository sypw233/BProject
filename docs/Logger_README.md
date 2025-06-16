# KMP 跨平台日志工具使用指南

## 概述

本项目提供了一个完整的跨平台日志解决方案，支持 Android、iOS、Desktop 和 Web 平台。通过 Kotlin Multiplatform 的 `expect/actual` 机制，实现了统一的日志接口和平台特定的实现。

## 功能特性

- ✅ **跨平台支持**: Android、iOS、Desktop、Web
- ✅ **多日志级别**: DEBUG、INFO、WARN、ERROR
- ✅ **异常处理**: 支持异常信息的完整记录
- ✅ **扩展函数**: 为任何对象提供便捷的日志方法
- ✅ **平台优化**: 每个平台使用最适合的日志输出方式
- ✅ **时间戳**: Desktop 平台自动添加时间戳
- ✅ **格式化输出**: 统一的日志格式，便于调试

## 平台实现

### Android 平台
- 使用 `android.util.Log`
- 支持 Logcat 查看
- 自动处理异常堆栈跟踪

### iOS 平台
- 使用 `NSLog`
- 在 Xcode Console 中查看
- 包含完整的异常信息

### Desktop 平台
- 使用 `println` 和 `System.err.println`
- 自动添加时间戳
- 错误日志输出到 stderr

### Web 平台
- 使用浏览器 `console` API
- 根据日志级别使用不同的 console 方法
- 在浏览器开发者工具中查看

## 基本使用

### 1. 导入日志工具

```kotlin
import ovo.sypw.bsp.utils.Logger
```

### 2. 基本日志方法

```kotlin
// 带标签的日志
Logger.d("MyTag", "这是一个调试日志")
Logger.i("MyTag", "这是一个信息日志")
Logger.w("MyTag", "这是一个警告日志")
Logger.e("MyTag", "这是一个错误日志")

// 简化的日志（使用默认标签"KMP"）
Logger.d("简化的调试日志")
Logger.i("简化的信息日志")
Logger.w("简化的警告日志")
Logger.e("简化的错误日志")
```

### 3. 异常日志

```kotlin
try {
    // 可能抛出异常的代码
    throw Exception("测试异常")
} catch (e: Exception) {
    Logger.e("ExceptionTag", "捕获到异常", e)
}
```

### 4. 对象打印

```kotlin
val data = mapOf("key1" to "value1", "key2" to "value2")
Logger.printObject("DataTag", data)
Logger.printObject(data) // 使用默认标签
```

## 扩展函数使用（推荐）

为任何类添加日志功能：

```kotlin
class MyClass {
    fun doSomething() {
        this.logd("开始执行操作")
        this.logi("操作进行中")
        this.logw("发现潜在问题")
        this.loge("操作失败")
    }
}
```

## 在不同场景中的使用

### ViewModel 中使用

```kotlin
class AuthViewModel : ViewModel() {
    
    fun login(username: String, password: String) {
        this.logi("开始登录流程")
        this.logd("用户名: $username")
        
        viewModelScope.launch {
            try {
                val result = authRepository.login(username, password)
                this@AuthViewModel.logi("登录成功")
            } catch (e: Exception) {
                this@AuthViewModel.loge("登录失败", e)
            }
        }
    }
}
```

### Repository 中使用

```kotlin
class AuthRepositoryImpl : AuthRepository {
    
    override suspend fun login(username: String, password: String): NetworkResult<LoginResponse> {
        Logger.i("AuthRepository", "开始登录请求")
        
        return when (val result = authApiService.login(loginRequest)) {
            is NetworkResult.Success -> {
                Logger.i("AuthRepository", "登录请求成功: ${result.data.message}")
                // 处理成功逻辑
            }
            is NetworkResult.Error -> {
                Logger.e("AuthRepository", "登录请求失败", result.exception)
                result
            }
        }
    }
}
```

### API Service 中使用

```kotlin
class BaseApiService {
    
    suspend inline fun <reified T> get(
        endpoint: String,
        parameters: Map<String, Any> = emptyMap()
    ): NetworkResult<T> {
        Logger.d("BaseApiService", "API请求URL: ${NetworkConfig.getApiUrl(endpoint)}")
        
        return safeApiCall {
            // API 调用逻辑
        }
    }
}
```

### 数据存储中使用

```kotlin
actual class LocalStorage {
    
    actual suspend fun saveString(key: String, value: String) {
        try {
            // 保存逻辑
            Logger.d("LocalStorage", "保存字符串成功: $key")
        } catch (e: Exception) {
            Logger.e("LocalStorage", "保存字符串失败: $key", e)
        }
    }
}
```

## 日志级别说明

| 级别 | 用途 | 示例场景 |
|------|------|----------|
| DEBUG | 调试信息 | 变量值、方法调用、详细流程 |
| INFO | 一般信息 | 操作成功、状态变更、重要事件 |
| WARN | 警告信息 | 潜在问题、降级处理、配置问题 |
| ERROR | 错误信息 | 异常捕获、操作失败、系统错误 |

## 最佳实践

### 1. 使用合适的日志级别

```kotlin
// ✅ 正确使用
Logger.d("Network", "请求参数: $params")  // 调试信息
Logger.i("Auth", "用户登录成功")           // 重要事件
Logger.w("Cache", "缓存即将过期")          // 警告信息
Logger.e("API", "网络请求失败", exception) // 错误信息

// ❌ 错误使用
Logger.e("Debug", "这只是调试信息")        // 不应该用ERROR记录调试信息
Logger.d("Error", "发生了严重错误")        // 不应该用DEBUG记录错误
```

### 2. 使用有意义的标签

```kotlin
// ✅ 好的标签
Logger.i("AuthViewModel", "登录成功")
Logger.e("NetworkRepository", "API调用失败")
Logger.d("DatabaseHelper", "查询用户数据")

// ❌ 不好的标签
Logger.i("Test", "登录成功")
Logger.e("Error", "API调用失败")
Logger.d("Debug", "查询用户数据")
```

### 3. 敏感信息处理

```kotlin
// ✅ 正确处理敏感信息
Logger.d("Auth", "用户名: $username")
Logger.d("Auth", "密码: ***")  // 密码脱敏

// ❌ 不要记录敏感信息
Logger.d("Auth", "密码: $password")  // 不要记录明文密码
Logger.d("API", "Token: $authToken") // 不要记录完整token
```

### 4. 使用扩展函数

```kotlin
// ✅ 推荐使用扩展函数
class MyClass {
    fun doSomething() {
        this.logi("开始操作")  // 自动使用类名作为标签
    }
}

// ✅ 也可以手动指定标签
Logger.i("MyClass", "开始操作")
```

### 5. 异常处理

```kotlin
// ✅ 正确的异常日志
try {
    riskyOperation()
} catch (e: NetworkException) {
    Logger.e("Network", "网络操作失败", e)
} catch (e: Exception) {
    Logger.e("Unknown", "未知错误", e)
}

// ❌ 不要忽略异常
try {
    riskyOperation()
} catch (e: Exception) {
    Logger.e("Error", "操作失败")  // 缺少异常信息
}
```

## 性能考虑

1. **字符串拼接**: 避免在日志中进行复杂的字符串拼接
2. **对象序列化**: 大对象的toString()可能影响性能
3. **日志频率**: 避免在循环中频繁记录日志
4. **生产环境**: 考虑在生产环境中禁用DEBUG级别日志

## 故障排查

### 常见问题

1. **日志不显示**
   - 检查平台特定的日志查看工具
   - 确认日志级别设置

2. **编译错误**
   - 确保所有平台都有对应的actual实现
   - 检查import语句

3. **性能问题**
   - 减少日志输出频率
   - 避免记录大对象

### 调试技巧

1. 使用不同的标签区分不同模块
2. 在关键路径添加日志跟踪
3. 使用异常日志快速定位问题
4. 结合平台特定的调试工具

## 总结

这个跨平台日志工具为KMP项目提供了统一、高效的日志解决方案。通过合理使用不同的日志级别和标签，可以大大提高开发和调试效率。记住要遵循最佳实践，避免记录敏感信息，并根据实际需求选择合适的日志级别。