package ovo.sypw.bsp.data.api

import io.ktor.client.request.forms.*
import io.ktor.client.request.header
import io.ktor.http.*
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.utils.Logger

/**
 * 文件上传API服务
 * 提供文件上传相关的网络请求功能
 */
class FileUploadApiService : BaseApiService() {
    
    /**
     * 上传文件
     * @param token 认证令牌
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @param contentType 文件MIME类型
     * @return 上传结果
     */
    suspend fun uploadFile(
        token: String,
        fileBytes: ByteArray,
        fileName: String,
        contentType: ContentType = ContentType.Application.OctetStream
    ): NetworkResult<SaResult> {
        Logger.d("FileUploadApiService", "开始上传文件: $fileName, 大小: ${fileBytes.size} bytes")
        
        return safeApiCall {
            httpClient.submitFormWithBinaryData(
                url = NetworkConfig.getApiUrl("/files/upload"),
                formData = formData {
                    append("file", fileBytes, Headers.build {
                        append(HttpHeaders.ContentType, contentType.toString())
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    })
                }
            ) {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
    
    /**
     * 上传图片文件
     * @param token 认证令牌
     * @param imageBytes 图片字节数组
     * @param fileName 文件名
     * @return 上传结果
     */
    suspend fun uploadImage(
        token: String,
        imageBytes: ByteArray,
        fileName: String
    ): NetworkResult<SaResult> {
        val contentType = when (fileName.substringAfterLast('.').lowercase()) {
            "jpg", "jpeg" -> ContentType.Image.JPEG
            "png" -> ContentType.Image.PNG
            "gif" -> ContentType.Image.GIF
            else -> ContentType.Image.Any
        }
        
        return uploadFile(token, imageBytes, fileName, contentType)
    }
}