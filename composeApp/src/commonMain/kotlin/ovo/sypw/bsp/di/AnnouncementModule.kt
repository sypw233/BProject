package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.data.api.AnnouncementApiService
import ovo.sypw.bsp.data.repository.AnnouncementRepositoryImpl
import ovo.sypw.bsp.domain.repository.AnnouncementRepository
import ovo.sypw.bsp.domain.usecase.AnnouncementUseCase

/**
 * 公告管理模块依赖注入
 */
val announcementModule = module {

    // API服务
    single { AnnouncementApiService() }

    // 仓库
    single<AnnouncementRepository> {
        AnnouncementRepositoryImpl(
            announcementApiService = get(),
            tokenStorage = get()
        )
    }

    // 用例
    single {
        AnnouncementUseCase(
            announcementRepository = get()
        )
    }
}