package ovo.sypw.bsp

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import ovo.sypw.bsp.di.getAllModules
import ovo.sypw.bsp.utils.file.FileUtils
import ovo.sypw.bsp.utils.file.createFileUtils

/**
 * Desktop 平台的 Koin 应用初始化
 */
@Composable
actual fun PlatformKoinApplication(content: @Composable () -> Unit) {
    KoinApplication(
        application = {
            modules(getAllModules())
            modules(org.koin.dsl.module {
                single<FileUtils> {
                    createFileUtils()
                }
            })
        }
    ) {
        content()
    }
}