package ovo.sypw.bsp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ovo.sypw.bsp.utils.file.FileUtils
import ovo.sypw.bsp.utils.file.createFileUtils

/**
 * Desktop平台的rememberFileUtils实现
 */
@Composable
actual fun rememberFileUtils(): FileUtils {
    return remember {
        createFileUtils()
    }
}