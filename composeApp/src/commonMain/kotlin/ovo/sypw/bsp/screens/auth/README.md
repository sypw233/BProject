# 认证功能模块

本模块实现了完整的用户认证功能，包括登录、注册、用户信息管理等功能，支持所有KMP目标平台。

## 功能特性

### 🔐 核心功能
- ✅ 用户登录
- ✅ 用户注册
- ✅ 获取当前用户信息
- ✅ 退出登录
- ✅ 登录状态检查
- ✅ Token自动管理

### 🌐 跨平台支持
- ✅ Android - 使用SharedPreferences存储Token
- ✅ iOS - 使用NSUserDefaults存储Token
- ✅ Desktop - 使用Properties文件存储Token
- ✅ Web (JS) - 使用localStorage存储Token
- ✅ Web (WASM) - 使用localStorage存储Token

## 架构设计

### 分层架构
```
Presentation Layer (UI)
    ↓
ViewModel Layer (状态管理)
    ↓
UseCase Layer (业务逻辑)
    ↓
Repository Layer (数据访问)
    ↓
Data Layer (API + Storage)
    ↓
Platform Layer (expect/actual)
```

### 核心组件

#### 1. 数据传输对象 (DTO)
- `UserLoginDTO` - 登录请求数据
- `UserRegisterDTO` - 注册请求数据
- `AuthResponseDTO` - 认证响应数据
- `UserInfoDTO` - 用户信息数据
- `SaResult<T>` - 统一响应格式

#### 2. 网络层
- `AuthApiService` - 认证相关API服务
- 基于Ktor Client实现跨平台网络请求

#### 3. 存储层
- `TokenStorage` - Token存储接口
- `TokenStorageImpl` - 各平台具体实现

#### 4. 业务层
- `AuthRepository` - 认证仓库接口
- `AuthRepositoryImpl` - 认证仓库实现
- `AuthUseCase` - 认证用例

#### 5. 表现层
- `AuthViewModel` - 认证视图模型
- `LoginScreen` - 登录界面
- `RegisterScreen` - 注册界面
- `AuthDemoScreen` - 认证演示界面

## 使用方法

### 1. 依赖注入配置

认证模块已集成到Koin依赖注入系统中：

```kotlin
// 在App.kt中自动初始化
val authViewModel: AuthViewModel = koinInject()
```

### 2. 导航集成

认证页面已集成到应用导航系统中：

- 认证演示页面：`AppScreen.AUTH_DEMO`
- 登录页面：`AppScreen.LOGIN`
- 注册页面：`AppScreen.REGISTER`

### 3. API接口

#### 登录接口
```http
POST /auth/login
Content-Type: application/json

{
  "username": "用户名",
  "password": "密码"
}
```

#### 注册接口
```http
POST /auth/register
Content-Type: application/json

{
  "username": "用户名",
  "password": "密码"
}
```

#### 获取用户信息
```http
GET /auth/current
Authorization: Bearer {token}
```

### 4. 状态管理

`AuthViewModel`提供了完整的状态管理：

```kotlin
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: UserInfoDTO? = null,
    val errorMessage: String? = null,
    val loginForm: LoginFormState = LoginFormState(),
    val registerForm: RegisterFormState = RegisterFormState()
)
```

### 5. 表单验证

内置表单验证功能：
- 用户名长度验证（3-20字符）
- 密码强度验证（6-50字符）
- 注册时密码确认验证

## 平台特定实现

### Android平台
```kotlin
// androidMain/TokenStorageImpl.android.kt
actual class TokenStorageImpl(private val context: Context) : TokenStorage {
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    // 使用SharedPreferences存储
}
```

### iOS平台
```kotlin
// iosMain/TokenStorageImpl.ios.kt
actual class TokenStorageImpl : TokenStorage {
    // 使用NSUserDefaults存储
}
```

### Desktop平台
```kotlin
// desktopMain/TokenStorageImpl.desktop.kt
actual class TokenStorageImpl : TokenStorage {
    // 使用Properties文件存储
}
```

### Web平台
```kotlin
// jsMain/TokenStorageImpl.js.kt
// wasmJsMain/TokenStorageImpl.wasmJs.kt
actual class TokenStorageImpl : TokenStorage {
    // 使用localStorage存储
}
```

## 安全考虑

1. **Token存储安全**
   - Android: 使用私有SharedPreferences
   - iOS: 使用NSUserDefaults（可考虑升级到Keychain）
   - Desktop: 使用用户目录下的配置文件
   - Web: 使用localStorage（注意XSS防护）

2. **网络安全**
   - 使用HTTPS传输
   - Token在请求头中传递
   - 敏感信息不在URL中暴露

3. **输入验证**
   - 客户端表单验证
   - 服务端验证（由后端实现）

## 扩展功能

### 可扩展的功能点
1. **Token刷新机制**
2. **生物识别认证**
3. **多因素认证**
4. **社交登录集成**
5. **密码重置功能**
6. **用户资料管理**

### 性能优化
1. **Token缓存策略**
2. **网络请求优化**
3. **UI状态优化**
4. **内存管理优化**

## 测试建议

### 单元测试
- Repository层测试
- UseCase层测试
- ViewModel层测试

### 集成测试
- API接口测试
- 跨平台存储测试
- 端到端认证流程测试

### UI测试
- 登录界面测试
- 注册界面测试
- 错误处理测试

## 故障排除

### 常见问题
1. **网络连接问题** - 检查网络配置和API地址
2. **Token存储问题** - 检查平台特定的存储权限
3. **依赖注入问题** - 确保模块正确注册
4. **导航问题** - 检查路由配置

### 调试技巧
1. 启用网络日志
2. 检查Token存储状态
3. 验证API响应格式
4. 查看ViewModel状态变化

---

**注意**: 本模块遵循KMP最佳实践，确保代码在所有目标平台上的一致性和可维护性。