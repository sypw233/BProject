package ovo.sypw.bsp.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ovo.sypw.bsp.data.api.DepartmentApiService
import ovo.sypw.bsp.data.repository.DepartmentRepositoryImpl
import ovo.sypw.bsp.domain.repository.DepartmentRepository
import ovo.sypw.bsp.domain.usecase.DepartmentUseCase

/**
 * 部门管理模块依赖注入
 */
val departmentModule = module {
    
    // API服务
    single { DepartmentApiService() }
    
    // 仓库
    single<DepartmentRepository> { 
        DepartmentRepositoryImpl(
            departmentApiService = get(),
            tokenStorage = get()
        ) 
    }
    
    // 用例
    single { 
        DepartmentUseCase(
            departmentRepository = get()
        ) 
    }
}