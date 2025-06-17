package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.FileUploadApiService
import ovo.sypw.bsp.data.repository.FileUploadRepositoryImpl
import ovo.sypw.bsp.domain.repository.FileUploadRepository
import ovo.sypw.bsp.domain.usecase.FileUploadUseCase

/**
 * 文件上传模块
 * 提供文件上传相关的依赖注入配置
 */
val fileUploadModule = module {

    // API服务
    single<FileUploadApiService> {
        FileUploadApiService()
    }

    // Repository
    single<FileUploadRepository> {
        FileUploadRepositoryImpl(
            fileUploadApiService = get(),
            tokenStorage = get()
        )
    }

    // UseCase
    single<FileUploadUseCase> {
        FileUploadUseCase(
            fileUploadRepository = get()
        )
    }
}