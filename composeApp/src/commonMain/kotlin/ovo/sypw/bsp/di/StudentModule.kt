package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.StudentApiService
import ovo.sypw.bsp.data.repository.StudentRepositoryImpl
import ovo.sypw.bsp.domain.repository.StudentRepository
import ovo.sypw.bsp.domain.usecase.StudentUseCase

/**
 * 学生管理模块的依赖注入配置
 * 配置学生相关的API服务、仓库和用例的依赖注入
 */
val studentModule = module {

    /**
     * 学生API服务
     * 单例模式，提供学生相关的网络API调用
     */
    single<StudentApiService> {
        StudentApiService()
    }

    /**
     * 学生仓库接口实现
     * 单例模式，整合API服务和本地存储
     */
    single<StudentRepository> {
        StudentRepositoryImpl(
            studentApiService = get(), // 从Koin容器中获取StudentApiService实例
            tokenStorage = get() // 从Koin容器中获取TokenStorage实例
        )
    }

    /**
     * 学生业务用例
     * 工厂模式，每次注入时创建新实例
     * 封装学生相关的业务逻辑
     */
    factory {
        StudentUseCase(
            studentRepository = get() // 从Koin容器中获取StudentRepository实例
        )
    }
}