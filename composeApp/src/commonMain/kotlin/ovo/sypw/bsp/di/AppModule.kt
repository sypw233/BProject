package ovo.sypw.bsp.di

import org.koin.dsl.module

/**
 * 应用主模块
 * 聚合所有的依赖注入模块
 */
val appModule = module {
    // 包含网络模块
    includes(networkModule)
    // 包含ViewModel模块
    includes(viewModelModule)
    // 包含认证模块
    includes(authModule)
}

/**
 * 获取所有模块的列表
 * @return 所有Koin模块的列表
 */
expect fun getAllModules(): List<org.koin.core.module.Module>