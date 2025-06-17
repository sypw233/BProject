package ovo.sypw.bsp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ovo.sypw.bsp.data.api.FileUploadApiService
import ovo.sypw.bsp.data.dto.FileUploadResponse
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.isSuccess
import ovo.sypw.bsp.data.dto.result.parseData
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.repository.FileUploadRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 文件上传仓库实现类
 * 整合网络API和本地存储，提供完整的文件上传功能
 */
class FileUploadRepositoryImpl(
    private val fileUploadApiService: FileUploadApiService,
    private val tokenStorage: TokenStorage
) : FileUploadRepository {

    /**
     * 上传文件
     */
    override fun uploadFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): Flow<NetworkResult<FileUploadResponse>> = performNetworkCall {
        val token = tokenStorage.getAccessToken()
        if (token.isNullOrEmpty()) {
            Logger.w("FileUploadRepository", "Token为空，无法上传文件")
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录后再上传文件"
            )
        }

        // 验证文件大小
        if (!validateFileSize(fileBytes.size.toLong())) {
            Logger.w("FileUploadRepository", "文件大小超出限制: ${fileBytes.size} bytes")
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("文件过大"),
                message = "文件大小不能超过10MB"
            )
        }

        // 验证文件类型
        if (!validateFileType(fileName)) {
            Logger.w("FileUploadRepository", "不支持的文件类型: $fileName")
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("文件类型不支持"),
                message = "不支持的文件类型"
            )
        }

        return@performNetworkCall when (val result =
            fileUploadApiService.uploadFile(token, fileBytes, fileName)) {
            is NetworkResult.Success -> {
                Logger.i("FileUploadRepository", "文件上传请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val uploadResponse = saResult.parseData<FileUploadResponse>()
                    if (uploadResponse != null) {
                        Logger.i("FileUploadRepository", "文件上传成功: ${uploadResponse.fileUrl}")
                        NetworkResult.Success(uploadResponse)
                    } else {
                        Logger.w("FileUploadRepository", "文件上传响应数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "文件上传响应数据解析失败"
                        )
                    }
                } else {
                    Logger.w("FileUploadRepository", "文件上传失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e("FileUploadRepository", "文件上传网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> {
                Logger.d("FileUploadRepository", "文件上传中...")
                result
            }

            else -> {
                NetworkResult.Error(
                    exception = Exception("未知错误"),
                    message = "未知的网络结果类型"
                )
            }
        }
    }

    /**
     * 上传图片文件
     */
    override fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        quality: Int
    ): Flow<NetworkResult<FileUploadResponse>> = performNetworkCall {
        val token = tokenStorage.getAccessToken()
        if (token.isNullOrEmpty()) {
            Logger.w("FileUploadRepository", "Token为空，无法上传图片")
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录后再上传图片"
            )
        }

        // 验证图片文件类型
        val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "webp")
        if (!validateFileType(fileName, imageExtensions)) {
            Logger.w("FileUploadRepository", "不支持的图片类型: $fileName")
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("图片类型不支持"),
                message = "仅支持 JPG、PNG、GIF、WebP 格式的图片"
            )
        }

        // 验证图片大小
        if (!validateFileSize(imageBytes.size.toLong(), 5 * 1024 * 1024)) { // 图片限制5MB
            Logger.w("FileUploadRepository", "图片大小超出限制: ${imageBytes.size} bytes")
            return@performNetworkCall NetworkResult.Error(
                exception = Exception("图片过大"),
                message = "图片大小不能超过5MB"
            )
        }

        return@performNetworkCall when (val result =
            fileUploadApiService.uploadImage(token, imageBytes, fileName)) {
            is NetworkResult.Success -> {
                Logger.i("FileUploadRepository", "图片上传请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val uploadResponse = saResult.parseData<FileUploadResponse>()
                    if (uploadResponse != null) {
                        Logger.i("FileUploadRepository", "图片上传成功: ${uploadResponse.fileUrl}")
                        NetworkResult.Success(uploadResponse)
                    } else {
                        Logger.w("FileUploadRepository", "图片上传响应数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "图片上传响应数据解析失败"
                        )
                    }
                } else {
                    Logger.w("FileUploadRepository", "图片上传失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e("FileUploadRepository", "图片上传网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> {
                Logger.d("FileUploadRepository", "图片上传中...")
                result
            }

            else -> {
                NetworkResult.Error(
                    exception = Exception("未知错误"),
                    message = "未知的网络结果类型"
                )
            }
        }
    }

    /**
     * 批量上传文件
     */
    override fun uploadMultipleFiles(
        files: List<Triple<ByteArray, String, String>>
    ): Flow<List<NetworkResult<FileUploadResponse>>> = flow {
        val results = mutableListOf<NetworkResult<FileUploadResponse>>()

        files.forEach { (fileBytes, fileName, mimeType) ->
            uploadFile(fileBytes, fileName, mimeType).collect { result ->
                if (result !is NetworkResult.Loading) {
                    results.add(result)
                }
            }
        }

        emit(results)
    }

    /**
     * 检查文件大小是否符合要求
     */
    override fun validateFileSize(fileSize: Long, maxSize: Long): Boolean {
        return fileSize <= maxSize && fileSize > 0
    }

    /**
     * 检查文件类型是否支持
     */
    override fun validateFileType(
        fileName: String,
        allowedExtensions: List<String>
    ): Boolean {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extension.isNotEmpty() && allowedExtensions.contains(extension)
    }
}