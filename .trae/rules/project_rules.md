1. 只编译桌面端查看报错
2. 所有when语句都添加else分支
3. 项目目录结构参考
MyKMPProject/
├── shared/                    # 共享核心模块
│   ├── src/
│   │   ├── commonMain/kotlin/com/app/
│   │   │   ├── data/         # 数据层
│   │   │   │   ├── api/      # API接口
│   │   │   │   ├── dto/      # 数据传输对象
│   │   │   │   ├── local/    # 本地存储
│   │   │   │   └── repository/ # 仓库实现
│   │   │   ├── domain/       # 业务层
│   │   │   │   ├── model/    # 领域模型
│   │   │   │   ├── repository/ # 仓库接口
│   │   │   │   └── usecase/  # 用例
│   │   │   ├── presentation/ # 表现层
│   │   │   │   ├── viewmodel/ # ViewModel
│   │   │   │   └── state/    # UI状态
│   │   │   ├── di/          # 依赖注入
│   │   │   └── utils/       # 工具类
│   │   ├── androidMain/kotlin/  # Android平台实现
│   │   ├── iosMain/kotlin/      # iOS平台实现
│   │   ├── desktopMain/kotlin/  # Desktop平台实现
│   │   └── jsMain/kotlin/       # Web平台实现
│   └── build.gradle.kts
├── androidApp/               # Android应用
├── iosApp/                  # iOS应用
├── desktopApp/              # Desktop应用
└── webApp/                  # Web应用