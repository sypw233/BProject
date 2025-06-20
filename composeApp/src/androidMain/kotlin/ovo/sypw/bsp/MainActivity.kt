package ovo.sypw.bsp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import io.github.vinceglb.filekit.core.FileKit
import ovo.sypw.bsp.utils.file.AndroidFileUtils
import ovo.sypw.bsp.utils.file.FileUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        FileKit.init(this)
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
