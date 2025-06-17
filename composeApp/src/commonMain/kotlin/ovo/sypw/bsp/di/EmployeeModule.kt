package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.EmployeeApiService
import ovo.sypw.bsp.data.repository.EmployeeRepositoryImpl
import ovo.sypw.bsp.domain.repository.EmployeeRepository
import ovo.sypw.bsp.domain.usecase.EmployeeUseCase

/**
 * 员工管理模块的依赖注入配置
 * 配置员工相关的API服务、Repository和UseCase的依赖关系
 */
val employeeModule = module {

    // API服务层
    single<EmployeeApiService> {
        EmployeeApiService()
    }

    // Repository层
    single<EmployeeRepository> {
        EmployeeRepositoryImpl(
            employeeApiService = get(),
            tokenStorage = get()
        )
    }

    // UseCase层
    single<EmployeeUseCase> {
        EmployeeUseCase(
            employeeRepository = get()
        )
    }
}