package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 文件上传响应数据类
 * 匹配API返回的文件上传结果格式
 */
@Serializable
data class FileUploadResponse(
    /**
     * 上传后的文件URL
     */
    val fileUrl: String
)

/**
 * 文件上传请求参数
 * 用于封装文件上传的相关信息
 */
data class FileUploadRequest(
    /**
     * 文件字节数组
     */
    val fileBytes: ByteArray,
    
    /**
     * 文件名
     */
    val fileName: String,
    
    /**
     * 文件MIME类型
     */
    val mimeType: String = "application/octet-stream"
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FileUploadRequest

        if (!fileBytes.contentEquals(other.fileBytes)) return false
        if (fileName != other.fileName) return false
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileBytes.contentHashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + mimeType.hashCode()
        return result
    }
}

/**
 * 图片上传请求参数
 * 专门用于图片文件上传
 */
data class ImageUploadRequest(
    /**
     * 图片字节数组
     */
    val imageBytes: ByteArray,
    
    /**
     * 图片文件名
     */
    val fileName: String,
    
    /**
     * 图片质量 (0-100)
     */
    val quality: Int = 85
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ImageUploadRequest

        if (!imageBytes.contentEquals(other.imageBytes)) return false
        if (fileName != other.fileName) return false
        if (quality != other.quality) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageBytes.contentHashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + quality
        return result
    }
}