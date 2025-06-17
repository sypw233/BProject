package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.AnnouncementCreateDto
import ovo.sypw.bsp.data.dto.AnnouncementDto
import ovo.sypw.bsp.data.dto.AnnouncementUpdateDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.AnnouncementRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 公告管理用例
 * 封装公告管理相关的业务逻辑
 */
class AnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository
) {

    companion object {
        private const val TAG = "AnnouncementUseCase"
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
    ): NetworkResult<PageResultDto<AnnouncementDto>> {
        Logger.d(TAG, "获取公告分页列表用例: current=$current, size=$size, title=$title")

        // 参数验证
        if (current < 1) {
            Logger.w(TAG, "页码参数无效: $current")
            return NetworkResult.Error(
                exception = IllegalArgumentException("页码必须大于0"),
                message = "页码参数无效"
            )
        }

        if (size < 1 || size > 100) {
            Logger.w(TAG, "每页大小参数无效: $size")
            return NetworkResult.Error(
                exception = IllegalArgumentException("每页大小必须在1-100之间"),
                message = "每页大小参数无效"
            )
        }

        // 验证可选参数
        type?.let {
            if (it < 0) {
                Logger.w(TAG, "公告类型参数无效: $it")
                return NetworkResult.Error(
                    exception = IllegalArgumentException("公告类型必须大于等于0"),
                    message = "公告类型参数无效"
                )
            }
        }

        status?.let {
            if (it < 0) {
                Logger.w(TAG, "公告状态参数无效: $it")
                return NetworkResult.Error(
                    exception = IllegalArgumentException("公告状态必须大于等于0"),
                    message = "公告状态参数无效"
                )
            }
        }

        priority?.let {
            if (it < 0) {
                Logger.w(TAG, "优先级参数无效: $it")
                return NetworkResult.Error(
                    exception = IllegalArgumentException("优先级必须大于等于0"),
                    message = "优先级参数无效"
                )
            }
        }

        creatorId?.let {
            if (it <= 0) {
                Logger.w(TAG, "创建者ID参数无效: $it")
                return NetworkResult.Error(
                    exception = IllegalArgumentException("创建者ID必须大于0"),
                    message = "创建者ID参数无效"
                )
            }
        }

        return announcementRepository.getAnnouncementPage(
            current, size, title, type, status, priority,
            publishTimeStart, publishTimeEnd, creatorId
        )
    }

    /**
     * 获取公告详情
     * @param id 公告ID
     * @return 公告详情数据结果
     */
    suspend fun getAnnouncementById(id: Int): NetworkResult<AnnouncementDto> {
        Logger.d(TAG, "获取公告详情用例: id=$id")

        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "公告ID参数无效: $id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告ID必须大于0"),
                message = "公告ID参数无效"
            )
        }

        return announcementRepository.getAnnouncementById(id)
    }

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
        announcementCreateDto: AnnouncementCreateDto
    ): NetworkResult<Unit> {
        Logger.d(TAG, "创建公告用例: title=${announcementCreateDto.title}")

        // 参数验证
        if (announcementCreateDto.title.isBlank()) {
            Logger.w(TAG, "公告标题不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告标题不能为空"),
                message = "公告标题不能为空"
            )
        }

        if (announcementCreateDto.title.length > 200) {
            Logger.w(TAG, "公告标题过长: ${announcementCreateDto.title.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告标题不能超过200个字符"),
                message = "公告标题不能超过200个字符"
            )
        }

        if (announcementCreateDto.content.isBlank()) {
            Logger.w(TAG, "公告内容不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告内容不能为空"),
                message = "公告内容不能为空"
            )
        }

        if (announcementCreateDto.content.length > 5000) {
            Logger.w(TAG, "公告内容过长: ${announcementCreateDto.content.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告内容不能超过5000个字符"),
                message = "公告内容不能超过5000个字符"
            )
        }

        if (announcementCreateDto.type < 0) {
            Logger.w(TAG, "公告类型参数无效: ${announcementCreateDto.type}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告类型必须大于等于0"),
                message = "公告类型参数无效"
            )
        }

        if (announcementCreateDto.status < 0) {
            Logger.w(TAG, "公告状态参数无效: ${announcementCreateDto.status}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告状态必须大于等于0"),
                message = "公告状态参数无效"
            )
        }

        if (announcementCreateDto.priority < 0) {
            Logger.w(TAG, "优先级参数无效: ${announcementCreateDto.priority}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("优先级必须大于等于0"),
                message = "优先级参数无效"
            )
        }

        if (announcementCreateDto.creatorId <= 0) {
            Logger.w(TAG, "创建者ID参数无效: ${announcementCreateDto.creatorId}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("创建者ID必须大于0"),
                message = "创建者ID参数无效"
            )
        }

        return announcementRepository.createAnnouncement(
            announcementCreateDto.title.trim(),
            announcementCreateDto.content.trim(),
            announcementCreateDto.type,
            announcementCreateDto.status,
            announcementCreateDto.priority,
            announcementCreateDto.publishTime,
            announcementCreateDto.expireTime,
            announcementCreateDto.creatorId
        )
    }

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
        announcementUpdateDto: AnnouncementUpdateDto
    ): NetworkResult<Unit> {
        Logger.d(
            TAG,
            "更新公告用例: id=${announcementUpdateDto.id}, title=${announcementUpdateDto.title}"
        )

        // 参数验证
        if (announcementUpdateDto.id <= 0) {
            Logger.w(TAG, "公告ID参数无效: ${announcementUpdateDto.id}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告ID必须大于0"),
                message = "公告ID参数无效"
            )
        }

        if (announcementUpdateDto.title.isBlank()) {
            Logger.w(TAG, "公告标题不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告标题不能为空"),
                message = "公告标题不能为空"
            )
        }

        if (announcementUpdateDto.title.length > 200) {
            Logger.w(TAG, "公告标题过长: ${announcementUpdateDto.title.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告标题不能超过200个字符"),
                message = "公告标题不能超过200个字符"
            )
        }

        if (announcementUpdateDto.content.isBlank()) {
            Logger.w(TAG, "公告内容不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告内容不能为空"),
                message = "公告内容不能为空"
            )
        }

        if (announcementUpdateDto.content.length > 5000) {
            Logger.w(TAG, "公告内容过长: ${announcementUpdateDto.content.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告内容不能超过5000个字符"),
                message = "公告内容不能超过5000个字符"
            )
        }

        if (announcementUpdateDto.type < 0) {
            Logger.w(TAG, "公告类型参数无效: ${announcementUpdateDto.type}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告类型必须大于等于0"),
                message = "公告类型参数无效"
            )
        }

        if (announcementUpdateDto.status < 0) {
            Logger.w(TAG, "公告状态参数无效: ${announcementUpdateDto.status}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告状态必须大于等于0"),
                message = "公告状态参数无效"
            )
        }

        if (announcementUpdateDto.priority < 0) {
            Logger.w(TAG, "优先级参数无效: ${announcementUpdateDto.priority}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("优先级必须大于等于0"),
                message = "优先级参数无效"
            )
        }

        return announcementRepository.updateAnnouncement(
            announcementUpdateDto.id,
            announcementUpdateDto.title.trim(),
            announcementUpdateDto.content.trim(),
            announcementUpdateDto.type,
            announcementUpdateDto.status,
            announcementUpdateDto.priority,
            announcementUpdateDto.publishTime,
            announcementUpdateDto.expireTime
        )
    }

    /**
     * 删除公告
     * @param id 公告ID
     * @return 删除结果
     */
    suspend fun deleteAnnouncement(id: Int): NetworkResult<Unit> {
        Logger.d(TAG, "删除公告用例: id=$id")

        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "公告ID参数无效: $id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告ID必须大于0"),
                message = "公告ID参数无效"
            )
        }

        return announcementRepository.deleteAnnouncement(id)
    }

    /**
     * 批量删除公告
     * @param ids 公告ID列表
     * @return 批量删除结果
     */
    suspend fun batchDeleteAnnouncements(ids: List<Int>): NetworkResult<Unit> {
        Logger.d(TAG, "批量删除公告用例: ids=$ids")

        // 参数验证
        if (ids.isEmpty()) {
            Logger.w(TAG, "公告ID列表不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告ID列表不能为空"),
                message = "请选择要删除的公告"
            )
        }

        if (ids.size > 50) {
            Logger.w(TAG, "批量删除数量过多: ${ids.size}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("单次批量删除不能超过50个公告"),
                message = "单次批量删除不能超过50个公告"
            )
        }

        // 验证所有ID都是有效的
        val invalidIds = ids.filter { it <= 0 }
        if (invalidIds.isNotEmpty()) {
            Logger.w(TAG, "包含无效的公告ID: $invalidIds")
            return NetworkResult.Error(
                exception = IllegalArgumentException("包含无效的公告ID"),
                message = "包含无效的公告ID"
            )
        }

        return announcementRepository.batchDeleteAnnouncements(ids)
    }

    /**
     * 发布公告
     * @param id 公告ID
     * @return 发布结果
     */
    suspend fun publishAnnouncement(id: Int): NetworkResult<Unit> {
        Logger.d(TAG, "发布公告用例: id=$id")

        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "公告ID参数无效: $id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告ID必须大于0"),
                message = "公告ID参数无效"
            )
        }

        return announcementRepository.publishAnnouncement(id)
    }

    /**
     * 下线公告
     * @param id 公告ID
     * @return 下线结果
     */
    suspend fun offlineAnnouncement(id: Int): NetworkResult<Unit> {
        Logger.d(TAG, "下线公告用例: id=$id")

        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "公告ID参数无效: $id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("公告ID必须大于0"),
                message = "公告ID参数无效"
            )
        }

        return announcementRepository.offlineAnnouncement(id)
    }

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
    ): NetworkResult<PageResultDto<AnnouncementDto>> {
        Logger.d(TAG, "获取已发布公告列表用例: current=$current, size=$size, title=$title")

        // 参数验证
        if (current < 1) {
            Logger.w(TAG, "页码参数无效: $current")
            return NetworkResult.Error(
                exception = IllegalArgumentException("页码必须大于0"),
                message = "页码参数无效"
            )
        }

        if (size < 1 || size > 100) {
            Logger.w(TAG, "每页大小参数无效: $size")
            return NetworkResult.Error(
                exception = IllegalArgumentException("每页大小必须在1-100之间"),
                message = "每页大小参数无效"
            )
        }

        // 验证可选参数
        type?.let {
            if (it < 0) {
                Logger.w(TAG, "公告类型参数无效: $it")
                return NetworkResult.Error(
                    exception = IllegalArgumentException("公告类型必须大于等于0"),
                    message = "公告类型参数无效"
                )
            }
        }

        priority?.let {
            if (it < 0) {
                Logger.w(TAG, "优先级参数无效: $it")
                return NetworkResult.Error(
                    exception = IllegalArgumentException("优先级必须大于等于0"),
                    message = "优先级参数无效"
                )
            }
        }

        return announcementRepository.getPublishedAnnouncements(
            current, size, title, type, priority
        )
    }
}