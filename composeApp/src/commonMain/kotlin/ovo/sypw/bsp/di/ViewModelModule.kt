package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.presentation.viewmodel.ApiTestViewModel
import ovo.sypw.bsp.presentation.viewmodel.AuthViewModel

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
    
    /**
     * 提供认证ViewModel
     * 依赖TokenStorage和各种用例
     */
    single<AuthViewModel> {
        AuthViewModel(
            tokenStorage = get(),
            loginUseCase = get(),
            registerUseCase = get(),
            logoutUseCase = get(),
            getUserInfoUseCase = get(),
            refreshTokenUseCase = get(),
            changePasswordUseCase = get(),
        )
    }
}