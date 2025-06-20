package ovo.sypw.bsp.utils.file

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.launch

/**
 * FileKit使用示例
 * 展示如何使用基于FileKit的新FileUtils API
 */
@Composable
fun FileKitExample() {
    val fileUtils = rememberFileUtils()
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf("准备就绪") }
    var isLoading by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FileKit 功能演示",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "状态信息",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "文件选择支持: ${if (fileUtils.isFileSelectionSupported()) "✅ 支持" else "❌ 不支持"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        // 图片选择功能
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "图片操作",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                message = "正在选择图片..."
                                val imageFile = fileUtils.selectImage()
                                if (imageFile != null) {
                                    message = "选择了图片: ${imageFile.name}, 大小: ${imageFile.getSize()} 字节"
                                    
                                    // 转换为ImageBitmap
                                    val imageBitmap = fileUtils.fileToImageBitmap(imageFile)
                                    if (imageBitmap != null) {
                                        message += "\n图片转换成功: ${imageBitmap.width}x${imageBitmap.height}"
                                    }
                                } else {
                                    message = "取消选择图片"
                                }
                            } catch (e: Exception) {
                                message = "选择图片失败: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && fileUtils.isFileSelectionSupported(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("选择图片")
                }
                
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                message = "正在选择图片（兼容模式）..."
                                val imageBytes = fileUtils.selectImageBytes()
                                if (imageBytes != null) {
                                    message = "选择了图片: ${imageBytes.size} 字节"
                                    
                                    // 转换为ImageBitmap
                                    val imageBitmap = fileUtils.bytesToImageBitmap(imageBytes)
                                    if (imageBitmap != null) {
                                        message += "\n图片转换成功: ${imageBitmap.width}x${imageBitmap.height}"
                                    }
                                } else {
                                    message = "取消选择图片"
                                }
                            } catch (e: Exception) {
                                message = "选择图片失败: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && fileUtils.isFileSelectionSupported(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("选择图片（兼容模式）")
                }
            }
        }
        
        // 文件选择功能
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "文件操作",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                message = "正在选择单个文件..."
                                val file = fileUtils.selectFile()
                                if (file != null) {
                                    message = "选择了文件: ${file.name}, 大小: ${file.getSize()} 字节"
                                } else {
                                    message = "取消选择文件"
                                }
                            } catch (e: Exception) {
                                message = "选择文件失败: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && fileUtils.isFileSelectionSupported(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("选择单个文件")
                }


            }
        }
        
        // 文件保存功能
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "文件保存",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                message = "正在保存文本文件..."
                                val testData = "Hello, FileKit!\n这是一个测试文件。\n当前时间: ${kotlinx.datetime.Clock.System.now()}"
                                val savedFile = fileUtils.saveFile(
                                    data = testData.toByteArray(),
                                    fileName = "filekit_test",
                                    extension = "txt"
                                )
                                if (savedFile != null) {
                                    message = "文件保存成功: ${savedFile.name}"
                                } else {
                                    message = "取消保存文件"
                                }
                            } catch (e: Exception) {
                                message = "保存文件失败: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && fileUtils.isFileSelectionSupported(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("保存文本文件")
                }
                
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                message = "正在保存文件（兼容模式）..."
                                val testData = "Hello, FileKit Compatibility Mode!"
                                val success = fileUtils.saveFileCompat(
                                    data = testData.toByteArray(),
                                    fileName = "filekit_compat_test.txt",
                                    mimeType = "text/plain"
                                )
                                message = if (success) {
                                    "文件保存成功（兼容模式）"
                                } else {
                                    "文件保存失败或取消"
                                }
                            } catch (e: Exception) {
                                message = "保存文件失败: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && fileUtils.isFileSelectionSupported(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("保存文件（兼容模式）")
                }
            }
        }
    }
}