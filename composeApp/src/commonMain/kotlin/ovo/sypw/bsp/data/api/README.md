# 网络请求基础配置说明

本项目已完成网络请求的基础配置，包含完整的目录结构和API请求配置。

## 目录结构

```
src/commonMain/kotlin/ovo/sypw/bsp/
├── data/
│   ├── api/
│   │   ├── NetworkConfig.kt          # 网络配置常量
│   │   ├── HttpClientConfig.kt       # HTTP客户端配置
│   │   ├── BaseApiService.kt         # 基础API服务类
│   │   └── ExampleApiService.kt      # 示例API服务实现
│   ├── dto/
│   │   └── ApiResponse.kt            # API响应数据类
│   └── repository/
│       └── ExampleRepositoryImpl.kt  # 示例Repository实现
├── domain/
│   ├── model/
│   │   └── NetworkResult.kt          # 网络请求结果封装
│   └── repository/
│       ├── BaseRepository.kt         # 基础Repository接口
│       └── ExampleRepository.kt      # 示例Repository接口
└── di/
    ├── NetworkModule.kt              # 网络模块依赖注入
    └── AppModule.kt                  # 应用主模块
```

## 核心组件说明

### 1. NetworkConfig.kt
- 定义API基础URL、超时时间等配置
- 提供生成完整API URL的工具方法

### 2. HttpClientConfig.kt
- 配置Ktor HTTP客户端
- 包含内容协商、日志、超时等设置
- 提供普通和调试模式的客户端

### 3. BaseApiService.kt
- 提供通用的HTTP请求方法（GET、POST、PUT、DELETE）
- 统一的错误处理和响应解析
- 自动处理常见HTTP状态码

### 4. NetworkResult.kt
- 封装网络请求结果的密封类
- 包含Success、Error、Loading、Idle四种状态
- 提供便捷的状态判断和数据处理方法

### 5. BaseRepository.kt
- 提供通用的数据访问方法
- 支持带缓存的网络请求
- 返回Flow<NetworkResult<T>>格式的响应式数据流

## 使用示例

### 1. 创建API服务

```kotlin
class UserApiService : BaseApiService() {
    suspend fun getUser(id: String): NetworkResult<User> {
        return get(endpoint = "users/$id")
    }
    
    suspend fun createUser(user: CreateUserRequest): NetworkResult<User> {
        return post(endpoint = "users", body = user)
    }
}
```

### 2. 创建Repository

```kotlin
class UserRepositoryImpl(
    private val apiService: UserApiService
) : UserRepository, BaseRepository {
    
    override fun getUser(id: String): Flow<NetworkResult<User>> {
        return performNetworkCall {
            apiService.getUser(id)
        }
    }
}
```

### 3. 在ViewModel中使用

```kotlin
class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _userState = MutableStateFlow<NetworkResult<User>>(NetworkResult.Idle)
    val userState = _userState.asStateFlow()
    
    fun loadUser(id: String) {
        viewModelScope.launch {
            userRepository.getUser(id).collect { result ->
                _userState.value = result
            }
        }
    }
}
```

### 4. 在Compose UI中使用

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel) {
    val userState by viewModel.userState.collectAsState()
    
    when (userState) {
        is NetworkResult.Loading -> {
            CircularProgressIndicator()
        }
        is NetworkResult.Success -> {
            UserContent(user = userState.data)
        }
        is NetworkResult.Error -> {
            ErrorMessage(message = userState.message)
        }
        is NetworkResult.Idle -> {
            // 初始状态
        }
    }
}
```

## 依赖注入配置

项目使用Koin进行依赖注入，网络相关的依赖已在`NetworkModule.kt`中配置：

```kotlin
val networkModule = module {
    single<HttpClient> { HttpClientConfig.createHttpClient() }
    single<ExampleApiService> { ExampleApiService() }
    single<ExampleRepository> { ExampleRepositoryImpl(get()) }
}
```

## 配置说明

### 网络配置
- 基础URL：可在`NetworkConfig.kt`中修改
- 超时时间：连接超时30秒，请求超时60秒
- 内容类型：默认使用JSON格式

### 错误处理
- 自动处理常见HTTP状态码
- 统一的错误消息格式
- 支持自定义错误处理逻辑

### 日志配置
- 开发模式下启用详细日志
- 生产模式下关闭敏感信息日志

## 扩展指南

1. **添加新的API服务**：继承`BaseApiService`类
2. **添加新的Repository**：实现对应的Repository接口并继承`BaseRepository`
3. **修改网络配置**：在`NetworkConfig.kt`中调整相关参数
4. **添加认证**：在`HttpClientConfig.kt`中添加认证拦截器
5. **添加缓存**：在Repository中使用`performNetworkCallWithCache`方法

## 注意事项

1. 所有网络请求都应该通过Repository层进行
2. 使用Flow进行响应式编程，便于UI状态管理
3. 合理使用缓存策略，提升用户体验
4. 在生产环境中关闭调试日志
5. 根据实际API接口调整数据模型和错误处理逻辑