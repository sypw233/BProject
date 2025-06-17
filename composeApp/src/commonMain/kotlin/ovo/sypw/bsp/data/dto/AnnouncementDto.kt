package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 公告类型枚举
 */
enum class AnnouncementType(val value: Int, val displayName: String) {
    /**
     * 系统公告
     */
    SYSTEM(1, "系统公告"),

    /**
     * 活动公告
     */
    ACTIVITY(2, "活动公告"),

    /**
     * 通知公告
     */
    NOTIFICATION(3, "通知公告");

    companion object {
        /**
         * 根据值获取公告类型
         */
        fun fromValue(value: Int): AnnouncementType? {
            return values().find { it.value == value }
        }

        /**
         * 获取所有公告类型的显示名称列表
         */
        fun getDisplayNames(): List<String> {
            return values().map { it.displayName }
        }
    }
}

/**
 * 公告状态枚举
 */
enum class AnnouncementStatus(val value: Int, val displayName: String) {
    /**
     * 草稿
     */
    DRAFT(0, "草稿"),

    /**
     * 已发布
     */
    PUBLISHED(1, "已发布"),

    /**
     * 已下线
     */
    OFFLINE(2, "已下线");

    companion object {
        /**
         * 根据值获取公告状态
         */
        fun fromValue(value: Int): AnnouncementStatus? {
            return values().find { it.value == value }
        }

        /**
         * 获取所有公告状态的显示名称列表
         */
        fun getDisplayNames(): List<String> {
            return values().map { it.displayName }
        }
    }
}

/**
 * 公告优先级枚举
 */
enum class AnnouncementPriority(val value: Int, val displayName: String) {
    /**
     * 普通
     */
    NORMAL(1, "普通"),

    /**
     * 重要
     */
    IMPORTANT(2, "重要"),

    /**
     * 紧急
     */
    URGENT(3, "紧急");

    companion object {
        /**
         * 根据值获取公告优先级
         */
        fun fromValue(value: Int): AnnouncementPriority? {
            return values().find { it.value == value }
        }

        /**
         * 获取所有公告优先级的显示名称列表
         */
        fun getDisplayNames(): List<String> {
            return values().map { it.displayName }
        }
    }
}

/**
 * 公告数据传输对象
 */
@Serializable
data class AnnouncementDto(
    /**
     * 公告ID
     */
    val id: Int? = null,

    /**
     * 公告标题
     */
    val title: String,

    /**
     * 公告内容
     */
    val content: String,

    /**
     * 公告类型
     */
    val type: Int,

    /**
     * 公告状态
     */
    val status: Int,

    /**
     * 优先级
     */
    val priority: Int,

    /**
     * 发布时间
     */
    val publishTime: String? = null,

    /**
     * 过期时间
     */
    val expireTime: String? = null,

    /**
     * 创建者ID
     */
    val creatorId: Int? = null,

    /**
     * 创建时间
     */
    val createTime: String? = null,

    /**
     * 更新时间
     */
    val updateTime: String? = null
)

/**
 * 创建公告请求DTO
 */
@Serializable
data class AnnouncementCreateDto(
    /**
     * 公告标题
     */
    val title: String,

    /**
     * 公告内容
     */
    val content: String,

    /**
     * 公告类型
     */
    val type: Int,

    /**
     * 公告状态
     */
    val status: Int,

    /**
     * 优先级
     */
    val priority: Int,

    /**
     * 发布时间
     */
    val publishTime: String? = null,

    /**
     * 过期时间
     */
    val expireTime: String? = null,

    /**
     * 创建者ID
     */
    val creatorId: Int
)

/**
 * 更新公告请求DTO
 */
@Serializable
data class AnnouncementUpdateDto(
    /**
     * 公告ID
     */
    val id: Int,

    /**
     * 公告标题
     */
    val title: String,

    /**
     * 公告内容
     */
    val content: String,

    /**
     * 公告类型
     */
    val type: Int,

    /**
     * 公告状态
     */
    val status: Int,

    /**
     * 优先级
     */
    val priority: Int,

    /**
     * 发布时间
     */
    val publishTime: String? = null,

    /**
     * 过期时间
     */
    val expireTime: String? = null
)

/**
 * 公告分页查询请求DTO
 */
@Serializable
data class AnnouncementPageQueryDto(
    /**
     * 当前页码
     */
    val current: Int = 1,

    /**
     * 每页大小
     */
    val size: Int = 10,

    /**
     * 公告标题（可选，用于搜索）
     */
    val title: String? = null,

    /**
     * 公告类型（可选）
     */
    val type: Int? = null,

    /**
     * 公告状态（可选）
     */
    val status: Int? = null,

    /**
     * 优先级（可选）
     */
    val priority: Int? = null,

    /**
     * 发布时间开始（可选）
     */
    val publishTimeStart: String? = null,

    /**
     * 发布时间结束（可选）
     */
    val publishTimeEnd: String? = null,

    /**
     * 创建者ID（可选）
     */
    val creatorId: Int? = null
)