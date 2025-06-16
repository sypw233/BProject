package ovo.sypw.bsp.domain.model

import kotlinx.serialization.Serializable

/**
 * 学生领域模型
 * 表示系统中的学生实体，包含学生的基本信息
 */
@Serializable
data class Student(
    /**
     * 学生ID
     */
    val id: Long,
    
    /**
     * 学生姓名
     */
    val name: String,
    
    /**
     * 性别
     * 1: 男性, 2: 女性
     */
    val gender: Long,
    
    /**
     * 出生日期
     * 格式: yyyy-MM-dd
     */
    val birthDate: String,
    
    /**
     * 入学日期
     * 格式: yyyy-MM-dd
     */
    val joinDate: String,
    
    /**
     * 所属班级ID
     */
    val classId: Long,
    
    /**
     * 学生状态
     * 1: 在校, 2: 休学, 3: 退学, 4: 毕业
     */
    val status: Long
) {
    
    /**
     * 获取性别显示文本
     */
    fun getGenderText(): String {
        return when (gender) {
            1L -> "男"
            2L -> "女"
            else -> "未知"
        }
    }
    
    /**
     * 获取状态显示文本
     */
    fun getStatusText(): String {
        return when (status) {
            1L -> "在校"
            2L -> "休学"
            3L -> "退学"
            4L -> "毕业"
            else -> "未知"
        }
    }
    
    /**
     * 检查学生是否为在校状态
     */
    fun isActive(): Boolean {
        return status == 1L
    }
    
    /**
     * 检查学生是否已毕业
     */
    fun isGraduated(): Boolean {
        return status == 4L
    }
    
    /**
     * 检查学生是否为男性
     */
    fun isMale(): Boolean {
        return gender == 1L
    }
    
    /**
     * 检查学生是否为女性
     */
    fun isFemale(): Boolean {
        return gender == 2L
    }
}

/**
 * 学生性别枚举
 */
enum class StudentGender(val value: Long, val displayName: String) {
    MALE(1L, "男"),
    FEMALE(2L, "女");
    
    companion object {
        fun fromValue(value: Long): StudentGender? {
            return values().find { it.value == value }
        }
    }
}

/**
 * 学生状态枚举
 */
enum class StudentStatus(val value: Long, val displayName: String) {
    ACTIVE(1L, "在校"),
    SUSPENDED(2L, "休学"),
    DROPPED(3L, "退学"),
    GRADUATED(4L, "毕业");
    
    companion object {
        fun fromValue(value: Long): StudentStatus? {
            return values().find { it.value == value }
        }
    }
}

/**
 * 学生创建请求模型
 */
@Serializable
data class StudentCreateRequest(
    /**
     * 学生姓名
     */
    val name: String,
    
    /**
     * 性别
     */
    val gender: Long,
    
    /**
     * 出生日期
     */
    val birthDate: String,
    
    /**
     * 入学日期
     */
    val joinDate: String,
    
    /**
     * 所属班级ID
     */
    val classId: Long,
    
    /**
     * 学生状态
     */
    val status: Long = 1L // 默认为在校状态
)

/**
 * 学生更新请求模型
 */
@Serializable
data class StudentUpdateRequest(
    /**
     * 学生ID
     */
    val id: Long,
    
    /**
     * 学生姓名
     */
    val name: String,
    
    /**
     * 性别
     */
    val gender: Long,
    
    /**
     * 出生日期
     */
    val birthDate: String,
    
    /**
     * 入学日期
     */
    val joinDate: String,
    
    /**
     * 所属班级ID
     */
    val classId: Long,
    
    /**
     * 学生状态
     */
    val status: Long
)

/**
 * 学生查询条件模型
 */
@Serializable
data class StudentQueryCondition(
    /**
     * 学生姓名（模糊查询）
     */
    val name: String? = null,
    
    /**
     * 性别筛选
     */
    val gender: Long? = null,
    
    /**
     * 班级ID筛选
     */
    val classId: Long? = null,
    
    /**
     * 状态筛选
     */
    val status: Long? = null,
    
    /**
     * 出生日期范围开始
     */
    val birthDateStart: String? = null,
    
    /**
     * 出生日期范围结束
     */
    val birthDateEnd: String? = null,
    
    /**
     * 入学日期范围开始
     */
    val joinDateStart: String? = null,
    
    /**
     * 入学日期范围结束
     */
    val joinDateEnd: String? = null
)

/**
 * 学生导入结果模型
 */
@Serializable
data class StudentImportResult(
    /**
     * 导入成功数量
     */
    val successCount: Int,
    
    /**
     * 导入失败数量
     */
    val failureCount: Int,
    
    /**
     * 失败详情列表
     */
    val failureDetails: List<String> = emptyList(),
    
    /**
     * 导入总数
     */
    val totalCount: Int = successCount + failureCount
) {
    
    /**
     * 检查是否全部导入成功
     */
    fun isAllSuccess(): Boolean {
        return failureCount == 0
    }
    
    /**
     * 获取成功率
     */
    fun getSuccessRate(): Double {
        return if (totalCount > 0) {
            successCount.toDouble() / totalCount.toDouble()
        } else {
            0.0
        }
    }
}