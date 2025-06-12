package ovo.sypw.bsp

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import ovo.sypw.bsp.di.getAllModules

/**
 * Android 平台的 Koin 应用初始化
 * 自动注入 Android Context
 */
@Composable
actual fun PlatformKoinApplication(content: @Composable () -> Unit) {
    val context = LocalContext.current
    Log.d("MAIN", "PlatformKoinApplication: START")
    KoinApplication(
        application = {
            androidContext(context)
            modules(getAllModules())
            modules(module {
                single<Context> { context }
            })
        }
    ) {
        content()
    }
}