package ovo.sypw.bsp.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.data.storage.TokenStorageImpl

/**
 * iOS平台的认证模块
 * 提供iOS特定的TokenStorage实现
 */
val iosAuthModule = module {
    // iOS平台的TokenStorage实现
    single<TokenStorage> { TokenStorageImpl() }
}

/**
 * iOS平台的应用模块实现
 * 包含iOS特定的依赖注入配置
 */
actual fun getAllModules(): List<Module> = listOf(
    networkModule,
    viewModelModule,
    authModule,
    iosAuthModule  // iOS特定的认证模块
)