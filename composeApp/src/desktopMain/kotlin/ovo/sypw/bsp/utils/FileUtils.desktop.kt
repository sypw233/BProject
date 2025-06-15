package ovo.sypw.bsp.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.filechooser.FileNameExtensionFilter
import java.io.File
import java.util.concurrent.atomic.AtomicReference

/**
 * Desktop平台的FileUtils实现
 */
class DesktopFileUtils : FileUtils {
    
    override fun isFileSelectionSupported(): Boolean = true
    
    /**
     * 选择图片文件
     * Desktop平台使用JFileChooser进行文件选择，确保在EDT线程中执行
     * @return 选择的图片文件字节数组，取消选择时返回null
     */
    override suspend fun selectImage(): ByteArray? {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val resultRef = AtomicReference<ByteArray?>()
                val exceptionRef = AtomicReference<Exception?>()
                
                // 确保文件选择器在EDT线程中运行，避免线程冲突
                SwingUtilities.invokeAndWait {
                    try {
                        val fileChooser = JFileChooser()
                        fileChooser.dialogTitle = "选择图片文件"
                        
                        // 设置文件过滤器
                        val filter = FileNameExtensionFilter(
                            "图片文件 (*.jpg, *.jpeg, *.png, *.gif, *.bmp)",
                            "jpg", "jpeg", "png", "gif", "bmp"
                        )
                        fileChooser.fileFilter = filter
                        fileChooser.isAcceptAllFileFilterUsed = false
                        
                        val result = fileChooser.showOpenDialog(null)
                        
                        if (result == JFileChooser.APPROVE_OPTION) {
                            val selectedFile = fileChooser.selectedFile
                            resultRef.set(selectedFile.readBytes())
                        } else {
                            resultRef.set(null)
                        }
                    } catch (e: Exception) {
                        exceptionRef.set(e)
                    }
                }
                
                // 检查是否有异常发生
                exceptionRef.get()?.let { throw it }
                
                resultRef.get()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    override fun bytesToImageBitmap(bytes: ByteArray): ImageBitmap? {
        return try {
            val skiaImage = Image.makeFromEncoded(bytes)
            skiaImage.toComposeImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

/**
 * 创建Desktop平台的FileUtils实例
 */
actual fun createFileUtils(): FileUtils {
    return DesktopFileUtils()
}