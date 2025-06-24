package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.StatisticsApiService
import ovo.sypw.bsp.domain.repository.StatisticsRepository
import ovo.sypw.bsp.domain.repository.impl.StatisticsRepositoryImpl
import ovo.sypw.bsp.domain.usecase.StatisticsUseCase

/**
 * 统计模块依赖注入配置
 */
val statisticsModule = module {

    // API服务
    single<StatisticsApiService> {
        StatisticsApiService()
    }

    // 仓库
    single<StatisticsRepository> {
        StatisticsRepositoryImpl(
            apiService = get(),
            tokenStorage = get()
        )
    }

    // 用例
    single<StatisticsUseCase> {
        StatisticsUseCase(repository = get())
    }

}