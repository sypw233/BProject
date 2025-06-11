package ovo.sypw.bsp.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level


/**
 * Koin依赖注入初始化器
 * 负责启动和停止Koin容器
 */
object KoinInitializer {
    
    /**
     * 初始化Koin依赖注入容器
     * @param enableLogging 是否启用日志记录
     */
    fun init(enableLogging: Boolean = false) {
        startKoin {
            // 配置日志
//            if (enableLogging) {
//                logger(PrintLogger(Level.DEBUG))
//            }
            
            // 加载所有模块
            modules(getAllModules())
        }
    }

    /**
     * 停止Koin容器
     * 通常在应用关闭时调用
     */
    fun stop() {
        stopKoin()
    }
    
    /**
     * 重新初始化Koin容器
     * 先停止再启动，用于测试或重置场景
     * @param enableLogging 是否启用日志记录
     */
    fun restart(enableLogging: Boolean = false) {
        stop()
        init(enableLogging)
    }
}