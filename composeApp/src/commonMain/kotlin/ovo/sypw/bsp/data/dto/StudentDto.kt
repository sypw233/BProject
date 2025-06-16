package ovo.sypw.bsp.data.dto

import kotlinx.serialization.Serializable

/**
 * 学生数据传输对象
 */
@Serializable
data class StudentDto(
    /**
     * 学生ID
     */
    val id: Int? = null,
    
    /**
     * 学生姓名
     */
    val name: String,
    
    /**
     * 性别 (1-男, 2-女)
     */
    val gender: Int,
    
    /**
     * 出生日期
     */
    val birthDate: String,
    
    /**
     * 班级ID
     */
    val classId: Int,
    
    /**
     * 入学日期
     */
    val joinDate: String,
    
    /**
     * 状态 (1-在校, 2-休学, 3-毕业, 4-退学)
     */
    val status: Int
)

/**
 * 创建学生请求DTO
 */
@Serializable
data class StudentCreateDto(
    /**
     * 学生姓名
     */
    val name: String,

    /**
     * 性别 (1-男, 2-女)
     */
    val gender: Int,

    /**
     * 出生日期
     */
    val birthDate: String,

    /**
     * 班级ID
     */
    val classId: Int,

    /**
     * 入学日期
     */
    val joinDate: String,

    /**
     * 状态 (1-在校, 2-休学, 3-毕业, 4-退学)
     */
    val status: Int = 1
)

/**
 * 更新学生请求DTO
 */
@Serializable
data class StudentUpdateDto(
    /**
     * 学生ID
     */
    val id: Int,

    /**
     * 学生姓名
     */
    val name: String,

    /**
     * 性别 (1-男, 2-女)
     */
    val gender: Int,

    /**
     * 出生日期
     */
    val birthDate: String,

    /**
     * 班级ID
     */
    val classId: Int,

    /**
     * 入学日期
     */
    val joinDate: String,

    /**
     * 状态 (1-在校, 2-休学, 3-毕业, 4-退学)
     */
    val status: Int
)

/**
 * 学生导入DTO
 */
@Serializable
data class StudentImportDto(
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
    val failures: List<String>? = null
)