package ovo.sypw.bsp.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hoc081098.kmp.viewmodel.koin.compose.koinKmpViewModel
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.NetdiskFile
import ovo.sypw.bsp.presentation.viewmodel.NetdiskViewModel
import ovo.sypw.bsp.utils.file.FileUploadUtils.formatDateTime
import ovo.sypw.bsp.utils.file.FileUploadUtils.formatFileSize
import ovo.sypw.bsp.utils.file.rememberFileUtils

/**
 * 网盘管理页面
 * 提供文件上传、下载、删除、重命名等功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetdiskScreen(
    modifier: Modifier = Modifier,
    viewModel: NetdiskViewModel = koinKmpViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 文件重命名对话框状态
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameFileId by remember { mutableStateOf(0) }
    var renameFileName by remember { mutableStateOf("") }

    // 搜索状态
    var searchText by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // 下载状态
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var downloadFileName by remember { mutableStateOf("") }

    // 文件工具实例
    val fileUtils = rememberFileUtils()

    // 显示错误信息
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Short
                )
            }
            viewModel.clearError()
        }
    }

    // 显示操作结果
    LaunchedEffect(uiState.operationResult, uiState.uploadResult) {
        uiState.operationResult?.let { result ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = result,
                    duration = SnackbarDuration.Short
                )
            }
            viewModel.clearOperationResult()
        }
        uiState.uploadResult?.let { result ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = result,
                    duration = SnackbarDuration.Short
                )
            }
            viewModel.clearOperationResult()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            NetdiskTopBar(
                searchText = searchText,
                isSearchActive = isSearchActive,
                isSelectionMode = uiState.isSelectionMode,
                selectedCount = uiState.selectedFiles.size,
                onSearchTextChange = { searchText = it },
                onSearchActiveChange = { isSearchActive = it },
                onSearch = { viewModel.searchFiles(searchText) },
                onToggleSelectionMode = { viewModel.toggleSelectionMode() },
                onDeleteSelected = {
                    if (uiState.selectedFiles.isNotEmpty()) {
                        viewModel.deleteFiles(uiState.selectedFiles.toList())
                    }
                },
                onSelectAll = { viewModel.toggleSelectAll() },
                onRefresh = { viewModel.refresh() }
            )
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        // 调用ViewModel的文件上传功能
                        viewModel.selectAndUploadFile()
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    if (uiState.isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Add, contentDescription = "上传文件")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 文件列表
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.fileList,
                        key = { it.id }
                    ) { file ->
                        FileItem(
                            file = file,
                            isSelected = uiState.selectedFiles.contains(file.id),
                            isSelectionMode = uiState.isSelectionMode,
                            onFileClick = {
                                if (uiState.isSelectionMode) {
                                    viewModel.toggleFileSelection(file.id)
                                } else {
                                    // 点击文件时直接下载并保存
                                    if (!isDownloading) {
                                        downloadFileName = file.fileName
                                        isDownloading = true
                                        scope.launch {
                                            try {
                                                val success = viewModel.downloadAndSaveFile(
                                                    file,
                                                    fileUtils
                                                ) { progress ->
                                                    downloadProgress = progress
                                                }
                                                if (success) {
                                                    snackbarHostState.showSnackbar(
                                                        message = "文件 ${file.fileName} 下载成功",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                } else {
                                                    snackbarHostState.showSnackbar(
                                                        message = "文件下载失败",
                                                        duration = SnackbarDuration.Short
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                snackbarHostState.showSnackbar(
                                                    message = "下载出错: ${e.message}",
                                                    duration = SnackbarDuration.Short
                                                )
                                            } finally {
                                                isDownloading = false
                                                downloadProgress = 0f
                                            }
                                        }
                                    }
                                }
                            },
                            onRename = {
                                renameFileId = file.id
                                renameFileName = file.fileName
                                showRenameDialog = true
                            },
                            onDelete = {
                                viewModel.deleteFile(file.id)
                            },
                            onDownload = {
                                if (!isDownloading) {
                                    downloadFileName = file.fileName
                                    isDownloading = true
                                    scope.launch {
                                        try {
                                            val success = viewModel.downloadAndSaveFile(
                                                file,
                                                fileUtils
                                            ) { progress ->
                                                downloadProgress = progress
                                            }
                                            if (success) {
                                                snackbarHostState.showSnackbar(
                                                    message = "文件 ${file.fileName} 下载成功",
                                                    duration = SnackbarDuration.Short
                                                )
                                            } else {
                                                snackbarHostState.showSnackbar(
                                                    message = "文件下载失败",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar(
                                                message = "下载出错: ${e.message}",
                                                duration = SnackbarDuration.Short
                                            )
                                        } finally {
                                            isDownloading = false
                                            downloadProgress = 0f
                                        }
                                    }
                                }
                            }
                        )
                    }

                    if (uiState.fileList.isEmpty() && !uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Folder,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = "暂无文件",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 下载进度对话框
    if (isDownloading) {
        DownloadProgressDialog(
            fileName = downloadFileName,
            progress = downloadProgress,
            onCancel = {
                isDownloading = false
                downloadProgress = 0f
            }
        )
    }

    // 重命名对话框
    if (showRenameDialog) {
        RenameDialog(
            fileName = renameFileName,
            onFileNameChange = { renameFileName = it },
            onConfirm = {
                viewModel.renameFile(renameFileId, renameFileName)
                showRenameDialog = false
            },
            onDismiss = {
                showRenameDialog = false
            }
        )
    }
}

/**
 * 下载进度对话框
 * 显示文件下载进度
 */
@Composable
private fun DownloadProgressDialog(
    fileName: String,
    progress: Float,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* 下载过程中不允许点击外部关闭 */ },
        title = {
            Text(
                text = "正在下载文件",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "文件名: $fileName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 进度条
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "下载进度: ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetdiskTopBar(
    searchText: String,
    isSearchActive: Boolean,
    isSelectionMode: Boolean,
    selectedCount: Int,
    onSearchTextChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onSearch: () -> Unit,
    onToggleSelectionMode: () -> Unit,
    onDeleteSelected: () -> Unit,
    onSelectAll: () -> Unit,
    onRefresh: () -> Unit
) {
    if (isSelectionMode) {
        // 选择模式的顶部栏
        TopAppBar(
            title = {
                Text("已选择 $selectedCount 项")
            },
            navigationIcon = {
                IconButton(onClick = onToggleSelectionMode) {
                    Icon(Icons.Default.Close, contentDescription = "退出选择模式")
                }
            },
            actions = {
                IconButton(onClick = onSelectAll) {
                    Icon(Icons.Default.SelectAll, contentDescription = "全选")
                }
                IconButton(
                    onClick = onDeleteSelected,
                    enabled = selectedCount > 0
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "删除选中")
                }
            }
        )
    } else {
        // 普通模式的顶部栏
        TopAppBar(
            title = {
                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = onSearchTextChange,
                        placeholder = { Text("搜索文件...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = onSearch) {
                                Icon(Icons.Default.Search, contentDescription = "搜索")
                            }
                        }
                    )
                } else {
                    Text("网盘管理")
                }
            },
            actions = {
                if (!isSearchActive) {
                    IconButton(onClick = { onSearchActiveChange(true) }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = onToggleSelectionMode) {
                        Icon(Icons.Default.Checklist, contentDescription = "选择模式")
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                } else {
                    IconButton(onClick = { onSearchActiveChange(false) }) {
                        Icon(Icons.Default.Close, contentDescription = "关闭搜索")
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileItem(
    file: NetdiskFile,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onFileClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFileClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 文件图标
            Icon(
                imageVector = getFileIcon(file.fileType),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 文件信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.fileName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatFileSize(file.fileSize),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatDateTime(file.updatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 选择模式下的复选框或菜单按钮
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onFileClick() }
                )
            } else {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "更多操作")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("下载") },
                            onClick = {
                                showMenu = false
                                onDownload()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Download, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("重命名") },
                            onClick = {
                                showMenu = false
                                onRename()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("删除") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RenameDialog(
    fileName: String,
    onFileNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("重命名文件") },
        text = {
            OutlinedTextField(
                value = fileName,
                onValueChange = onFileNameChange,
                label = { Text("文件名") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = fileName.isNotBlank()
            ) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 根据文件类型获取对应的图标
 */
private fun getFileIcon(fileType: String?): androidx.compose.ui.graphics.vector.ImageVector {
    return when (fileType?.lowercase()) {
        "image", "jpg", "jpeg", "png", "gif", "bmp", "webp" -> Icons.Default.Image
        "video", "mp4", "avi", "mkv", "mov", "wmv" -> Icons.Default.VideoFile
        "audio", "mp3", "wav", "flac", "aac" -> Icons.Default.AudioFile
        "pdf" -> Icons.Default.PictureAsPdf
        "doc", "docx", "txt", "rtf" -> Icons.Default.Description
        "zip", "rar", "7z", "tar", "gz" -> Icons.Default.Archive
        else -> Icons.AutoMirrored.Filled.InsertDriveFile
    }
}


/**
 * 格式化日期
 */
private fun formatDate(timestamp: Long): String {
    // 简单的日期格式化，实际项目中可以使用更完善的日期库
    return "${timestamp / 1000 / 60 / 60 / 24}天前"
}