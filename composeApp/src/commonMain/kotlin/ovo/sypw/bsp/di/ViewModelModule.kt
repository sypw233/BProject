package ovo.sypw.bsp.di

import org.koin.dsl.module
import ovo.sypw.bsp.presentation.viewmodel.ApiTestViewModel
import ovo.sypw.bsp.presentation.viewmodel.PublicAnnouncementViewModel
import ovo.sypw.bsp.presentation.viewmodel.admin.AdminViewModel
import ovo.sypw.bsp.presentation.viewmodel.admin.AnnouncementViewModel
import ovo.sypw.bsp.presentation.viewmodel.admin.AuthViewModel
import ovo.sypw.bsp.presentation.viewmodel.admin.ClassViewModel
import ovo.sypw.bsp.presentation.viewmodel.admin.DepartmentViewModel
import ovo.sypw.bsp.presentation.viewmodel.admin.EmployeeViewModel
import ovo.sypw.bsp.presentation.viewmodel.admin.StudentViewModel
import ovo.sypw.bsp.presentation.viewmodel.admin.RequestLogViewModel
import ovo.sypw.bsp.presentation.viewmodel.AIChatViewModel
import ovo.sypw.bsp.presentation.viewmodel.admin.StatisticsViewModel

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
     * 提供AI对话ViewModel
     * 依赖AIChatUseCase, AIChatApiService, TokenStorage
     */
    factory {
        AIChatViewModel(
            aiChatUseCase = get(),
            aiChatApiService = get(),
            tokenStorage = get()
        )
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
            departmentUseCase = get(),
            fileUploadUseCase = get(),
            fileUtils = get()
        )
    }

    /**
     * 提供班级管理ViewModel
     * 专门负责班级相关的状态管理和业务逻辑
     */
    factory {
        ClassViewModel(
            classUseCase = get()
        )
    }

    /**
     * 提供学生管理ViewModel
     * 专门负责学生相关的状态管理和业务逻辑
     */
    factory {
        StudentViewModel(
            studentUseCase = get(),
            classUseCase = get(),
            fileUtils = get()
        )
    }

    /**
     * 提供公告管理ViewModel
     * 专门负责公告相关的状态管理和业务逻辑
     */
    factory {
        AnnouncementViewModel(
            announcementUseCase = get()
        )
    }

    /**
     * 提供公告显示ViewModel
     * 专门负责公告显示界面的状态管理和业务逻辑
     */
    factory {
        PublicAnnouncementViewModel(
            announcementUseCase = get()
        )
    }

    /**
     * 提供请求日志管理ViewModel
     * 专门负责请求日志相关的状态管理和业务逻辑
     */
    factory {
        RequestLogViewModel(
            requestLogUseCase = get()
        )
    }

    factory {
        StatisticsViewModel(
            statisticsUseCase = get()
        )
    }
}