package ovo.sypw.bsp.domain.repository

import kotlinx.coroutines.flow.Flow
import ovo.sypw.bsp.data.dto.FileUploadResponse
import ovo.sypw.bsp.data.dto.result.NetworkResult

/**
 * 文件上传仓库接口
 * 定义文件上传相关的数据访问方法
 */
interface FileUploadRepository : BaseRepository {

    /**
     * 上传文件
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @param mimeType 文件MIME类型
     * @return 上传结果Flow
     */
    fun uploadFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String = "application/octet-stream"
    ): Flow<NetworkResult<FileUploadResponse>>

    /**
     * 上传图片文件
     * @param imageBytes 图片字节数组
     * @param fileName 文件名
     * @param quality 图片质量 (0-100)
     * @return 上传结果Flow
     */
    fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        quality: Int = 85
    ): Flow<NetworkResult<FileUploadResponse>>

    /**
     * 批量上传文件
     * @param files 文件列表，每个元素包含文件字节数组、文件名和MIME类型
     * @return 上传结果Flow列表
     */
    fun uploadMultipleFiles(
        files: List<Triple<ByteArray, String, String>>
    ): Flow<List<NetworkResult<FileUploadResponse>>>

    /**
     * 检查文件大小是否符合要求
     * @param fileSize 文件大小（字节）
     * @param maxSize 最大允许大小（字节），默认10MB
     * @return 是否符合要求
     */
    fun validateFileSize(fileSize: Long, maxSize: Long = 10 * 1024 * 1024): Boolean

    /**
     * 检查文件类型是否支持
     * @param fileName 文件名
     * @param allowedExtensions 允许的文件扩展名列表
     * @return 是否支持
     */
    fun validateFileType(
        fileName: String,
        allowedExtensions: List<String> = listOf("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx")
    ): Boolean
}