package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.NetdiskApiService
import ovo.sypw.bsp.domain.repository.NetdiskRepository
import ovo.sypw.bsp.domain.repository.impl.NetdiskRepositoryImpl
import ovo.sypw.bsp.presentation.viewmodel.NetdiskViewModel

/**
 * 网盘管理模块依赖注入配置
 */
val netdiskModule = module {

    /**
     * 提供网盘API服务
     */
    single<NetdiskApiService> {
        NetdiskApiService()
    }

    /**
     * 提供网盘Repository实现
     */
    single<NetdiskRepository> {
        NetdiskRepositoryImpl(
            netdiskApiService = get(),
            tokenStorage = get()
        )
    }

    /**
     * 提供网盘管理ViewModel
     */
    factory {
        NetdiskViewModel(
            repository = get(),
            fileUtils = get()
        )
    }
}