package ovo.sypw.bsp.di

import org.koin.core.module.Module

/**
 * Android平台的应用模块实现
 * 包含Android特定的依赖注入配置
 */
actual fun getAllModules(): List<Module> = listOf(
    networkModule,
    viewModelModule,
    authModule,
    androidAuthModule  // Android特定的认证模块
)