package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 网盘文件信息数据类
 * 匹配API返回的文件信息格式
 */
@Serializable
data class NetdiskFile(
    /**
     * 文件ID
     */
    val id: Int,

    /**
     * 用户ID
     */
    val userId: Int,

    /**
     * 用户名
     */
    val username: String,
    /**
     * 文件名
     */
    val fileName: String,

    /**
     * 文件大小（字节）
     */
    val fileSize: Long,

    /**
     * 文件类型/扩展名
     */
    val fileType: String,

    /**
     * 文件访问URL
     */
    val fileUrl: String,

    /**
     * 创建时间
     */
    val createdAt: String,

    /**
     * 更新时间
     */
    val updatedAt: String
)

/**
 * 网盘文件列表分页响应数据类
 * 匹配API返回的分页数据格式
 */
@Serializable
data class NetdiskFilePageResponse(
    /**
     * 文件记录列表
     */
    val records: List<NetdiskFile>,

    /**
     * 总记录数
     */
    val total: Int,

    /**
     * 每页大小
     */
    val size: Int,

    /**
     * 当前页码
     */
    val current: Int,

    /**
     * 总页数
     */
    val pages: Int
)

/**
 * 网盘文件上传请求参数
 * 用于封装文件上传的相关信息
 */
data class NetdiskFileUploadRequest(
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

        other as NetdiskFileUploadRequest

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
 * 网盘文件名修改请求参数
 * 用于修改文件名的请求
 */
@Serializable
data class NetdiskFileUpdateRequest(
    /**
     * 新文件名
     */
    val fileName: String
)

/**
 * 网盘文件查询参数
 * 用于文件列表查询的过滤条件
 */
data class NetdiskFileQueryParams(
    /**
     * 页码（从1开始）
     */
    val page: Int = 1,

    /**
     * 每页大小
     */
    val size: Int = 10,

    /**
     * 文件名过滤（可选）
     */
    val fileName: String? = null,

    /**
     * 文件类型过滤（可选）
     */
    val fileType: String? = null
)

/**
 * 网盘文件操作结果
 * 用于封装文件操作的结果状态
 */
sealed class NetdiskFileOperationResult {
    /**
     * 操作成功
     */
    data class Success(val message: String = "操作成功") : NetdiskFileOperationResult()

    /**
     * 操作失败
     */
    data class Error(val message: String, val exception: Throwable? = null) :
        NetdiskFileOperationResult()

    /**
     * 操作进行中
     */
    data object Loading : NetdiskFileOperationResult()
}