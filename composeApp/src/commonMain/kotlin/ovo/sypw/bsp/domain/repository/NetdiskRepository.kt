package ovo.sypw.bsp.domain.repository

import kotlinx.coroutines.flow.Flow
import ovo.sypw.bsp.data.dto.NetdiskFile
import ovo.sypw.bsp.data.dto.NetdiskFileOperationResult
import ovo.sypw.bsp.data.dto.NetdiskFilePageResponse
import ovo.sypw.bsp.data.dto.NetdiskFileQueryParams
import ovo.sypw.bsp.data.dto.result.NetworkResult

/**
 * 网盘管理仓库接口
 * 定义网盘文件管理相关的数据访问方法
 */
interface NetdiskRepository : BaseRepository {

    /**
     * 上传文件到网盘
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @param mimeType 文件MIME类型
     * @return 上传结果Flow
     */
    fun uploadFile(
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String = "application/octet-stream"
    ): Flow<NetworkResult<NetdiskFileOperationResult>>

    /**
     * 获取文件列表
     * @param queryParams 查询参数
     * @return 文件列表Flow
     */
    fun getFileList(
        queryParams: NetdiskFileQueryParams = NetdiskFileQueryParams()
    ): Flow<NetworkResult<NetdiskFilePageResponse>>

    /**
     * 获取文件详情
     * @param fileId 文件ID
     * @return 文件详情Flow
     */
    fun getFileDetail(
        fileId: Int
    ): Flow<NetworkResult<NetdiskFile>>

    /**
     * 修改文件名
     * @param fileId 文件ID
     * @param newFileName 新文件名
     * @return 修改结果Flow
     */
    fun updateFileName(
        fileId: Int,
        newFileName: String
    ): Flow<NetworkResult<NetdiskFileOperationResult>>

    /**
     * 删除文件
     * @param fileId 文件ID
     * @return 删除结果Flow
     */
    fun deleteFile(
        fileId: Int
    ): Flow<NetworkResult<NetdiskFileOperationResult>>

    /**
     * 批量删除文件
     * @param fileIds 文件ID列表
     * @return 批量删除结果Flow
     */
    fun deleteFiles(
        fileIds: List<Int>
    ): Flow<NetworkResult<NetdiskFileOperationResult>>

    /**
     * 搜索文件
     * @param keyword 搜索关键词
     * @param fileType 文件类型过滤（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果Flow
     */
    fun searchFiles(
        keyword: String,
        fileType: String? = null,
        page: Int = 1,
        size: Int = 10
    ): Flow<NetworkResult<NetdiskFilePageResponse>>

    /**
     * 获取文件类型统计
     * @return 文件类型统计Flow
     */
    fun getFileTypeStatistics(): Flow<NetworkResult<Map<String, Int>>>

    /**
     * 检查文件是否存在
     * @param fileName 文件名
     * @return 是否存在Flow
     */
    fun checkFileExists(
        fileName: String
    ): Flow<NetworkResult<Boolean>>

    /**
     * 获取存储空间使用情况
     * @return 存储空间信息Flow
     */
    fun getStorageInfo(): Flow<NetworkResult<Map<String, Any>>>

    /**
     * 批量上传文件
     * @param files 文件列表，每个元素包含文件字节数组、文件名和MIME类型
     * @return 批量上传结果Flow
     */
    fun uploadMultipleFiles(
        files: List<Triple<ByteArray, String, String>>
    ): Flow<NetworkResult<List<NetdiskFileOperationResult>>>

    /**
     * 下载文件
     * @param fileId 文件ID
     * @return 文件字节数组Flow
     */
    fun downloadFile(
        fileId: Int
    ): Flow<NetworkResult<ByteArray>>

    /**
     * 获取文件下载链接
     * @param fileId 文件ID
     * @return 下载链接Flow
     */
    fun getDownloadUrl(
        fileId: Int
    ): Flow<NetworkResult<String>>

    /**
     * 刷新文件列表缓存
     */
    suspend fun refreshFileListCache()

    /**
     * 清除所有缓存
     */
    suspend fun clearAllCache()
}