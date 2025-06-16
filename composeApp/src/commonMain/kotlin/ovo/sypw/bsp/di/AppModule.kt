package ovo.sypw.bsp.di

import org.koin.dsl.module

/**
 * 应用主模块
 * 聚合所有的依赖注入模块
 */
val appModule = module {
    // 包含网络模块
    includes(networkModule)
    // 包含认证模块
    includes(authModule)
    // 包含ViewModel模块
    includes(viewModelModule)
    // 包含存储模块
    includes(storageModule)
    // 包含部门管理模块
    includes(departmentModule)
    // 包含班级管理模块
    includes(classModule)
    // 包含员工管理模块
    includes(employeeModule)
    // 包含学生管理模块
    includes(studentModule)
    // 包含文件上传模块
    includes(fileUploadModule)
    // 包含统计模块
    includes(statisticsModule)
    // 包含公告管理模块
    includes(announcementModule)
}

/**
 * 获取所有模块的列表
 * @return 所有Koin模块的列表
 */
fun getAllModules() = listOf(
    networkModule,
    authModule,
    viewModelModule,
    storageModule,
    departmentModule,
    classModule,
    employeeModule,
    studentModule,
    fileUploadModule,
    statisticsModule,
    announcementModule
)