package ovo.sypw.bsp.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import ovo.sypw.bsp.data.api.ExampleApiService
import ovo.sypw.bsp.data.api.HttpClientConfig
import ovo.sypw.bsp.data.repository.ExampleRepositoryImpl
import ovo.sypw.bsp.domain.repository.ExampleRepository

/**
 * 网络模块依赖注入配置
 */
val networkModule = module {

    /**
     * 提供HttpClient单例
     */
    single<HttpClient> {
        HttpClientConfig.createHttpClient()
    }

    /**
     * 提供调试模式的HttpClient
     */
    single<HttpClient>(qualifier = org.koin.core.qualifier.named("debug")) {
        HttpClientConfig.createDebugHttpClient()
    }

    /**
     * 提供API服务
     */
    single<ExampleApiService> {
        ExampleApiService()
    }

    /**
     * 提供Repository实现
     */
    single<ExampleRepository> {
        ExampleRepositoryImpl(
            apiService = get(),
            tokenStorage = get()
        )
    }
}