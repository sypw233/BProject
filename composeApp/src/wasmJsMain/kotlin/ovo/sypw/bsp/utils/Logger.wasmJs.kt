package ovo.sypw.bsp.utils

/**
 * Web平台的日志实现
 * 使用console.log进行日志输出
 */
actual fun log(level: Logger.Level, tag: String, message: String, throwable: Throwable?) {
    val levelStr = when (level) {
        Logger.Level.DEBUG -> "[DEBUG]"
        Logger.Level.INFO -> "[INFO]"
        Logger.Level.WARN -> "[WARN]"
        Logger.Level.ERROR -> "[ERROR]"
    }
    
    val logMessage = if (throwable != null) {
        "$levelStr [$tag] $message\nException: ${throwable.message}\n${throwable.stackTraceToString()}"
    } else {
        "$levelStr [$tag] $message"
    }
    
    // 使用不同的console方法根据日志级别
    when (level) {
        Logger.Level.DEBUG -> println(logMessage)
        Logger.Level.INFO -> println(logMessage)
        Logger.Level.WARN -> println(logMessage)
        Logger.Level.ERROR -> println(logMessage)
    }
}