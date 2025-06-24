package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.AIChatApiService
import ovo.sypw.bsp.domain.repository.AIChatRepository
import ovo.sypw.bsp.domain.repository.impl.AIChatRepositoryImpl
import ovo.sypw.bsp.domain.usecase.AIChatUseCase

/**
 * AI对话模块的依赖注入配置
 * 配置AI对话相关的API服务、仓库和用例的依赖注入
 */
val aiChatModule = module {

    /**
     * AI对话API服务
     * 单例模式，提供AI对话相关的网络API调用
     */
    single<AIChatApiService> {
        AIChatApiService()
    }

    /**
     * AI对话仓库实现
     * 单例模式，整合API服务和本地存储
     * 依赖：AIChatApiService, TokenStorage
     */
    single<AIChatRepository> {
        AIChatRepositoryImpl(
            aiChatApiService = get(),
            tokenStorage = get()
        )
    }

    /**
     * AI对话用例
     * 工厂模式，每次注入时创建新实例
     * 封装AI对话相关的业务逻辑
     * 依赖：AIChatRepository
     */
    factory<AIChatUseCase> {
        AIChatUseCase(
            aiChatRepository = get()
        )
    }
}