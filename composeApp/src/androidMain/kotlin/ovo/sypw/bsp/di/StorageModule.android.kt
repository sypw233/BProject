package ovo.sypw.bsp.di


import android.content.Context
import org.koin.core.context.GlobalContext
import ovo.sypw.bsp.data.storage.LocalStorage

/**
 * Android平台的LocalStorage创建函数
 * 使用Android Context创建LocalStorage实例
 */
actual fun createLocalStorage(): LocalStorage {
    return LocalStorage(GlobalContext.get().get<Context>())
}