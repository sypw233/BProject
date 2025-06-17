package ovo.sypw.bsp

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import ovo.sypw.bsp.di.getAllModules

/**
 * iOS 平台的 Koin 应用初始化
 */
@Composable
actual fun PlatformKoinApplication(content: @Composable () -> Unit) {
    KoinApplication(
        application = {
            modules(getAllModules())
            modules(org.koin.dsl.module {
                single<ovo.sypw.bsp.utils.file.FileUtils> { 
                    ovo.sypw.bsp.utils.file.createFileUtils() 
                }
            })
        }
    ) {
        content()
    }
}