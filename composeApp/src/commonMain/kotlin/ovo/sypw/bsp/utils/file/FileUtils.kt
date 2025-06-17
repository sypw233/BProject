package ovo.sypw.bsp.utils.file

import androidx.compose.ui.graphics.ImageBitmap

/**
 * 跨平台文件工具类接口
 * 用于处理图片选择和读取功能
 */
interface FileUtils {
    /**
     * 检查当前平台是否支持文件选择
     * @return 是否支持文件选择
     */
    fun isFileSelectionSupported(): Boolean
    
    /**
     * 选择图片文件
     * @return 图片的字节数组，如果取消选择则返回null
     */
    suspend fun selectImage(): ByteArray?
    
    /**
     * 选择文件
     * @return 文件的字节数组，如果取消选择则返回null
     */
    suspend fun selectFile(): ByteArray?
    
    /**
     * 保存文件
     * @param data 文件数据
     * @param fileName 文件名
     * @param mimeType MIME类型
     * @return 是否保存成功
     */
    suspend fun saveFile(data: ByteArray, fileName: String, mimeType: String): Boolean
    
    /**
     * 将字节数组转换为ImageBitmap
     * @param bytes 图片字节数组
     * @return ImageBitmap对象
     */
    fun bytesToImageBitmap(bytes: ByteArray): ImageBitmap?
}

/**
 * 获取平台特定的FileUtils实例
 */
expect fun createFileUtils(): FileUtils

/**
 * 支持的图片格式
 */
object SupportedImageFormats {
    val extensions = listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    val mimeTypes = listOf(
        "image/jpeg",
        "image/png", 
        "image/gif",
        "image/bmp",
        "image/webp"
    )
}