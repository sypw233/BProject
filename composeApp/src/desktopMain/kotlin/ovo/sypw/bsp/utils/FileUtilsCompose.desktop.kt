package ovo.sypw.bsp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Desktop平台的rememberFileUtils实现
 */
@Composable
actual fun rememberFileUtils(): FileUtils {
    return remember {
        createFileUtils()
    }
}