package ovo.sypw.bsp.utils.file

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import android.graphics.BitmapFactory

/**
 * Android平台的FileUtils实现
 */
class AndroidFileUtils(private val context: Context) : FileUtils {
    
    override fun isFileSelectionSupported(): Boolean = true
    
    /**
     * 选择图片文件
     * Android平台使用GetContent contract进行文件选择
     * @return 选择的图片文件字节数组，取消选择时返回null
     */
    override suspend fun selectImage(): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            try {
                val activity = context as? ComponentActivity
                if (activity == null) {
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }
                
                // 使用GetContent contract进行文件选择
                val launcher = activity.registerForActivityResult(
                    ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    try {
                        if (uri != null) {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val bytes = inputStream?.readBytes()
                            inputStream?.close()
                            continuation.resume(bytes)
                        } else {
                            continuation.resume(null)
                        }
                    } catch (e: Exception) {
                        continuation.resume(null)
                    }
                }
                
                launcher.launch("image/*")
                
            } catch (e: Exception) {
                continuation.resume(null)
            }
        }
    }

    override suspend fun selectFile(): ByteArray? {
        TODO("Not yet implemented")
    }

    override suspend fun saveFile(
        data: ByteArray,
        fileName: String,
        mimeType: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * 将字节数组转换为ImageBitmap
     * Android平台使用BitmapFactory进行图片解码
     * @param bytes 图片文件的字节数组
     * @return 解码后的ImageBitmap对象，解码失败时返回null
     */
    override fun bytesToImageBitmap(bytes: ByteArray): ImageBitmap? {
        return try {
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * 创建Android平台的FileUtils实例
 * Android平台需要Context参数，此函数会抛出异常提示使用带Context的版本
 * @throws IllegalStateException 提示使用createFileUtils(context)函数
 */
actual fun createFileUtils(): FileUtils {
    throw IllegalStateException("Android FileUtils需要Context参数，请使用createFileUtils(context)")
}

/**
 * 创建Android平台的FileUtils实例（带Context参数）
 * @param context Android应用上下文，用于文件操作和Intent启动
 * @return AndroidFileUtils实例
 */
fun createFileUtils(context: Context): FileUtils {
    return AndroidFileUtils(context)
}