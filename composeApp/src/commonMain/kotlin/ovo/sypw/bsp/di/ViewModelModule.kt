package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.presentation.viewmodel.ApiTestViewModel
import ovo.sypw.bsp.presentation.viewmodel.AuthViewModel
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel

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
            changePasswordUseCase = get(),
        )
    }
    
    /**
     * 提供后台管理ViewModel
     * 管理部门和员工相关状态
     */
    factory {
        AdminViewModel(
            departmentUseCase = get()
        )
    }
}