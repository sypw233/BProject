package ovo.sypw.bsp.utils.file

import kotlinx.datetime.Clock
import ovo.sypw.bsp.utils.StringUtils.format
import kotlin.math.log10
import kotlin.math.pow

/**
 * 文件上传工具类
 * 提供文件上传相关的通用工具方法
 */
object FileUploadUtils {

    /**
     * 支持的图片文件扩展名
     */
    val SUPPORTED_IMAGE_EXTENSIONS = listOf(
        "jpg", "jpeg", "png", "gif", "webp", "bmp"
    )

    /**
     * 支持的文档文件扩展名
     */
    val SUPPORTED_DOCUMENT_EXTENSIONS = listOf(
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
    )

    /**
     * 支持的压缩文件扩展名
     */
    val SUPPORTED_ARCHIVE_EXTENSIONS = listOf(
        "zip", "rar", "7z", "tar", "gz"
    )

    /**
     * 所有支持的文件扩展名
     */
    val ALL_SUPPORTED_EXTENSIONS = SUPPORTED_IMAGE_EXTENSIONS +
            SUPPORTED_DOCUMENT_EXTENSIONS +
            SUPPORTED_ARCHIVE_EXTENSIONS

    /**
     * 默认最大文件大小 (10MB)
     */
    const val DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024L

    /**
     * 默认最大图片大小 (5MB)
     */
    const val DEFAULT_MAX_IMAGE_SIZE = 5 * 1024 * 1024L

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 文件扩展名（小写）
     */
    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "").lowercase()
    }

    /**
     * 检查是否为图片文件
     * @param fileName 文件名
     * @return 是否为图片文件
     */
    fun isImageFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName)
        return SUPPORTED_IMAGE_EXTENSIONS.contains(extension)
    }

    /**
     * 检查是否为文档文件
     * @param fileName 文件名
     * @return 是否为文档文件
     */
    fun isDocumentFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName)
        return SUPPORTED_DOCUMENT_EXTENSIONS.contains(extension)
    }

    /**
     * 检查是否为压缩文件
     * @param fileName 文件名
     * @return 是否为压缩文件
     */
    fun isArchiveFile(fileName: String): Boolean {
        val extension = getFileExtension(fileName)
        return SUPPORTED_ARCHIVE_EXTENSIONS.contains(extension)
    }

    /**
     * 检查文件类型是否支持
     * @param fileName 文件名
     * @param allowedExtensions 允许的扩展名列表，为空则使用默认支持列表
     * @return 是否支持
     */
    fun isSupportedFileType(
        fileName: String,
        allowedExtensions: List<String> = ALL_SUPPORTED_EXTENSIONS
    ): Boolean {
        val extension = getFileExtension(fileName)
        return extension.isNotEmpty() && allowedExtensions.contains(extension)
    }

    /**
     * 检查文件大小是否符合要求
     * @param fileSize 文件大小（字节）
     * @param maxSize 最大允许大小（字节）
     * @return 是否符合要求
     */
    fun isValidFileSize(fileSize: Long, maxSize: Long = DEFAULT_MAX_FILE_SIZE): Boolean {
        return fileSize > 0 && fileSize <= maxSize
    }

    /**
     * 格式化文件大小显示
     * @param sizeInBytes 文件大小（字节）
     * @return 格式化后的大小字符串
     */
    fun formatFileSize(sizeInBytes: Long): String {
        if (sizeInBytes <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups =
            (log10(sizeInBytes.toDouble()) / log10(1024.0)).toInt().coerceAtMost(units.size - 1)

        val size = sizeInBytes / 1024.0.pow(digitGroups.toDouble())
        return String.format("%.1f %s", size, units[digitGroups])
    }

    /**
     * 格式化日期时间显示
     * @param dateTimeString ISO格式的日期时间字符串
     * @return 格式化后的日期时间字符串
     */
    fun formatDateTime(dateTimeString: String): String {
        return try {
            // 如果是ISO格式，转换为更友好的显示格式
            if (dateTimeString.contains('T')) {
                val parts = dateTimeString.split('T')
                val datePart = parts[0]
                val timePart = parts.getOrNull(1)?.substringBefore('.') ?: "00:00:00"
                "$datePart $timePart"
            } else {
                dateTimeString
            }
        } catch (e: Exception) {
            dateTimeString
        }
    }

    /**
     * 格式化相对时间显示（如：2小时前、3天前）
     * @param dateTimeString ISO格式的日期时间字符串
     * @return 相对时间字符串
     */
    fun formatRelativeTime(dateTimeString: String): String {
        return try {
            Clock.System.now().toEpochMilliseconds()
            // 简单的时间解析，实际项目中建议使用kotlinx-datetime
            if (dateTimeString.contains('T')) {
                dateTimeString.replace('T', ' ').substringBefore('.')
            } else {
                dateTimeString
            }

            // 这里返回格式化的时间，实际实现需要根据具体需求调整
            formatDateTime(dateTimeString)
        } catch (e: Exception) {
            dateTimeString
        }
    }


    /**
     * 清理文件名（移除非法字符）
     * @param fileName 原始文件名
     * @return 清理后的文件名
     */
    fun sanitizeFileName(fileName: String): String {
        // 移除或替换非法字符
        val illegalChars = Regex("[<>:\"/\\|?*]")
        var cleanName = fileName.replace(illegalChars, "_")

        // 移除开头和结尾的点和空格
        cleanName = cleanName.trim().trim('.')

        // 确保文件名不为空
        if (cleanName.isEmpty()) {
            cleanName = "file_${Clock.System.now().toEpochMilliseconds()}"
        }

        // 限制文件名长度
        if (cleanName.length > 255) {
            val extension = getFileExtension(fileName)
            val maxNameLength = 255 - extension.length - 1 // -1 for the dot
            cleanName = cleanName.substring(0, maxNameLength) + "." + extension
        }

        return cleanName
    }

    /**
     * 获取文件MIME类型
     * @param fileName 文件名
     * @return MIME类型
     */
    fun getMimeType(fileName: String): String {
        val extension = getFileExtension(fileName)
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
            "ppt" -> "application/vnd.ms-powerpoint"
            "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            "txt" -> "text/plain"
            "zip" -> "application/zip"
            "rar" -> "application/x-rar-compressed"
            "7z" -> "application/x-7z-compressed"
            "tar" -> "application/x-tar"
            "gz" -> "application/gzip"
            else -> "application/octet-stream"
        }
    }

    /**
     * 验证文件名是否合法
     * @param fileName 文件名
     * @return 验证结果，null表示合法
     */
    fun validateFileName(fileName: String): String? {
        return when {
            fileName.isBlank() -> "文件名不能为空"
            fileName.length > 255 -> "文件名过长，不能超过255个字符"
            !fileName.contains('.') -> "文件名必须包含扩展名"
            fileName.startsWith('.') -> "文件名不能以点开头"
            fileName.endsWith('.') -> "文件名不能以点结尾"
            fileName.contains(Regex("[<>:\"/\\|?*]")) -> "文件名包含非法字符"
            fileName.contains("  ") -> "文件名不能包含连续空格"
            else -> null
        }
    }

    /**
     * 获取文件类型描述
     * @param fileName 文件名
     * @return 文件类型描述
     */
    fun getFileTypeDescription(fileName: String): String {
        return when {
            isImageFile(fileName) -> "图片文件"
            isDocumentFile(fileName) -> "文档文件"
            isArchiveFile(fileName) -> "压缩文件"
            else -> "其他文件"
        }
    }
}