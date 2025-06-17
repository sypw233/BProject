package ovo.sypw.bsp.domain.usecase

import kotlinx.coroutines.flow.Flow
import ovo.sypw.bsp.data.dto.FileUploadResponse
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.FileUploadRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 文件上传用例
 * 处理文件上传的业务逻辑
 */
class FileUploadUseCase(
    private val fileUploadRepository: FileUploadRepository
) {

    /**
     * 上传单个文件
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @param mimeType 文件MIME类型
     * @return 上传结果Flow
     */
    suspend operator fun invoke(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String = "application/octet-stream"
    ): Flow<NetworkResult<FileUploadResponse>> {
        // 输入验证
        val validationResult = validateInput(fileBytes, fileName)
        if (validationResult != null) {
            Logger.w("FileUploadUseCase", "输入验证失败: $validationResult")
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(
                    exception = Exception(validationResult),
                    message = validationResult
                )
            )
        }

        Logger.i("FileUploadUseCase", "开始上传文件: $fileName")
        return fileUploadRepository.uploadFile(fileBytes, fileName, mimeType)
    }

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
    ): Flow<NetworkResult<FileUploadResponse>> {
        // 输入验证
        val validationResult = validateImageInput(imageBytes, fileName, quality)
        if (validationResult != null) {
            Logger.w("FileUploadUseCase", "图片输入验证失败: $validationResult")
            return kotlinx.coroutines.flow.flowOf(
                NetworkResult.Error(
                    exception = Exception(validationResult),
                    message = validationResult
                )
            )
        }

        Logger.i("FileUploadUseCase", "开始上传图片: $fileName")
        return fileUploadRepository.uploadImage(imageBytes, fileName, quality)
    }

    /**
     * 批量上传文件
     * @param files 文件列表，每个元素包含文件字节数组、文件名和MIME类型
     * @return 上传结果Flow列表
     */
    fun uploadMultipleFiles(
        files: List<Triple<ByteArray, String, String>>
    ): Flow<List<NetworkResult<FileUploadResponse>>> {
        if (files.isEmpty()) {
            Logger.w("FileUploadUseCase", "文件列表为空")
            return kotlinx.coroutines.flow.flowOf(
                listOf(
                    NetworkResult.Error(
                        exception = Exception("文件列表为空"),
                        message = "请选择要上传的文件"
                    )
                )
            )
        }

        // 验证每个文件
        files.forEachIndexed { index, (fileBytes, fileName, _) ->
            val validationResult = validateInput(fileBytes, fileName)
            if (validationResult != null) {
                Logger.w("FileUploadUseCase", "第${index + 1}个文件验证失败: $validationResult")
                return kotlinx.coroutines.flow.flowOf(
                    listOf(
                        NetworkResult.Error(
                            exception = Exception(validationResult),
                            message = "第${index + 1}个文件: $validationResult"
                        )
                    )
                )
            }
        }

        Logger.i("FileUploadUseCase", "开始批量上传${files.size}个文件")
        return fileUploadRepository.uploadMultipleFiles(files)
    }

    /**
     * 验证文件输入
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @return 验证错误信息，null表示验证通过
     */
    private fun validateInput(
        fileBytes: ByteArray,
        fileName: String
    ): String? {
        return when {
            fileBytes.isEmpty() -> "文件内容为空"
            fileName.isBlank() -> "文件名不能为空"
            fileName.length > 255 -> "文件名过长，不能超过255个字符"
            !fileName.contains('.') -> "文件名必须包含扩展名"
            fileName.startsWith('.') -> "文件名不能以点开头"
            fileName.contains(Regex("[<>:\"/\\|?*]")) -> "文件名包含非法字符"
            else -> null
        }
    }

    /**
     * 验证图片输入
     * @param imageBytes 图片字节数组
     * @param fileName 文件名
     * @param quality 图片质量
     * @return 验证错误信息，null表示验证通过
     */
    private fun validateImageInput(
        imageBytes: ByteArray,
        fileName: String,
        quality: Int
    ): String? {
        val baseValidation = validateInput(imageBytes, fileName)
        if (baseValidation != null) {
            return baseValidation
        }

        return when {
            quality < 0 || quality > 100 -> "图片质量必须在0-100之间"
            !isImageFile(fileName) -> "文件不是支持的图片格式"
            else -> null
        }
    }

    /**
     * 检查是否为图片文件
     * @param fileName 文件名
     * @return 是否为图片文件
     */
    private fun isImageFile(fileName: String): Boolean {
        val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "webp", "bmp")
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return imageExtensions.contains(extension)
    }

    /**
     * 获取文件MIME类型
     * @param fileName 文件名
     * @return MIME类型
     */
    fun getMimeType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "bmp" -> "image/bmp"
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "txt" -> "text/plain"
            "zip" -> "application/zip"
            "rar" -> "application/x-rar-compressed"
            else -> "application/octet-stream"
        }
    }
}