package ovo.sypw.bsp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform