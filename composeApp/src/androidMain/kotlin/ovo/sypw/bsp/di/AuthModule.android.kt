package ovo.sypw.bsp.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.data.storage.TokenStorageImpl

/**
 * Android平台的认证模块
 * 提供Android特定的TokenStorage实现
 */
val androidAuthModule = module {
    
    // Android平台的TokenStorage实现，需要Context
    single<TokenStorage> { TokenStorageImpl(androidContext()) }
}