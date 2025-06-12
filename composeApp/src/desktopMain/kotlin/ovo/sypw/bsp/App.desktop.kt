package ovo.sypw.bsp

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import ovo.sypw.bsp.di.getAllModules

/**
 * Desktop 平台的 Koin 应用初始化
 */
@Composable
actual fun PlatformKoinApplication(content: @Composable () -> Unit) {
    KoinApplication(
        application = {
            modules(getAllModules())
        }
    ) {
        content()
    }
}