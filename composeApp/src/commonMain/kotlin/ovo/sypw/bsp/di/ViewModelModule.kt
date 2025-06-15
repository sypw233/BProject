package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.presentation.viewmodel.ApiTestViewModel
import ovo.sypw.bsp.presentation.viewmodel.AuthViewModel
import ovo.sypw.bsp.presentation.viewmodel.AdminViewModel
import ovo.sypw.bsp.presentation.viewmodel.DepartmentViewModel
import ovo.sypw.bsp.presentation.viewmodel.EmployeeViewModel

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
     * 负责Tab切换和基础状态管理
     */
    factory {
        AdminViewModel()
    }
    
    /**
     * 提供部门管理ViewModel
     * 专门负责部门相关的状态管理和业务逻辑
     */
    factory {
        DepartmentViewModel(
            departmentUseCase = get()
        )
    }
    
    /**
     * 提供员工管理ViewModel
     * 专门负责员工相关的状态管理和业务逻辑
     */
    factory {
        EmployeeViewModel(
            employeeUseCase = get(),
            departmentUseCase = get()
        )
    }
}