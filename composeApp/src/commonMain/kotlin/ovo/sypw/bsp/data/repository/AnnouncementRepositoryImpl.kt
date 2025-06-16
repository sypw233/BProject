package ovo.sypw.bsp.data.repository

import ovo.sypw.bsp.data.api.AnnouncementApiService
import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.AnnouncementRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 公告管理仓库实现类
 * 整合网络API和本地存储，提供完整的公告管理功能
 */
class AnnouncementRepositoryImpl(
    private val announcementApiService: AnnouncementApiService,
    private val tokenStorage: TokenStorage
) : AnnouncementRepository {

    companion object {
        private const val TAG = "AnnouncementRepositoryImpl"
    }

    /**
     * 获取认证令牌
     * @return 认证令牌，如果未登录则返回null
     */
    private suspend fun getAuthToken(): String? {
        return tokenStorage.getAccessToken()
    }

    /**
     * 获取公告分页列表
     */
    override suspend fun getAnnouncementPage(
        current: Int,
        size: Int,
        title: String?,
        type: Int?,
        status: Int?,
        priority: Int?,
        publishTimeStart: String?,
        publishTimeEnd: String?,
        creatorId: Int?
    ): NetworkResult<PageResultDto<AnnouncementDto>> {
        Logger.d(TAG, "获取公告分页列表: current=$current, size=$size, title=$title")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取公告分页列表失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = announcementApiService.getAnnouncementPage(
            current, size, title, type, status, priority,
            publishTimeStart, publishTimeEnd, creatorId, token
        )) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取公告分页列表请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val pageResult = saResult.parseData<PageResultDto<AnnouncementDto>>()
                    if (pageResult != null) {
                        Logger.i(TAG, "公告分页数据解析成功: ${pageResult.records.size}条记录")
                        NetworkResult.Success(pageResult)
                    } else {
                        Logger.w(TAG, "公告分页数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "公告数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取公告分页列表失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取公告分页列表网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 获取公告详情
     */
    override suspend fun getAnnouncementById(id: Int): NetworkResult<AnnouncementDto> {
        Logger.d(TAG, "获取公告详情: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取公告详情失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = announcementApiService.getAnnouncementById(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取公告详情请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val announcement = saResult.parseData<AnnouncementDto>()
                    if (announcement != null) {
                        Logger.i(TAG, "公告详情数据解析成功: ${announcement.title}")
                        NetworkResult.Success(announcement)
                    } else {
                        Logger.w(TAG, "公告详情数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "公告详情数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取公告详情失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取公告详情网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 创建公告
     */
    override suspend fun createAnnouncement(
        title: String,
        content: String,
        type: Int,
        status: Int,
        priority: Int,
        publishTime: String?,
        expireTime: String?,
        creatorId: Int
    ): NetworkResult<Unit> {
        Logger.d(TAG, "创建公告: title=$title")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "创建公告失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        val createDto = AnnouncementCreateDto(
            title = title,
            content = content,
            type = type,
            status = status,
            priority = priority,
            publishTime = publishTime,
            expireTime = expireTime,
            creatorId = creatorId
        )

        return when (val result = announcementApiService.createAnnouncement(createDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "创建公告请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "公告创建成功: $title")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "创建公告失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "创建公告网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 更新公告
     */
    override suspend fun updateAnnouncement(
        id: Int,
        title: String,
        content: String,
        type: Int,
        status: Int,
        priority: Int,
        publishTime: String?,
        expireTime: String?
    ): NetworkResult<Unit> {
        Logger.d(TAG, "更新公告: id=$id, title=$title")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "更新公告失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        val updateDto = AnnouncementUpdateDto(
            id = id,
            title = title,
            content = content,
            type = type,
            status = status,
            priority = priority,
            publishTime = publishTime,
            expireTime = expireTime
        )

        return when (val result = announcementApiService.updateAnnouncement(updateDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "更新公告请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "公告更新成功: id=$id, title=$title")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "更新公告失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "更新公告网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 删除公告
     */
    override suspend fun deleteAnnouncement(id: Int): NetworkResult<Unit> {
        Logger.d(TAG, "删除公告: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "删除公告失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = announcementApiService.deleteAnnouncement(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "删除公告请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "公告删除成功: id=$id")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "删除公告失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "删除公告网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 批量删除公告
     */
    override suspend fun batchDeleteAnnouncements(ids: List<Int>): NetworkResult<Unit> {
        Logger.d(TAG, "批量删除公告: ids=$ids")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "批量删除公告失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = announcementApiService.batchDeleteAnnouncements(ids, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "批量删除公告请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "公告批量删除成功: ${ids.size}个公告")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "批量删除公告失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "批量删除公告网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 发布公告
     */
    override suspend fun publishAnnouncement(id: Int): NetworkResult<Unit> {
        Logger.d(TAG, "发布公告: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "发布公告失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = announcementApiService.publishAnnouncement(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "发布公告请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "公告发布成功: id=$id")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "发布公告失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "发布公告网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 下线公告
     */
    override suspend fun offlineAnnouncement(id: Int): NetworkResult<Unit> {
        Logger.d(TAG, "下线公告: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "下线公告失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = announcementApiService.offlineAnnouncement(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "下线公告请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "公告下线成功: id=$id")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "下线公告失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "下线公告网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 获取已发布的公告列表
     */
    override suspend fun getPublishedAnnouncements(
        current: Int,
        size: Int,
        title: String?,
        type: Int?,
        priority: Int?
    ): NetworkResult<PageResultDto<AnnouncementDto>> {
        Logger.d(TAG, "获取已发布公告列表: current=$current, size=$size, title=$title")
        val token = tokenStorage.getAccessToken()
        if (token == null) {
            Logger.w(TAG, "获取已发布公告列表失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }
        return when (val result = announcementApiService.getPublishedAnnouncements(
            token
        )) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取已发布公告列表请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // API返回的是直接的公告列表数组，需要手动构造分页对象
                    val announcements = saResult.parseData<List<AnnouncementDto>>()
                    if (announcements != null) {
                        Logger.i(
                            TAG,
                            "已发布公告列表数据解析成功: ${announcements.size}条记录"
                        )
                        // 构造分页结果对象
                        val pageResult = PageResultDto(
                            records = announcements,
                            total = announcements.size.toLong(),
                            size = size,
                            current = current,
                            pages = if (announcements.size <= size) 1 else (announcements.size + size - 1) / size
                        )
                        NetworkResult.Success(pageResult)
                    } else {
                        Logger.w(TAG, "已发布公告列表数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "已发布公告数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取已发布公告列表失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取已发布公告列表网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
}