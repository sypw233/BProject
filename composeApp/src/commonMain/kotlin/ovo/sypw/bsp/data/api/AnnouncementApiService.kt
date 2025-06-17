package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.data.dto.AnnouncementCreateDto
import ovo.sypw.bsp.data.dto.AnnouncementUpdateDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.utils.Logger

/**
 * 公告管理API服务
 * 提供公告相关的所有API调用方法
 */
class AnnouncementApiService : BaseApiService() {

    companion object {
        private const val TAG = "AnnouncementApiService"

        // API端点常量
        private const val ANNOUNCEMENTS_ENDPOINT = "/announcements"
        private const val ANNOUNCEMENTS_PAGE_ENDPOINT = "/announcements/page"
        private const val ANNOUNCEMENTS_BATCH_ENDPOINT = "/announcements/batch"
        private const val ANNOUNCEMENTS_PUBLISH_ENDPOINT = "/announcements/{id}/publish"
        private const val ANNOUNCEMENTS_OFFLINE_ENDPOINT = "/announcements/{id}/offline"
    }

    /**
     * 获取公告分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param title 公告标题（可选，用于搜索）
     * @param type 公告类型（可选）
     * @param status 公告状态（可选）
     * @param priority 优先级（可选）
     * @param publishTimeStart 发布时间开始（可选）
     * @param publishTimeEnd 发布时间结束（可选）
     * @param creatorId 创建者ID（可选）
     * @param token 认证令牌
     * @return 公告分页数据
     */
    suspend fun getAnnouncementPage(
        current: Int = 1,
        size: Int = 10,
        title: String? = null,
        type: Int? = null,
        status: Int? = null,
        priority: Int? = null,
        publishTimeStart: String? = null,
        publishTimeEnd: String? = null,
        creatorId: Int? = null,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取公告分页列表: current=$current, size=$size, title=$title")

        val parameters = mutableMapOf<String, Any>(
            "current" to current,
            "size" to size
        )

        // 添加可选参数
        title?.let { parameters["title"] = it }
        type?.let { parameters["type"] = it }
        status?.let { parameters["status"] = it }
        priority?.let { parameters["priority"] = it }
        publishTimeStart?.let { parameters["publishTimeStart"] = it }
        publishTimeEnd?.let { parameters["publishTimeEnd"] = it }
        creatorId?.let { parameters["creatorId"] = it }

        return getWithToken(
            endpoint = ANNOUNCEMENTS_PAGE_ENDPOINT,
            token = token,
            parameters = parameters
        )
    }

    /**
     * 获取公告详情
     * @param id 公告ID
     * @param token 认证令牌
     * @return 公告详情数据
     */
    suspend fun getAnnouncementById(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取公告详情: id=$id")

        return getWithToken(
            endpoint = "$ANNOUNCEMENTS_ENDPOINT/$id",
            token = token
        )
    }

    /**
     * 创建公告
     * @param announcementCreateDto 创建公告请求数据
     * @param token 认证令牌
     * @return 创建结果
     */
    suspend fun createAnnouncement(
        announcementCreateDto: AnnouncementCreateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "创建公告: title=${announcementCreateDto.title}")

        return postWithToken(
            endpoint = ANNOUNCEMENTS_ENDPOINT,
            token = token,
            body = announcementCreateDto
        )
    }

    /**
     * 更新公告
     * @param announcementUpdateDto 更新公告请求数据
     * @param token 认证令牌
     * @return 更新结果
     */
    suspend fun updateAnnouncement(
        announcementUpdateDto: AnnouncementUpdateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(
            TAG,
            "更新公告: id=${announcementUpdateDto.id}, title=${announcementUpdateDto.title}"
        )

        return putWithToken(
            endpoint = ANNOUNCEMENTS_ENDPOINT,
            token = token,
            body = announcementUpdateDto
        )
    }

    /**
     * 删除公告
     * @param id 公告ID
     * @param token 认证令牌
     * @return 删除结果
     */
    suspend fun deleteAnnouncement(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "删除公告: id=$id")

        return deleteWithToken(
            endpoint = "$ANNOUNCEMENTS_ENDPOINT/$id",
            token = token
        )
    }

    /**
     * 批量删除公告
     * @param ids 公告ID列表
     * @param token 认证令牌
     * @return 批量删除结果
     */
    suspend fun batchDeleteAnnouncements(
        ids: List<Int>,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "批量删除公告: ids=$ids")

        return deleteWithToken(
            endpoint = ANNOUNCEMENTS_BATCH_ENDPOINT,
            token = token,
            parameters = mapOf("ids" to ids)
        )
    }

    /**
     * 发布公告
     * @param id 公告ID
     * @param token 认证令牌
     * @return 发布结果
     */
    suspend fun publishAnnouncement(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "发布公告: id=$id")

        return putWithToken(
            endpoint = "$ANNOUNCEMENTS_ENDPOINT/$id/publish",
            token = token
        )
    }

    /**
     * 下线公告
     * @param id 公告ID
     * @param token 认证令牌
     * @return 下线结果
     */
    suspend fun offlineAnnouncement(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "下线公告: id=$id")

        return putWithToken(
            endpoint = "$ANNOUNCEMENTS_ENDPOINT/$id/offline",
            token = token
        )
    }

    /**
     * 获取已发布的公告列表（无需认证）
     * @param current 当前页码
     * @param size 每页大小
     * @param title 公告标题（可选，用于搜索）
     * @param type 公告类型（可选）
     * @param priority 优先级（可选）
     * @return 已发布公告列表数据
     */
    suspend fun getPublishedAnnouncements(
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取已发布公告列表:")


        return getWithToken(
            endpoint = "/announcements/published",
            token = token
        )
    }
}