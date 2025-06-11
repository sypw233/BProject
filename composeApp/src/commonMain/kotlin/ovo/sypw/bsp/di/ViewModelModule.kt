package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.presentation.viewmodel.ApiTestViewModel

/**
 * ViewModel模块依赖注入配置
 * 管理所有ViewModel的创建和依赖
 */
val viewModelModule = module {
    
    /**
     * 提供API测试ViewModel
     * 依赖ExampleRepository
     */
    factory {
        ApiTestViewModel(get())
    }
}