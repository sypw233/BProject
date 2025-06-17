package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 部门数据传输对象
 */
@Serializable
data class DepartmentDto(
    /**
     * 部门ID
     */
    val id: Int? = null,

    /**
     * 部门名称
     */
    val name: String
)

/**
 * 创建部门请求DTO
 */
@Serializable
data class DepartmentCreateDto(
    /**
     * 部门名称
     */
    val name: String
)

/**
 * 更新部门请求DTO
 */
@Serializable
data class DepartmentUpdateDto(
    /**
     * 部门ID
     */
    val id: Int,

    /**
     * 部门名称
     */
    val name: String
)


/**
 * 分页结果DTO
 */
@Serializable
data class PageResultDto<T>(
    /**
     * 当前页码
     */
    val current: Int,

    /**
     * 每页大小
     */
    val size: Int,

    /**
     * 总记录数
     */
    val total: Long,

    /**
     * 总页数
     */
    val pages: Int,

    /**
     * 数据列表
     */
    val records: List<T>
)