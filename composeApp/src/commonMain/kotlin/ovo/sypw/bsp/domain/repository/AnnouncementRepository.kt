package ovo.sypw.bsp.domain.repository

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 公告管理仓库接口
 * 定义公告管理相关的业务操作
 */
interface AnnouncementRepository : BaseRepository {
    
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
     * @return 公告分页数据结果
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
        creatorId: Int? = null
    ): NetworkResult<PageResultDto<AnnouncementDto>>
    
    /**
     * 获取公告详情
     * @param id 公告ID
     * @return 公告详情数据结果
     */
    suspend fun getAnnouncementById(id: Int): NetworkResult<AnnouncementDto>
    
    /**
     * 创建公告
     * @param title 公告标题
     * @param content 公告内容
     * @param type 公告类型
     * @param status 公告状态
     * @param priority 优先级
     * @param publishTime 发布时间（可选）
     * @param expireTime 过期时间（可选）
     * @param creatorId 创建者ID
     * @return 创建结果
     */
    suspend fun createAnnouncement(
        title: String,
        content: String,
        type: Int,
        status: Int,
        priority: Int,
        publishTime: String? = null,
        expireTime: String? = null,
        creatorId: Int
    ): NetworkResult<Unit>
    
    /**
     * 更新公告
     * @param id 公告ID
     * @param title 公告标题
     * @param content 公告内容
     * @param type 公告类型
     * @param status 公告状态
     * @param priority 优先级
     * @param publishTime 发布时间（可选）
     * @param expireTime 过期时间（可选）
     * @return 更新结果
     */
    suspend fun updateAnnouncement(
        id: Int,
        title: String,
        content: String,
        type: Int,
        status: Int,
        priority: Int,
        publishTime: String? = null,
        expireTime: String? = null
    ): NetworkResult<Unit>
    
    /**
     * 删除公告
     * @param id 公告ID
     * @return 删除结果
     */
    suspend fun deleteAnnouncement(id: Int): NetworkResult<Unit>
    
    /**
     * 批量删除公告
     * @param ids 公告ID列表
     * @return 批量删除结果
     */
    suspend fun batchDeleteAnnouncements(ids: List<Int>): NetworkResult<Unit>
    
    /**
     * 发布公告
     * @param id 公告ID
     * @return 发布结果
     */
    suspend fun publishAnnouncement(id: Int): NetworkResult<Unit>
    
    /**
     * 下线公告
     * @param id 公告ID
     * @return 下线结果
     */
    suspend fun offlineAnnouncement(id: Int): NetworkResult<Unit>
    
    /**
     * 获取已发布的公告列表（无需认证）
     * @param current 当前页码
     * @param size 每页大小
     * @param title 公告标题（可选，用于搜索）
     * @param type 公告类型（可选）
     * @param priority 优先级（可选）
     * @return 已发布公告分页数据结果
     */
    suspend fun getPublishedAnnouncements(
        current: Int = 1,
        size: Int = 10,
        title: String? = null,
        type: Int? = null,
        priority: Int? = null
    ): NetworkResult<PageResultDto<AnnouncementDto>>
}