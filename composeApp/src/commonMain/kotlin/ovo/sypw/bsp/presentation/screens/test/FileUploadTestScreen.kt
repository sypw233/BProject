package ovo.sypw.bsp.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.FileUploadResponse
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.FileUploadUseCase
import ovo.sypw.bsp.utils.FileUtils
import ovo.sypw.bsp.utils.FileUploadUtils
import ovo.sypw.bsp.utils.rememberFileUtils
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 文件上传测试界面
 * 演示文件上传功能的使用
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun FileUploadTestScreen(
    fileUploadUseCase: FileUploadUseCase,
    modifier: Modifier = Modifier
) {
    val fileUtils = rememberFileUtils()
    val scope = rememberCoroutineScope()
    
    // 状态管理
    var isUploading by remember { mutableStateOf(false) }
    var uploadResults by remember { mutableStateOf<List<Pair<String, NetworkResult<FileUploadResponse>>>>(emptyList()) }
    var selectedFiles by remember { mutableStateOf<List<Pair<String, ByteArray>>>(emptyList()) }
    var statusMessage by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        Text(
            text = "文件上传测试",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        // 文件选择区域
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "文件选择",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 选择图片按钮
                    Button(
                        onClick = {
                            if (fileUtils.isFileSelectionSupported()) {
                                scope.launch {
                                    try {
                                        val imageBytes = fileUtils.selectImage()
                                        if (imageBytes != null) {
                                            val fileName = "image_${Clock.System.now().toEpochMilliseconds()}.jpg"
                                            selectedFiles = selectedFiles + (fileName to imageBytes)
                                            statusMessage = "已选择图片: $fileName (${FileUploadUtils.formatFileSize(imageBytes.size.toLong())})"
                                        } else {
                                            statusMessage = "未选择图片"
                                        }
                                    } catch (e: Exception) {
                                        statusMessage = "选择图片失败: ${e.message}"
                                    }
                                }
                            } else {
                                statusMessage = "当前平台不支持文件选择"
                            }
                        },
                        enabled = !isUploading && fileUtils.isFileSelectionSupported()
                    ) {
                        Text("选择图片")
                    }
                    
                    // 清空选择按钮
                    OutlinedButton(
                        onClick = {
                            selectedFiles = emptyList()
                            statusMessage = "已清空选择的文件"
                        },
                        enabled = selectedFiles.isNotEmpty() && !isUploading
                    ) {
                        Text("清空选择")
                    }
                }
                
                // 显示已选择的文件
                if (selectedFiles.isNotEmpty()) {
                    Text(
                        text = "已选择 ${selectedFiles.size} 个文件:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(selectedFiles) { (fileName, fileBytes) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = fileName,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = FileUploadUtils.formatFileSize(fileBytes.size.toLong()),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // 上传控制区域
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "上传控制",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 上传按钮
                    Button(
                        onClick = {
                            if (selectedFiles.isNotEmpty()) {
                                scope.launch {
                                    isUploading = true
                                    statusMessage = "正在上传文件..."
                                    
                                    try {
                                        val results = mutableListOf<Pair<String, NetworkResult<FileUploadResponse>>>()
                                        
                                        selectedFiles.forEach { (fileName, fileBytes) ->
                                            val mimeType = fileUploadUseCase.getMimeType(fileName)
                                            
                                            fileUploadUseCase(fileBytes, fileName, mimeType).collect { result ->
                                                when (result) {
                                                    is NetworkResult.Loading -> {
                                                        statusMessage = "正在上传: $fileName"
                                                    }
                                                    is NetworkResult.Success -> {
                                                        results.add(fileName to result)
                                                        statusMessage = "上传成功: $fileName"
                                                    }
                                                    is NetworkResult.Error -> {
                                                        results.add(fileName to result)
                                                        statusMessage = "上传失败: $fileName - ${result.message}"
                                                    }

                                                    NetworkResult.Idle -> TODO()
                                                }
                                            }
                                        }
                                        
                                        uploadResults = results
                                        statusMessage = "上传完成，成功 ${results.count { it.second is NetworkResult.Success }} 个，失败 ${results.count { it.second is NetworkResult.Error }} 个"
                                        
                                    } catch (e: Exception) {
                                        statusMessage = "上传过程中发生错误: ${e.message}"
                                    } finally {
                                        isUploading = false
                                    }
                                }
                            } else {
                                statusMessage = "请先选择要上传的文件"
                            }
                        },
                        enabled = selectedFiles.isNotEmpty() && !isUploading
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (isUploading) "上传中..." else "开始上传")
                    }
                    
                    // 清空结果按钮
                    OutlinedButton(
                        onClick = {
                            uploadResults = emptyList()
                            statusMessage = "已清空上传结果"
                        },
                        enabled = uploadResults.isNotEmpty() && !isUploading
                    ) {
                        Text("清空结果")
                    }
                }
                
                // 状态消息
                if (statusMessage.isNotEmpty()) {
                    Text(
                        text = statusMessage,
                        fontSize = 14.sp,
                        color = if (statusMessage.contains("失败") || statusMessage.contains("错误")) {
                            MaterialTheme.colorScheme.error
                        } else if (statusMessage.contains("成功")) {
                            Color(0xFF4CAF50)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
        
        // 上传结果区域
        if (uploadResults.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "上传结果",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uploadResults) { (fileName, result) ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (result) {
                                        is NetworkResult.Success -> Color(0xFFE8F5E8)
                                        is NetworkResult.Error -> Color(0xFFFFEBEE)
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = fileName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    
                                    when (result) {
                                        is NetworkResult.Success -> {
                                            Text(
                                                text = "✓ 上传成功",
                                                fontSize = 12.sp,
                                                color = Color(0xFF4CAF50)
                                            )
                                            Text(
                                                text = "文件URL: ${result.data.fileUrl}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        is NetworkResult.Error -> {
                                            Text(
                                                text = "✗ 上传失败",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            Text(
                                                text = "错误: ${result.message}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        is NetworkResult.Loading -> {
                                            Text(
                                                text = "⏳ 上传中...",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        NetworkResult.Idle -> TODO()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // 使用说明
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "使用说明",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                val instructions = listOf(
                    "1. 点击'选择图片'按钮选择要上传的图片文件",
                    "2. 可以选择多个文件进行批量上传",
                    "3. 点击'开始上传'按钮开始上传选中的文件",
                    "4. 上传结果会显示在下方，包括成功和失败的详细信息",
                    "5. 支持的图片格式: JPG, PNG, GIF, WebP, BMP",
                    "6. 单个图片文件大小限制: 5MB"
                )
                
                instructions.forEach { instruction ->
                    Text(
                        text = instruction,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}