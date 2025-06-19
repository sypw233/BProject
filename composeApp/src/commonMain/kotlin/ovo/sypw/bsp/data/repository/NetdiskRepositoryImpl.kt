package ovo.sypw.bsp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import ovo.sypw.bsp.data.api.NetdiskApiService
import ovo.sypw.bsp.data.dto.NetdiskFile
import ovo.sypw.bsp.data.dto.NetdiskFileOperationResult
import ovo.sypw.bsp.data.dto.NetdiskFilePageResponse
import ovo.sypw.bsp.data.dto.NetdiskFileQueryParams
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.NetworkResult.*
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.repository.BaseRepository
import ovo.sypw.bsp.domain.repository.NetdiskRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 网盘管理Repository实现类
 * 实现网盘文件管理相关的所有数据访问方法
 */
class NetdiskRepositoryImpl(
    private val netdiskApiService: NetdiskApiService,
    private val tokenStorage: TokenStorage
) : NetdiskRepository, BaseRepository {

    companion object {
        private const val TAG = "NetdiskRepositoryImpl"
    }

    /**
     * 获取认证令牌
     * @return 认证令牌，如果未登录则返回null
     */
    private suspend fun getAuthToken(): String? {
        return tokenStorage.getAccessToken()
    }

    /**
     * 处理API响应并转换为指定类型
     * @param result API响应结果
     * @param transform 转换函数
     * @return 转换后的结果
     */
    private inline fun <T, R> handleApiResponse(
        result: NetworkResult<SaResult>,
        transform: (T) -> R
    ): NetworkResult<R> {
        return when (result) {
            is NetworkResult.Success -> {
                try {
                    val data = result.data.data as? T
                    if (data != null) {
                        Success(transform(data))
                    } else {
                        Error(
                            exception = Exception("数据格式错误"),
                            message = "响应数据格式不正确"
                        )
                    }
                } catch (e: Exception) {
                    Logger.e(TAG, "数据转换失败", e)
                    Error(
                        exception = e,
                        message = "数据解析失败: ${e.message}"
                    )
                }
            }
            is NetworkResult.Error -> Error(result.exception, result.message)
            is NetworkResult.Loading -> NetworkResult.Loading
            NetworkResult.Idle -> TODO()
        }
    }

    override fun uploadFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): Flow<NetworkResult<NetdiskFileOperationResult>> = performNetworkCall {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("未找到认证令牌"),
                message = "请先登录"
            )
        }

        Logger.d(TAG, "上传文件: $fileName")
        val result = netdiskApiService.uploadFile(token, fileBytes, fileName, mimeType)
        
        when (result) {
            is Success -> {
                Logger.d(TAG, "文件上传成功: $fileName")
                Success(NetdiskFileOperationResult.Success("文件上传成功"))
            }
            is Error -> {
                Logger.e(TAG, "文件上传失败: ${result.message}")
                Error(
                    exception = result.exception,
                    message = result.message ?: "文件上传失败"
                )
            }
            is Loading -> Loading
            Idle -> TODO()
        }
    }

    override fun getFileList(
        queryParams: NetdiskFileQueryParams
    ): Flow<NetworkResult<NetdiskFilePageResponse>> = performNetworkCall {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("未找到认证令牌"),
                message = "请先登录"
            )
        }

        Logger.d(TAG, "获取文件列表: $queryParams")
        val result = netdiskApiService.getFileList(
            token = token,
            page = queryParams.page,
            size = queryParams.size,
            fileName = queryParams.fileName,
            fileType = queryParams.fileType
        )

        when (result) {
            is Success -> {
                try {
                    // 直接从SaResult的data字段解析NetdiskFilePageResponse
                    val saResult = result.data
                    val pageResponse = saResult.data?.let { dataElement ->
                        Json.decodeFromJsonElement(NetdiskFilePageResponse.serializer(), dataElement)
                    } ?: throw Exception("响应数据为空")
                    
                    Logger.d(TAG, "获取文件列表成功，共 ${pageResponse.total} 个文件")
                    Success(pageResponse)
                } catch (e: Exception) {
                    Logger.e(TAG, "文件列表数据解析失败", e)
                    Error(
                        exception = e,
                        message = "数据解析失败: ${e.message}"
                    )
                }
            }
            is Error -> {
                Logger.e(TAG, "获取文件列表失败: ${result.message}")
                Error(
                    exception = result.exception,
                    message = result.message ?: "获取文件列表失败"
                )
            }
            is Loading -> Loading
            Idle -> TODO()
        }
    }

    override fun getFileDetail(
        fileId: Int
    ): Flow<NetworkResult<NetdiskFile>> = performNetworkCall {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("未找到认证令牌"),
                message = "请先登录"
            )
        }

        Logger.d(TAG, "获取文件详情: fileId=$fileId")
        val result = netdiskApiService.getFileDetail(token, fileId)

        when (result) {
            is Success -> {
                try {
                    val fileDetail = Json.decodeFromString(NetdiskFile.serializer(), 
                        Json.encodeToString(SaResult.serializer(), result.data.data as SaResult))
                    
                    Logger.d(TAG, "获取文件详情成功: ${fileDetail.fileName}")
                    Success(fileDetail)
                } catch (e: Exception) {
                    Logger.e(TAG, "文件详情数据解析失败", e)
                    Error(
                        exception = e,
                        message = "数据解析失败: ${e.message}"
                    )
                }
            }
            is Error -> {
                Logger.e(TAG, "获取文件详情失败: ${result.message}")
                Error(
                    exception = result.exception,
                    message = result.message ?: "获取文件详情失败"
                )
            }
            is Loading -> Loading
            Idle -> TODO()
        }
    }

    override fun updateFileName(
        fileId: Int,
        newFileName: String
    ): Flow<NetworkResult<NetdiskFileOperationResult>> = performNetworkCall {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("未找到认证令牌"),
                message = "请先登录"
            )
        }

        Logger.d(TAG, "修改文件名: fileId=$fileId, newFileName=$newFileName")
        val result = netdiskApiService.updateFileName(token, fileId, newFileName)

        when (result) {
            is Success -> {
                Logger.d(TAG, "文件名修改成功")
                Success(NetdiskFileOperationResult.Success("文件名修改成功"))
            }
            is Error -> {
                Logger.e(TAG, "文件名修改失败: ${result.message}")
                Error(
                    exception = result.exception,
                    message = result.message ?: "文件名修改失败"
                )
            }
            is Loading -> Loading
            Idle -> TODO()
        }
    }

    override fun deleteFile(
        fileId: Int
    ): Flow<NetworkResult<NetdiskFileOperationResult>> = performNetworkCall {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("未找到认证令牌"),
                message = "请先登录"
            )
        }

        Logger.d(TAG, "删除文件: fileId=$fileId")
        val result = netdiskApiService.deleteFile(token, fileId)

        when (result) {
            is Success -> {
                Logger.d(TAG, "文件删除成功")
                Success(NetdiskFileOperationResult.Success("文件删除成功"))
            }
            is Error -> {
                Logger.e(TAG, "文件删除失败: ${result.message}")
                Error(
                    exception = result.exception,
                    message = result.message ?: "文件删除失败"
                )
            }
            is Loading -> Loading
            Idle -> TODO()
        }
    }

    override fun deleteFiles(
        fileIds: List<Int>
    ): Flow<NetworkResult<NetdiskFileOperationResult>> = performNetworkCall {
        val token = getAuthToken()
        if (token.isNullOrEmpty()) {
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("未找到认证令牌"),
                message = "请先登录"
            )
        }

        Logger.d(TAG, "批量删除文件: fileIds=$fileIds")
        val result = netdiskApiService.deleteFiles(token, fileIds)

        when (result) {
            is Success -> {
                val successCount = result.data.count { it is Success }
                val totalCount = result.data.size
                
                Logger.d(TAG, "批量删除完成: $successCount/$totalCount")
                Success(
                    NetdiskFileOperationResult.Success("批量删除完成: $successCount/$totalCount")
                )
            }
            is Error -> {
                Logger.e(TAG, "批量删除失败: ${result.message}")
                Error(
                    exception = result.exception,
                    message = result.message ?: "批量删除失败"
                )
            }
            is Loading -> Loading
            Idle -> TODO()
        }
    }

    override fun searchFiles(
        keyword: String,
        fileType: String?,
        page: Int,
        size: Int
    ): Flow<NetworkResult<NetdiskFilePageResponse>> {
        return getFileList(
            NetdiskFileQueryParams(
                page = page,
                size = size,
                fileName = keyword,
                fileType = fileType
            )
        )
    }

    override fun getFileTypeStatistics(): Flow<NetworkResult<Map<String, Int>>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            // 获取所有文件并统计类型
            val fileListResult = getFileList(NetdiskFileQueryParams(page = 1, size = 1000))
            fileListResult.collect { result ->
                when (result) {
                    is Success -> {
                        val statistics = result.data.records
                            .groupBy { it.fileType }
                            .mapValues { it.value.size }
                        
                        emit(Success(statistics))
                    }
                    is Error -> {
                        emit(Error(result.exception, result.message))
                    }
                    is Loading -> {
                        // 继续等待
                    }

                    Idle -> TODO()
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "获取文件类型统计失败", e)
            emit(NetworkResult.Error(e, "获取文件类型统计失败: ${e.message}"))
        }
    }

    override fun checkFileExists(
        fileName: String
    ): Flow<NetworkResult<Boolean>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            val searchResult = searchFiles(fileName, page = 1, size = 1)
            searchResult.collect { result ->
                when (result) {
                    is Success -> {
                        val exists = result.data.records.any { it.fileName == fileName }
                        emit(Success(exists))
                    }
                    is Error -> {
                        emit(Error(result.exception, result.message))
                    }
                    is Loading -> {
                        // 继续等待
                    }

                    Idle -> TODO()
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "检查文件是否存在失败", e)
            emit(NetworkResult.Error(e, "检查文件是否存在失败: ${e.message}"))
        }
    }

    override fun getStorageInfo(): Flow<NetworkResult<Map<String, Any>>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            // 获取所有文件并计算存储信息
            val fileListResult = getFileList(NetdiskFileQueryParams(page = 1, size = 1000))
            fileListResult.collect { result ->
                when (result) {
                    is Success -> {
                        val totalFiles = result.data.total
                        val totalSize = result.data.records.sumOf { it.fileSize }
                        val averageSize = if (totalFiles > 0) totalSize / totalFiles else 0
                        
                        val storageInfo = mapOf(
                            "totalFiles" to totalFiles,
                            "totalSize" to totalSize,
                            "averageSize" to averageSize,
                            "usedSpace" to totalSize,
                            "freeSpace" to (Long.MAX_VALUE - totalSize) // 模拟剩余空间
                        )
                        
                        emit(Success(storageInfo))
                    }
                    is Error -> {
                        emit(Error(result.exception, result.message))
                    }
                    is Loading -> {
                        // 继续等待
                    }

                    Idle -> TODO()
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "获取存储信息失败", e)
            emit(NetworkResult.Error(e, "获取存储信息失败: ${e.message}"))
        }
    }

    override fun uploadMultipleFiles(
        files: List<Triple<ByteArray, String, String>>
    ): Flow<NetworkResult<List<NetdiskFileOperationResult>>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            val results = mutableListOf<NetdiskFileOperationResult>()
            
            for ((fileBytes, fileName, mimeType) in files) {
                val uploadResult = uploadFile(fileBytes, fileName, mimeType)
                uploadResult.collect { result ->
                    when (result) {
                        is Success -> {
                            results.add(result.data)
                        }
                        is Error -> {
                            results.add(
                                NetdiskFileOperationResult.Error(
                                    "上传 $fileName 失败: ${result.message}",
                                    result.exception
                                )
                            )
                        }
                        is Loading -> {
                            // 继续等待
                        }

                        Idle -> TODO()
                    }
                }
            }
            
            emit(NetworkResult.Success(results))
        } catch (e: Exception) {
            Logger.e(TAG, "批量上传文件失败", e)
            emit(NetworkResult.Error(e, "批量上传文件失败: ${e.message}"))
        }
    }

    override fun downloadFile(
        fileId: Int
    ): Flow<NetworkResult<ByteArray>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            // 首先获取文件详情以获取下载URL
            val fileDetailResult = getFileDetail(fileId)
            fileDetailResult.collect { result ->
                when (result) {
                    is Success -> {
                        // 这里应该根据fileUrl下载文件内容
                        // 由于API文档中没有直接的下载接口，这里返回一个模拟的结果
                        emit(
                            Error(
                                exception = Exception("下载功能暂未实现"),
                                message = "请使用文件URL直接下载: ${result.data.fileUrl}"
                            )
                        )
                    }
                    is Error -> {
                        emit(Error(result.exception, result.message))
                    }
                    is Loading -> {
                        // 继续等待
                    }

                    Idle -> TODO()
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "下载文件失败", e)
            emit(NetworkResult.Error(e, "下载文件失败: ${e.message}"))
        }
    }

    override fun getDownloadUrl(
        fileId: Int
    ): Flow<NetworkResult<String>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            val fileDetailResult = getFileDetail(fileId)
            fileDetailResult.collect { result ->
                when (result) {
                    is Success -> {
                        emit(Success(result.data.fileUrl))
                    }
                    is Error -> {
                        emit(Error(result.exception, result.message))
                    }
                    is Loading -> {
                        // 继续等待
                    }

                    Idle -> TODO()
                }
            }
        } catch (e: Exception) {
            Logger.e(TAG, "获取下载链接失败", e)
            emit(NetworkResult.Error(e, "获取下载链接失败: ${e.message}"))
        }
    }

    override suspend fun refreshFileListCache() {
        Logger.d(TAG, "刷新文件列表缓存")
        // 这里可以实现缓存刷新逻辑
    }

    override suspend fun clearAllCache() {
        Logger.d(TAG, "清除所有缓存")
        // 这里可以实现缓存清除逻辑
    }
}