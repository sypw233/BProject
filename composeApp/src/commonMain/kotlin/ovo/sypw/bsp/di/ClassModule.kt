package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.ClassApiService
import ovo.sypw.bsp.domain.repository.ClassRepository
import ovo.sypw.bsp.domain.repository.impl.ClassRepositoryImpl
import ovo.sypw.bsp.domain.usecase.ClassUseCase

/**
 * 班级管理模块依赖注入
 */
val classModule = module {

    // API服务
    single { ClassApiService() }

    // 仓库
    single<ClassRepository> {
        ClassRepositoryImpl(
            classApiService = get(),
            tokenStorage = get()
        )
    }

    // 用例
    single {
        ClassUseCase(
            classRepository = get()
        )
    }
}