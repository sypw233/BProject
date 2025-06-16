package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 员工数据传输对象
 */
@Serializable
data class EmployeeDto(
    /**
     * 员工ID
     */
    val id: Int? = null,
    
    /**
     * 用户名
     */
    val username: String,
    
    /**
     * 密码
     */
    val password: String? = null,
    
    /**
     * 真实姓名
     */
    val realName: String,
    
    /**
     * 性别 (1-男, 2-女)
     */
    val gender: Int,
    
    /**
     * 头像URL
     */
    val avatar: String? = null,
    
    /**
     * 职位 (1-普通员工, 2-主管, 3-经理)
     */
    val job: Int,
    
    /**
     * 部门ID
     */
    val departmentId: Int,
    
    /**
     * 入职日期
     */
    val entryDate: String
)

/**
 * 创建员工请求DTO
 */
@Serializable
data class EmployeeCreateDto(
    /**
     * 用户名
     */
    val username: String,

    /**
     * 密码
     */
    val password: String,

    /**
     * 真实姓名
     */
    val realName: String,

    /**
     * 性别 (1-男, 2-女)
     */
    val gender: Int,

    /**
     * 头像URL
     */
    val avatar: String? = null,

    /**
     * 职位 (1-普通员工, 2-主管, 3-经理)
     */
    val job: Int,

    /**
     * 部门ID
     */
    val departmentId: Int,

    /**
     * 入职日期
     */
    val entryDate: String?
)

/**
 * 更新员工请求DTO
 */
@Serializable
data class EmployeeUpdateDto(
    /**
     * 员工ID
     */
    val id: Int,

    /**
     * 用户名
     */
    val username: String,

    /**
     * 密码（可选，不更新时为null）
     */
    val password: String? = null,

    /**
     * 真实姓名
     */
    val realName: String,

    /**
     * 性别 (1-男, 2-女)
     */
    val gender: Int,

    /**
     * 头像URL
     */
    val avatar: String? = null,

    /**
     * 职位 (1-普通员工, 2-主管, 3-经理)
     */
    val job: Int,

    /**
     * 部门ID
     */
    val departmentId: Int,

    /**
     * 入职日期
     */
    val entryDate: String?
)

/**
 * 员工导入DTO
 */
@Serializable
data class EmployeeImportDto(
    /**
     * 导入结果消息
     */
    val message: String,
    
    /**
     * 成功导入数量
     */
    val successCount: Int,
    
    /**
     * 失败导入数量
     */
    val failureCount: Int,
    
    /**
     * 失败详情列表
     */
    val failureDetails: List<String>? = null
) {
    /**
     * 总导入数量
     */
    val totalCount: Int
        get() = successCount + failureCount
    
    /**
     * 是否全部成功
     */
    fun isAllSuccess(): Boolean = failureCount == 0
}