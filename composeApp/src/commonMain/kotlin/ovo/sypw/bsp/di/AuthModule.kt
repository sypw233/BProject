package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.AuthApiService
import ovo.sypw.bsp.data.repository.AuthRepositoryImpl
import ovo.sypw.bsp.data.storage.TokenStorageImpl
import ovo.sypw.bsp.domain.repository.AuthRepository
import ovo.sypw.bsp.domain.usecase.AuthUseCase
import ovo.sypw.bsp.presentation.viewmodel.AuthViewModel

/**
 * 认证模块
 * 配置认证相关的依赖注入
 * 注意：TokenStorage由平台特定模块提供
 */
val authModule = module {
    
    // API服务
    single { AuthApiService() }
    
    // Repository
    single<AuthRepository> { 
        AuthRepositoryImpl(
            authApiService = get(),
            tokenStorage = get()
        ) 
    }
    
    // UseCase
    single { 
        AuthUseCase(
            authRepository = get()
        ) 
    }
    
    // ViewModel
    single { 
        AuthViewModel(
            authUseCase = get()
        ) 
    }
}