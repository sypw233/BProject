package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.RequestLogApiService
import ovo.sypw.bsp.domain.repository.RequestLogRepository
import ovo.sypw.bsp.domain.repository.impl.RequestLogRepositoryImpl
import ovo.sypw.bsp.domain.usecase.RequestLogUseCase

/**
 * 请求日志管理模块的依赖注入配置
 * 配置请求日志相关的API服务、仓库和用例的依赖注入
 */
val requestLogModule = module {

    /**
     * 请求日志API服务
     * 单例模式，提供请求日志相关的网络API调用
     */
    single<RequestLogApiService> {
        RequestLogApiService()
    }

    /**
     * 请求日志仓库接口实现
     * 单例模式，整合API服务和本地存储
     */
    single<RequestLogRepository> {
        RequestLogRepositoryImpl(
            requestLogApiService = get(), // 从Koin容器中获取RequestLogApiService实例
            tokenStorage = get() // 从Koin容器中获取TokenStorage实例
        )
    }

    /**
     * 请求日志业务用例
     * 工厂模式，每次注入时创建新实例
     * 封装请求日志相关的业务逻辑
     */
    factory {
        RequestLogUseCase(
            requestLogRepository = get() // 从Koin容器中获取RequestLogRepository实例
        )
    }
}