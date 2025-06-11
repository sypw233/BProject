package ovo.sypw.bsp.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.data.storage.TokenStorageImpl

/**
 * Desktop平台的认证模块
 * 提供Desktop特定的TokenStorage实现
 */
val desktopAuthModule = module {
    // Desktop平台的TokenStorage实现
    single<TokenStorage> { TokenStorageImpl() }
}

/**
 * Desktop平台的应用模块实现
 * 包含Desktop特定的依赖注入配置
 */
actual fun getAllModules(): List<Module> = listOf(
    networkModule,
    viewModelModule,
    authModule,
    desktopAuthModule  // Desktop特定的认证模块
)