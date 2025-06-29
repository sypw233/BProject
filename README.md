# BSP - 企业级管理系统

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-4285F4.svg?style=flat&logo=jetpackcompose)](https://github.com/JetBrains/compose-multiplatform)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-1.8.2-green.svg?style=flat&logo=jetpackcompose)](https://github.com/JetBrains/compose-multiplatform)

一个基于 **Kotlin Multiplatform** 和 **Compose Multiplatform** 构建的现代化企业级管理系统，支持 **Android**、**iOS**、**Desktop** 多平台部署。采用 Clean Architecture 架构模式，提供完整的企业级功能模块。

## ✨ 功能特性

### 🏢 核心业务模块
- **👥 员工管理** - 完整的员工信息管理系统
- **🎓 学生管理** - 学生信息录入、查询、统计
- **🏛️ 部门管理** - 组织架构管理
- **📚 班级管理** - 班级信息维护
- **📢 公告系统** - 富文本公告发布与管理
- **🤖 AI 聊天** - 智能对话助手
- **💾 网盘系统** - 文件上传下载管理
- **📊 数据统计** - 可视化数据分析
- **📝 操作日志** - 完整的用户行为追踪

### 🔧 技术特性
- **🌐 跨平台支持** - Android、iOS、Desktop 一套代码多端运行
- **🏗️ Clean Architecture** - 分层架构设计，职责分离
- **🔄 MVVM 模式** - 数据驱动的响应式编程
- **🎨 现代化 UI** - 基于 Material Design 3 的响应式界面
- **🔐 安全认证** - JWT Token 认证机制
- **📱 响应式设计** - 适配不同屏幕尺寸
- **🚀 高性能** - 基于 Kotlin Coroutines 的异步处理
- **📦 模块化架构** - Clean Architecture + MVVM 设计模式
- **🔍 智能搜索** - 多条件筛选和分页加载
- **📈 数据可视化** - 图表展示和统计分析

## 🏗️ 技术栈

### 核心框架
- **[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) 2.1.20** - 跨平台开发框架
- **[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) 1.8.2** - 声明式 UI 框架
- **[Ktor Client](https://ktor.io/docs/client.html) 3.1.3** - 跨平台网络请求库
- **[Koin](https://insert-koin.io/) 4.1.0** - 轻量级依赖注入框架

### UI 组件
- **Material Design 3** - 现代化设计语言
- **Material Icons Extended** - 扩展图标库
- **Compose Charts** - 数据可视化图表
- **Rich Text Editor** - 富文本编辑器 (RichEditor Compose)
- **Coil 3.2.0** - 图片加载库
- **Markdown Renderer** - Markdown 渲染支持

### 数据处理
- **Kotlinx Serialization** - JSON 序列化
- **Kotlinx Coroutines 1.10.2** - 异步编程
- **Kotlinx DateTime 0.6.2** - 时间处理
- **FileKit** - 跨平台文件操作

### 架构组件
- **expect/actual 机制** - 平台特定实现
- **Repository 模式** - 数据访问抽象
- **UseCase 模式** - 业务逻辑封装
- **KMP ViewModel** - 跨平台状态管理
- **Clean Architecture** - 分层架构设计

## 🚀 快速开始

### 环境要求

- **JDK 21** 或更高版本
- **Android Studio** 最新版本 (推荐 2024.2.1+)
- **Xcode 15+** (iOS 开发)
- **Kotlin 2.1.20**
- **Gradle 8.10+**

### 克隆项目

```bash
git clone https://github.com/yourusername/bProject.git
cd bProject
```

### 运行不同平台

#### Android
```bash
# 构建Debug版本
./gradlew :composeApp:assembleDebug

# 安装到设备
./gradlew :composeApp:installDebug
```

#### iOS
```bash
# 在iOS模拟器运行
./gradlew :composeApp:iosSimulatorArm64Test

# 构建iOS Framework
./gradlew :composeApp:iosArm64Archive
```

#### Desktop
```bash
# 运行桌面应用
./gradlew :composeApp:run

# 创建可分发包
./gradlew :composeApp:createDistributable

# 打包为平台特定格式
./gradlew :composeApp:packageDistributionForCurrentOS
```

### 开发环境配置

#### Android 开发
```bash
# 安装 Android SDK
# 配置 ANDROID_HOME 环境变量
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

#### iOS 开发
```bash
# 安装 Xcode Command Line Tools
xcode-select --install

# 安装 CocoaPods (如需要)
sudo gem install cocoapods
```


## 📁 项目结构

```
bProject/
├── composeApp/                 # 主应用模块
│   ├── src/
│   │   ├── commonMain/kotlin/ovo/sypw/bsp/  # 共享代码
│   │   │   ├── data/           # 数据层
│   │   │   │   ├── api/        # API 服务层
│   │   │   │   │   ├── *ApiService.kt      # 各模块API服务
│   │   │   │   │   ├── BaseApiService.kt   # 基础API服务
│   │   │   │   │   ├── HttpClientConfig.kt # HTTP客户端配置
│   │   │   │   │   └── NetworkConfig.kt    # 网络配置
│   │   │   │   ├── dto/        # 数据传输对象
│   │   │   │   │   ├── *Dto.kt             # 各模块DTO
│   │   │   │   │   └── result/             # 网络结果封装
│   │   │   │   ├── model/      # 数据模型
│   │   │   │   ├── paging/     # 分页数据源
│   │   │   │   └── storage/    # 本地存储
│   │   │   │       ├── LocalStorage.kt     # 存储抽象
│   │   │   │       └── TokenStorage*.kt    # Token存储
│   │   │   ├── domain/         # 业务逻辑层
│   │   │   │   ├── repository/ # 仓储接口
│   │   │   │   │   ├── *Repository.kt      # 各模块仓储接口
│   │   │   │   │   └── impl/               # 仓储实现
│   │   │   │   └── usecase/    # 用例层
│   │   │   │       └── *UseCase.kt         # 各模块用例
│   │   │   ├── presentation/   # 表现层
│   │   │   │   ├── components/ # UI 组件
│   │   │   │   │   ├── ChartComponents.kt  # 图表组件
│   │   │   │   │   ├── CommonComponents.kt # 通用组件
│   │   │   │   │   ├── dialog/             # 对话框组件
│   │   │   │   │   ├── search/             # 搜索组件
│   │   │   │   │   └── template/           # 模板组件
│   │   │   │   ├── navigation/ # 导航管理
│   │   │   │   ├── screens/    # 页面
│   │   │   │   │   ├── admin/              # 管理员页面
│   │   │   │   │   ├── aichat/             # AI聊天页面
│   │   │   │   │   ├── announcement/       # 公告页面
│   │   │   │   │   └── auth/               # 认证页面
│   │   │   │   └── viewmodel/  # 视图模型
│   │   │   │       ├── admin/              # 管理员ViewModel
│   │   │   │       └── *ViewModel.kt       # 各模块ViewModel
│   │   │   ├── di/             # 依赖注入
│   │   │   │   └── *Module.kt              # 各模块DI配置
│   │   │   ├── utils/          # 工具类
│   │   │   │   ├── file/                   # 文件操作工具
│   │   │   │   ├── Logger.kt               # 日志工具
│   │   │   │   ├── ResponsiveUtils.kt      # 响应式布局工具
│   │   │   │   └── *.kt                    # 其他工具类
│   │   │   ├── App.kt          # 应用入口
│   │   │   └── Platform.kt     # 平台抽象
│   │   ├── androidMain/        # Android 特定代码
│   │   ├── iosMain/           # iOS 特定代码
│   │   ├── desktopMain/       # Desktop 特定代码
│   │   └── commonTest/        # 共享测试代码
│   └── build.gradle.kts       # 构建配置
├── iosApp/                     # iOS 应用入口
│   ├── iosApp/
│   │   ├── ContentView.swift   # SwiftUI视图
│   │   └── iOSApp.swift        # iOS应用入口
│   └── iosApp.xcodeproj/       # Xcode项目文件
├── .github/workflows/          # CI/CD 配置
│   └── build-all-platforms.yml # 多平台构建流程
├── gradle/                     # Gradle配置
│   └── libs.versions.toml      # 版本目录
├── docs/                       # 项目文档
├── README.md                   # 项目说明
└── settings.gradle.kts         # 项目设置
```

## 🏛️ 架构设计

本项目采用 **Clean Architecture** 架构模式，结合 **MVVM** 设计模式：

### 分层架构

```
┌─────────────────────────────────────┐
│           Presentation Layer        │  ← UI 层 (Compose)
├─────────────────────────────────────┤
│           Domain Layer              │  ← 业务逻辑层
├─────────────────────────────────────┤
│           Data Layer                │  ← 数据层
├─────────────────────────────────────┤
│           Platform Layer            │  ← 平台特定实现
└─────────────────────────────────────┘
```

### 数据流

```
UI → ViewModel → UseCase → Repository → API/Storage
```

## 🔧 配置说明

### 网络配置

在 `NetworkConfig.kt` 中配置后端服务地址：

```kotlin
object NetworkConfig {
    const val BASE_URL = "https://your-api-server.com"
    const val TIMEOUT = 30000L
}
```

### 依赖注入

项目使用 Koin 进行依赖注入，模块化配置：

```kotlin
// 启动 Koin
startKoin {
    modules(
        networkModule,
        repositoryModule,
        useCaseModule,
        viewModelModule
    )
}
```

## 📱 平台特性

### Android
- 支持 Android 7.0+ (API 24+)
- Material Design 3 主题
- 自适应布局
- 后台任务处理

### iOS
- 支持 iOS 16.0+
- 原生性能
- SwiftUI 集成
- 系统主题适配

### Desktop
- Windows、macOS、Linux 支持
- 窗口管理
- 键盘快捷键
- 系统托盘集成

## 🔧 技术架构详解

### expect/actual 机制

项目使用 Kotlin Multiplatform 的 expect/actual 机制实现平台特定功能：

```kotlin
// commonMain - 共享声明
expect class PlatformContext
expected fun getPlatformName(): String
expected class LocalStorage {
    fun saveToken(token: String)
    fun getToken(): String?
}

// androidMain - Android 实现
actual class PlatformContext(val context: Context)
actual fun getPlatformName() = "Android"
actual class LocalStorage(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    actual fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }
    actual fun getToken(): String? = prefs.getString("token", null)
}

// iosMain - iOS 实现
actual class PlatformContext
actual fun getPlatformName() = "iOS"
actual class LocalStorage {
    actual fun saveToken(token: String) {
        NSUserDefaults.standardUserDefaults.setObject(token, "token")
    }
    actual fun getToken(): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey("token")
    }
}

// desktopMain - Desktop 实现
actual class PlatformContext
actual fun getPlatformName() = "Desktop"
actual class LocalStorage {
    private val prefs = Preferences.userNodeForPackage(LocalStorage::class.java)
    actual fun saveToken(token: String) {
        prefs.put("token", token)
    }
    actual fun getToken(): String? = prefs.get("token", null)
}
```

### 依赖注入架构

使用 Koin 实现模块化的依赖注入：

```kotlin
// NetworkModule.kt
val networkModule = module {
    single<HttpClient> {
        HttpClient(get<HttpClientEngine>()) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
    
    //// 平台特定的 HttpClientEngine
    single<HttpClientEngine> { 
        when (Platform.current) {
            is Platform.Android -> OkHttp.create()
            is Platform.iOS -> Darwin.create()
            is Platform.Desktop -> OkHttp.create()
        }
    }
}

// RepositoryModule.kt
val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<StudentRepository> { StudentRepositoryImpl(get()) }
    single<EmployeeRepository> { EmployeeRepositoryImpl(get()) }
}

// ViewModelModule.kt
val viewModelModule = module {
    factory { AuthViewModel(get(), get(), get()) }
    factory { StudentViewModel(get()) }
    factory { EmployeeViewModel(get()) }
}
```

### 网络层架构

基于 Ktor Client 的网络层设计：

```kotlin
// BaseApiService.kt
abstract class BaseApiService(protected val httpClient: HttpClient) {
    protected suspend inline fun <reified T> safeApiCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): NetworkResult<T> {
        return try {
            val response = apiCall()
            when (response.status) {
                HttpStatusCode.OK -> {
                    val data = response.body<T>()
                    NetworkResult.Success(data)
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("认证失败")
                }
                else -> {
                    NetworkResult.Error("网络请求失败: ${response.status}")
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "未知错误")
        }
    }
}

// AuthApiService.kt
class AuthApiService(httpClient: HttpClient) : BaseApiService(httpClient) {
    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return safeApiCall<LoginResponse> {
            httpClient.post("${NetworkConfig.BASE_URL}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
    }
}
```

### UI 架构设计

基于 Compose Multiplatform 的响应式 UI 架构：

```kotlin
// 状态管理
@Composable
fun StudentManagementScreen(
    viewModel: StudentViewModel = koinKmpViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val students by viewModel.students.collectAsLazyPagingItems()
    
    LaunchedEffect(Unit) {
        viewModel.loadStudents()
    }
    
    when (uiState) {
        is StudentUiState.Loading -> LoadingIndicator()
        is StudentUiState.Success -> {
            StudentList(
                students = students,
                onStudentClick = viewModel::selectStudent,
                onRefresh = viewModel::refresh
            )
        }
        is StudentUiState.Error -> {
            ErrorMessage(
                message = uiState.message,
                onRetry = viewModel::retry
            )
        }
    }
}

// ViewModel 实现
class StudentViewModel(
    private val studentUseCase: StudentUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<StudentUiState>(StudentUiState.Loading)
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()
    
    private val _students = MutableStateFlow<PagingData<Student>>(PagingData.empty())
    val students: StateFlow<PagingData<Student>> = _students.asStateFlow()
    
    fun loadStudents() {
        viewModelScope.launch {
            studentUseCase.getStudents()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _students.value = pagingData
                    _uiState.value = StudentUiState.Success
                }
        }
    }
}
```

### 响应式设计实现

```kotlin
// ResponsiveUtils.kt
@Composable
fun rememberWindowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    return remember(configuration) {
        WindowInfo(
            screenWidthInfo = when {
                configuration.screenWidthDp < 600 -> WindowInfo.WindowType.Compact
                configuration.screenWidthDp < 840 -> WindowInfo.WindowType.Medium
                else -> WindowInfo.WindowType.Expanded
            },
            screenHeightInfo = when {
                configuration.screenHeightDp < 480 -> WindowInfo.WindowType.Compact
                configuration.screenHeightDp < 900 -> WindowInfo.WindowType.Medium
                else -> WindowInfo.WindowType.Expanded
            }
        )
    }
}

// 自适应布局组件
@Composable
fun AdaptiveLayout(
    content: @Composable (WindowInfo) -> Unit
) {
    val windowInfo = rememberWindowInfo()
    content(windowInfo)
}
```

### 数据持久化策略

```kotlin
// 跨平台数据存储抽象
interface DataStore {
    suspend fun saveString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun saveBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String): Boolean
    suspend fun clear()
}

// Android 实现
actual class DataStoreImpl(private val context: Context) : DataStore {
    private val dataStore = context.createDataStore("app_preferences")
    
    actual suspend fun saveString(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
    
    actual suspend fun getString(key: String): String? {
        return dataStore.data.first()[stringPreferencesKey(key)]
    }
}

// iOS 实现
actual class DataStoreImpl : DataStore {
    actual suspend fun saveString(key: String, value: String) {
        NSUserDefaults.standardUserDefaults.setObject(value, key)
    }
    
    actual suspend fun getString(key: String): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey(key)
    }
}
```

### 错误处理机制

```kotlin
// 统一错误处理
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

// 扩展函数简化错误处理
suspend fun <T> NetworkResult<T>.onSuccess(
    action: suspend (T) -> Unit
): NetworkResult<T> {
    if (this is NetworkResult.Success) {
        action(data)
    }
    return this
}

suspend fun <T> NetworkResult<T>.onError(
    action: suspend (String) -> Unit
): NetworkResult<T> {
    if (this is NetworkResult.Error) {
        action(message)
    }
    return this
}
```

## 🧪 测试策略

### 单元测试

```kotlin
// commonTest
class StudentUseCaseTest {
    private val mockRepository = mockk<StudentRepository>()
    private val useCase = StudentUseCase(mockRepository)
    
    @Test
    fun `should return students when repository call succeeds`() = runTest {
        // Given
        val expectedStudents = listOf(
            Student(id = 1, name = "张三", age = 20),
            Student(id = 2, name = "李四", age = 21)
        )
        coEvery { mockRepository.getStudents() } returns flowOf(expectedStudents)
        
        // When
        val result = useCase.getStudents().first()
        
        // Then
        assertEquals(expectedStudents, result)
    }
}
```

### 集成测试

```kotlin
// 网络层集成测试
class ApiIntegrationTest {
    private lateinit var mockEngine: MockEngine
    private lateinit var apiService: StudentApiService
    
    @BeforeTest
    fun setup() {
        mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/api/students" -> {
                    respond(
                        content = """
                            {
                                "data": [
                                    {"id": 1, "name": "张三", "age": 20}
                                ],
                                "success": true
                            }
                        """.trimIndent(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> error("Unhandled ${request.url.encodedPath}")
            }
        }
        
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }
        
        apiService = StudentApiService(httpClient)
    }
    
    @Test
    fun `should fetch students successfully`() = runTest {
        val result = apiService.getStudents()
        assertTrue(result is NetworkResult.Success)
        assertEquals(1, (result as NetworkResult.Success).data.data.size)
    }
}
```

### 测试命令

```bash
# 运行所有测试
./gradlew test

# 运行特定平台测试
./gradlew :composeApp:testDebugUnitTest  # Android
./gradlew :composeApp:iosSimulatorArm64Test  # iOS
./gradlew :composeApp:desktopTest  # Desktop

# 运行集成测试
./gradlew :composeApp:connectedAndroidTest  # Android 集成测试

# 生成测试报告
./gradlew :composeApp:testDebugUnitTest --continue
./gradlew :composeApp:jacocoTestReport  # 代码覆盖率报告
```

## 📦 构建发布

### 自动化构建

项目配置了 GitHub Actions 自动化构建流程，支持：

- ✅ **Android APK** 构建和签名 (支持自动签名)
- ✅ **Windows MSI** 安装包构建
- ✅ **macOS DMG** 应用包构建
- ✅ **Linux DEB** 包构建
- ✅ **iOS Framework** 构建 (需要 macOS 环境)


### 代码规范

- 遵循 Kotlin 官方编码规范
- 使用有意义的变量和函数命名
- 添加适当的注释和文档
- 编写单元测试



