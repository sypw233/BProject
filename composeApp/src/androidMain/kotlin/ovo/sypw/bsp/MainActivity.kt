package ovo.sypw.bsp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ovo.sypw.bsp.di.getAllModules

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // 初始化Koin依赖注入，配置Android Context
        startKoin {
            androidContext(this@MainActivity)
            modules(getAllModules())
        }
        
        setContent {
            App()
        }
    }
}
