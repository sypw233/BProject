package ovo.sypw.bsp.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.skia.Image
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.File
import org.w3c.files.FileReader
import kotlinx.browser.document
import kotlin.coroutines.resume
import kotlin.js.Promise
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array

/**
 * Web平台的FileUtils实现
 */
class WebFileUtils : FileUtils {
    
    override fun isFileSelectionSupported(): Boolean {
        return js("typeof FileReader !== 'undefined'") as Boolean
    }
    
    override suspend fun selectImage(): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            try {
                val input = document.createElement("input") as HTMLInputElement
                input.type = "file"
                input.accept = "image/*"
                
                input.onchange = { event ->
                    val files = input.files
                    if (files != null && files.length > 0) {
                        val file = files[0]!!
                        val reader = FileReader()
                        
                        reader.onload = {
                            try {
                                val arrayBuffer = reader.result as ArrayBuffer
                                val int8Array = Int8Array(arrayBuffer)
                                val byteArray = ByteArray(int8Array.length) { i ->
                                    int8Array[i]
                                }
                                continuation.resume(byteArray)
                            } catch (e: Exception) {
                                continuation.resume(null)
                            }
                        }
                        
                        reader.onerror = {
                            continuation.resume(null)
                        }
                        
                        reader.readAsArrayBuffer(file)
                    } else {
                        continuation.resume(null)
                    }
                }
                
                input.oncancel = {
                    continuation.resume(null)
                }
                
                input.click()
                
            } catch (e: Exception) {
                continuation.resume(null)
            }
        }
    }
    
    override fun bytesToImageBitmap(bytes: ByteArray): ImageBitmap? {
        return try {
            val skiaImage = Image.makeFromEncoded(bytes)
            skiaImage.toComposeImageBitmap()
        } catch (e: Exception) {
            console.error("Error converting bytes to ImageBitmap:", e)
            null
        }
    }
}

/**
 * 创建Web平台的FileUtils实例
 */
actual fun createFileUtils(): FileUtils {
    return WebFileUtils()
}