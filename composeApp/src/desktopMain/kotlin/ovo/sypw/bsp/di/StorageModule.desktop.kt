package ovo.sypw.bsp.di

import ovo.sypw.bsp.data.storage.LocalStorage

/**
 * Desktop平台的LocalStorage创建函数
 * 直接创建LocalStorage实例，无需额外参数
 */
actual fun createLocalStorage(): LocalStorage {
    return LocalStorage()
}