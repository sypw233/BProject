package ovo.sypw.bsp.presentation.viewmodel

import com.hoc081098.kmp.viewmodel.ViewModel
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray
import ovo.sypw.bsp.data.api.HttpClientConfig
import ovo.sypw.bsp.data.dto.NetdiskFile
import ovo.sypw.bsp.data.dto.NetdiskFileQueryParams
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.NetdiskRepository
import ovo.sypw.bsp.utils.file.FileUtils

/**
 * 网盘管理页面的ViewModel
 * 负责处理文件上传、下载、删除、重命名等操作
 */
class NetdiskViewModel(
    private val repository: NetdiskRepository,
    private val fileUtils: FileUtils
) : ViewModel() {

    // 私有可变状态
    private val _uiState = MutableStateFlow(NetdiskUiState())

    // 公开只读状态
    val uiState: StateFlow<NetdiskUiState> = _uiState.asStateFlow()

    init {
        // 初始化时加载文件列表
        loadFileList()
    }

    /**
     * 加载文件列表
     */
    fun loadFileList(
        page: Int = 1,
        size: Int = 20,
        fileType: String? = null,
        keyword: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val queryParams = NetdiskFileQueryParams(
                    page = page,
                    size = size,
                    fileType = fileType,
                    fileName = keyword
                )

                repository.getFileList(queryParams).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            val pageResponse = result.data
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                fileList = pageResponse.records,
                                currentPage = pageResponse.current,
                                totalFiles = pageResponse.total,
                                error = null
                            )
                        }

                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "加载文件列表失败: ${result.message}"
                            )
                        }

                        is NetworkResult.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }

                        NetworkResult.Idle -> TODO()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载文件列表异常: ${e.message}"
                )
            }
        }
    }

    /**
     * 上传文件
     */
    fun uploadFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true, error = null)

            try {
                repository.uploadFile(fileBytes, fileName, mimeType).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                uploadResult = "文件上传成功: ${fileName}",
                                error = null
                            )
                            // 重新加载文件列表
                            loadFileList()
                        }

                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                error = "文件上传失败: ${result.message}"
                            )
                        }

                        is NetworkResult.Loading -> {
                            _uiState.value = _uiState.value.copy(isUploading = true)
                        }

                        NetworkResult.Idle -> TODO()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    error = "文件上传异常: ${e.message}"
                )
            }
        }
    }

    /**
     * 删除文件
     */
    fun deleteFile(fileId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                repository.deleteFile(fileId).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                operationResult = "文件删除成功",
                                error = null
                            )
                            // 重新加载文件列表
                            loadFileList()
                        }

                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "文件删除失败: ${result.message}"
                            )
                        }

                        is NetworkResult.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }

                        NetworkResult.Idle -> TODO()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "文件删除异常: ${e.message}"
                )
            }
        }
    }

    /**
     * 批量删除文件
     */
    fun deleteFiles(fileIds: List<Int>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                repository.deleteFiles(fileIds).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                operationResult = "批量删除成功",
                                error = null
                            )
                            // 重新加载文件列表
                            loadFileList()
                        }

                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "批量删除失败: ${result.message}"
                            )
                        }

                        is NetworkResult.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }

                        NetworkResult.Idle -> TODO()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "批量删除异常: ${e.message}"
                )
            }
        }
    }

    /**
     * 重命名文件
     */
    fun renameFile(fileId: Int, newName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                repository.updateFileName(fileId, newName).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                operationResult = "文件重命名成功",
                                error = null
                            )
                            // 重新加载文件列表
                            loadFileList()
                        }

                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "文件重命名失败: ${result.message}"
                            )
                        }

                        is NetworkResult.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }

                        NetworkResult.Idle -> TODO()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "文件重命名异常: ${e.message}"
                )
            }
        }
    }

    /**
     * 搜索文件
     */
    fun searchFiles(keyword: String) {
        if (keyword.isBlank()) {
            loadFileList()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                repository.searchFiles(keyword).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                fileList = result.data.records,
                                searchKeyword = keyword,
                                error = null
                            )
                        }

                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "搜索文件失败: ${result.message}"
                            )
                        }

                        is NetworkResult.Loading -> {
                            _uiState.value = _uiState.value.copy(isLoading = true)
                        }

                        NetworkResult.Idle -> TODO()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "搜索文件异常: ${e.message}"
                )
            }
        }
    }


    /**
     * 切换选择模式
     */
    fun toggleSelectionMode() {
        _uiState.value = _uiState.value.copy(
            isSelectionMode = !_uiState.value.isSelectionMode,
            selectedFiles = emptySet()
        )
    }

    /**
     * 选择/取消选择文件
     */
    fun toggleFileSelection(fileId: Int) {
        val currentSelected = _uiState.value.selectedFiles
        val newSelected = if (currentSelected.contains(fileId)) {
            currentSelected - fileId
        } else {
            currentSelected + fileId
        }
        _uiState.value = _uiState.value.copy(selectedFiles = newSelected)
    }

    /**
     * 全选/取消全选
     */
    fun toggleSelectAll() {
        val allFileIds = _uiState.value.fileList.map { it.id }.toSet()
        val isAllSelected = _uiState.value.selectedFiles.containsAll(allFileIds)

        _uiState.value = _uiState.value.copy(
            selectedFiles = if (isAllSelected) emptySet() else allFileIds
        )
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * 清除操作结果
     */
    fun clearOperationResult() {
        _uiState.value = _uiState.value.copy(
            operationResult = null,
            uploadResult = null
        )
    }

    /**
     * 选择并上传文件
     * 使用FileUtils选择文件并上传到网盘，保持原始文件名
     */
    fun selectAndUploadFile() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isUploading = true, error = null)

                // 选择文件
                val selectedFile = fileUtils.selectFile()
                if (selectedFile != null) {
                    // 获取原始文件名
                    val fileName = selectedFile.name

                    // 读取文件字节数组
                    val fileBytes = fileUtils.readBytes(selectedFile)

                    // 调用上传方法
                    uploadFileWithBytes(fileBytes, fileName)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        uploadResult = "文件选择已取消"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    error = "文件选择失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 使用字节数组上传文件
     */
    private fun uploadFileWithBytes(fileBytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            try {
                repository.uploadFile(fileBytes, fileName).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                uploadResult = "文件上传成功: $fileName"
                            )
                            // 刷新文件列表
                            loadFileList()
                        }

                        is NetworkResult.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                error = "文件上传失败: ${result.message}"
                            )
                        }

                        is NetworkResult.Loading -> {
                            _uiState.value = _uiState.value.copy(isUploading = true)
                        }

                        NetworkResult.Idle -> TODO()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    error = "文件上传异常: ${e.message}"
                )
            }
        }
    }

    /**
     * 获取文件下载URL
     */
    fun getDownloadUrl(file: NetdiskFile): String? {
        return file.fileUrl
    }

    /**
     * 下载文件并保存到本地
     * @param file 要下载的文件
     * @param fileUtils 文件工具实例
     * @param onProgress 下载进度回调
     * @return 是否下载成功
     */
    suspend fun downloadAndSaveFile(
        file: NetdiskFile,
        fileUtils: FileUtils,
        onProgress: (Float) -> Unit
    ): Boolean {
        return try {
            val downloadUrl = file.fileUrl
            if (downloadUrl.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    error = "文件下载链接无效"
                )
                return false
            }

            // 使用HttpClient下载文件
            val httpClient = HttpClientConfig.createHttpClient()
            val response = httpClient.get(downloadUrl)
            val channel = response.bodyAsChannel()
            val contentLength = response.headers["Content-Length"]?.toLongOrNull() ?: 0L

            // 读取文件数据
            val data = if (contentLength > 0) {
                // 如果知道文件大小，可以显示进度
                val buffer = ByteArray(8192)
                val result = mutableListOf<Byte>()
                var totalRead = 0L

                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(buffer.size.toLong())
                    if (packet.exhausted()) break

                    val bytes = packet.readByteArray()
                    result.addAll(bytes.toList())
                    totalRead += bytes.size

                    // 更新进度
                    val progress = if (contentLength > 0) {
                        (totalRead.toFloat() / contentLength.toFloat()).coerceIn(0f, 1f)
                    } else {
                        0f
                    }
                    onProgress(progress)
                }

                result.toByteArray()
            } else {
                // 如果不知道文件大小，直接读取全部
                onProgress(0.5f) // 显示50%进度
                channel.readRemaining().readByteArray()
            }

            onProgress(0.9f) // 下载完成，准备保存

            // 获取文件扩展名
            val extension = file.fileName.substringAfterLast('.', "")
            val fileName = file.fileName.substringBeforeLast('.', "")
            // 保存文件
            val savedFile = fileUtils.saveFile(
                data = data,
                fileName = fileName,
                extension = extension
            )

            // 关闭httpClient
            httpClient.close()

            onProgress(1.0f) // 保存完成

            if (savedFile != null) {
                _uiState.value = _uiState.value.copy(
                    operationResult = "文件 ${file.fileName} 下载成功"
                )
                true
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "文件保存失败"
                )
                false
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "下载失败: ${e.message}"
            )
            false
        }
    }

    /**
     * 下载文件（返回下载URL供UI层处理）
     */
    fun downloadFile(file: NetdiskFile): String? {
        val downloadUrl = file.fileUrl
        _uiState.value = _uiState.value.copy(
            operationResult = "文件下载链接: ${file.fileUrl}"
        )
        return downloadUrl
    }

    /**
     * 刷新文件列表
     */
    fun refresh() {
        loadFileList()
    }
}

/**
 * 网盘管理页面的UI状态
 */
data class NetdiskUiState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val fileList: List<NetdiskFile> = emptyList(),
    val currentPage: Int = 1,
    val totalFiles: Int = 0,

    val searchKeyword: String? = null,
    val isSelectionMode: Boolean = false,
    val selectedFiles: Set<Int> = emptySet(),
    val uploadResult: String? = null,
    val operationResult: String? = null,
    val error: String? = null
)