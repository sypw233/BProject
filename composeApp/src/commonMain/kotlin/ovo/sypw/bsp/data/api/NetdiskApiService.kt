package ovo.sypw.bsp.data.api

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import ovo.sypw.bsp.data.dto.NetdiskFileUpdateRequest
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.utils.Logger

/**
 * 网盘管理API服务
 * 提供网盘文件管理相关的所有API调用方法
 */
class NetdiskApiService : BaseApiService() {

    companion object {
        private const val TAG = "NetdiskApiService"

        // API端点常量
        private const val UPLOAD_ENDPOINT = "/netdisk/upload"
        private const val FILES_ENDPOINT = "/netdisk/files"
        private const val FILE_DETAIL_ENDPOINT = "/netdisk/files"
    }

    /**
     * 上传文件到网盘
     * @param token 认证令牌
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @param mimeType 文件MIME类型
     * @return 上传结果
     */
    suspend fun uploadFile(
        token: String,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String = "application/octet-stream"
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "上传文件: $fileName, 大小: ${fileBytes.size} bytes")

        return try {
            val formData = MultiPartFormDataContent(
                formData {
                    append(
                        "file",
                        fileBytes,
                        Headers.build {
                            append(HttpHeaders.ContentType, mimeType)
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        }
                    )
                }
            )

            postWithToken(
                endpoint = UPLOAD_ENDPOINT,
                body = formData,
                token = token
            )
        } catch (e: Exception) {
            Logger.e(TAG, "文件上传失败", e)
            NetworkResult.Error(
                exception = e,
                message = "文件上传失败: ${e.message}"
            )
        }
    }

    /**
     * 获取文件列表
     * @param token 认证令牌
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param fileName 文件名过滤（可选）
     * @param fileType 文件类型过滤（可选）
     * @return 文件列表响应
     */
    suspend fun getFileList(
        token: String,
        page: Int = 1,
        size: Int = 10,
        fileName: String? = null,
        fileType: String? = null
    ): NetworkResult<SaResult> {
        Logger.d(
            TAG,
            "获取文件列表: page=$page, size=$size, fileName=$fileName, fileType=$fileType"
        )

        val parameters = mutableMapOf<String, Any>(
            "page" to page,
            "size" to size
        )

        // 添加可选参数
        fileName?.let { parameters["fileName"] = it }
        fileType?.let { parameters["fileType"] = it }

        return getWithToken(
            endpoint = FILES_ENDPOINT,
            parameters = parameters,
            token = token
        )
    }

    /**
     * 获取文件详情
     * @param token 认证令牌
     * @param fileId 文件ID
     * @return 文件详情响应
     */
    suspend fun getFileDetail(
        token: String,
        fileId: Int
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取文件详情: fileId=$fileId")

        return getWithToken(
            endpoint = "$FILE_DETAIL_ENDPOINT/$fileId",
            token = token
        )
    }

    /**
     * 修改文件名
     * @param token 认证令牌
     * @param fileId 文件ID
     * @param newFileName 新文件名
     * @return 修改结果
     */
    suspend fun updateFileName(
        token: String,
        fileId: Int,
        newFileName: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "修改文件名: fileId=$fileId, newFileName=$newFileName")

        val updateRequest = NetdiskFileUpdateRequest(fileName = newFileName)

        return putWithToken(
            endpoint = "$FILE_DETAIL_ENDPOINT/$fileId",
            body = updateRequest,
            token = token
        )
    }

    /**
     * 删除文件
     * @param token 认证令牌
     * @param fileId 文件ID
     * @return 删除结果
     */
    suspend fun deleteFile(
        token: String,
        fileId: Int
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "删除文件: fileId=$fileId")

        return deleteWithToken(
            endpoint = "$FILE_DETAIL_ENDPOINT/$fileId",
            token = token
        )
    }

    /**
     * 批量删除文件
     * @param token 认证令牌
     * @param fileIds 文件ID列表
     * @return 批量删除结果
     */
    suspend fun deleteFiles(
        token: String,
        fileIds: List<Int>
    ): NetworkResult<List<NetworkResult<SaResult>>> {
        Logger.d(TAG, "批量删除文件: fileIds=$fileIds")

        return try {
            val results = fileIds.map { fileId ->
                deleteFile(token, fileId)
            }
            NetworkResult.Success(results)
        } catch (e: Exception) {
            Logger.e(TAG, "批量删除文件失败", e)
            NetworkResult.Error(
                exception = e,
                message = "批量删除文件失败: ${e.message}"
            )
        }
    }
}