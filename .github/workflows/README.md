# GitHub Actions 构建脚本说明

本项目包含三个GitHub Actions工作流，用于自动化构建Kotlin Multiplatform桌面应用程序。

## 📋 工作流概览

### 1. `build-windows.yml` - Windows专用构建
**用途**: 专门构建Windows平台的.exe文件和MSI安装包

**触发条件**:
- 推送到 `main` 分支
- 创建版本标签 (如 `v1.0.0`)
- Pull Request到 `main` 分支
- 手动触发

**构建产物**:
- Windows MSI安装包 (`.msi`)
- Windows可执行文件 (`.exe`)

### 2. `build-desktop.yml` - 全平台桌面构建
**用途**: 构建所有支持的桌面平台应用

**支持平台**:
- ✅ Windows (MSI安装包)
- ✅ macOS (DMG安装包) 
- ✅ Linux (DEB安装包)

**触发条件**:
- 推送到 `main` 分支
- 创建版本标签
- Pull Request到 `main` 分支
- 手动触发

**特殊功能**:
- 自动创建GitHub Release (仅限标签推送)
- 包含所有平台的安装包

### 3. `quick-build.yml` - 快速构建测试
**用途**: 开发阶段的快速验证和测试

**触发条件**:
- 推送到 `develop` 或 `feature/*` 分支
- Pull Request到 `main` 或 `develop` 分支
- 手动触发

**特点**:
- 仅构建Windows版本（速度快）
- 运行单元测试
- 构建产物保留7天
- 适合开发阶段使用

## 🚀 使用指南

### 开发阶段
1. 在 `develop` 或 `feature/*` 分支开发
2. 推送代码触发 `quick-build.yml`
3. 检查构建状态和测试结果
4. 下载构建产物进行本地测试

### 发布准备
1. 合并代码到 `main` 分支
2. 触发 `build-desktop.yml` 构建所有平台
3. 验证所有平台的构建产物

### 正式发布
1. 创建版本标签：`git tag v1.0.0 && git push origin v1.0.0`
2. 自动触发全平台构建
3. 自动创建GitHub Release
4. 发布包含所有平台安装包的Release

## 🛠️ 技术细节

### 构建环境
- **JDK版本**: 21 (Temurin发行版)
- **Kotlin版本**: 2.1.21
- **Compose Multiplatform**: 1.8.1
- **Gradle缓存**: 自动缓存依赖以加速构建

### 构建任务
```bash
# Windows MSI安装包
./gradlew :composeApp:packageMsi

# Windows可执行文件
./gradlew :composeApp:createDistributable

# macOS DMG安装包
./gradlew :composeApp:packageDmg

# Linux DEB安装包
./gradlew :composeApp:packageDeb

# 所有平台
./gradlew :composeApp:packageDistributionForCurrentOS
```

### 构建产物路径
```
composeApp/build/compose/binaries/main/
├── msi/           # Windows MSI安装包
├── dmg/           # macOS DMG安装包
├── deb/           # Linux DEB安装包
└── app/           # 可执行文件目录
```

## 📦 安装说明

### Windows
1. 下载 `.msi` 文件
2. 双击运行安装程序
3. 按照向导完成安装

### macOS
1. 下载 `.dmg` 文件
2. 双击挂载磁盘映像
3. 拖拽应用到 `Applications` 文件夹

### Linux
1. 下载 `.deb` 文件
2. 运行: `sudo dpkg -i filename.deb`
3. 或使用图形化包管理器安装

## 🔧 自定义配置

### 修改应用信息
在 `composeApp/build.gradle.kts` 中修改:
```kotlin
compose.desktop {
    application {
        mainClass = "ovo.sypw.bsp.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "你的应用名称"
            packageVersion = "1.0.0"
            
            // 可选：添加应用图标
            // macOS
            macOS {
                iconFile.set(project.file("src/macosMain/resources/icon.icns"))
            }
            // Windows
            windows {
                iconFile.set(project.file("src/windowsMain/resources/icon.ico"))
            }
            // Linux
            linux {
                iconFile.set(project.file("src/linuxMain/resources/icon.png"))
            }
        }
    }
}
```

### 添加代码签名（可选）
对于生产环境，建议添加代码签名以提高安全性和用户信任度。

## 🐛 故障排除

### 常见问题
1. **构建失败**: 检查JDK版本是否为21
2. **权限错误**: 确保gradlew有执行权限
3. **依赖问题**: 清理Gradle缓存后重试
4. **内存不足**: 在gradle.properties中增加堆内存

### 调试命令
```bash
# 本地测试构建
./gradlew :composeApp:packageDistributionForCurrentOS --info

# 清理构建缓存
./gradlew clean

# 检查依赖
./gradlew :composeApp:dependencies
```

## 📞 支持

如果遇到问题，请：
1. 检查GitHub Actions的构建日志
2. 查看本项目的Issues
3. 参考Compose Multiplatform官方文档