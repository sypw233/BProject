package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.AuthApiService
import ovo.sypw.bsp.data.repository.AuthRepositoryImpl
import ovo.sypw.bsp.domain.repository.AuthRepository
import ovo.sypw.bsp.domain.usecase.*

/**
 * 认证模块依赖注入配置
 * 包含所有认证相关的服务、仓库和用例
 */
val authModule = module {
    
    /**
     * 提供认证API服务
     */
    single<AuthApiService> {
        AuthApiService()
    }
    
    /**
     * 提供认证仓库实现
     */
    single<AuthRepository> {
        AuthRepositoryImpl(
            authApiService = get(),
            tokenStorage = get()
        )
    }
    
    /**
     * 提供登录用例
     */
    single<LoginUseCase> {
        LoginUseCase(
            authRepository = get()
        )
    }
    
    /**
     * 提供注册用例
     */
    single<RegisterUseCase> {
        RegisterUseCase(
            authRepository = get()
        )
    }
    
    /**
     * 提供登出用例
     */
    single<LogoutUseCase> {
        LogoutUseCase(
            authRepository = get()
        )
    }
    
    /**
     * 提供获取用户信息用例
     */
    single<GetUserInfoUseCase> {
        GetUserInfoUseCase(
            authRepository = get()
        )
    }
    
    /**
     * 提供刷新令牌用例
     */
    single<RefreshTokenUseCase> {
        RefreshTokenUseCase(
            authRepository = get()
        )
    }
}