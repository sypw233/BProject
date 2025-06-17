package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.utils.Logger

/**
 * 学生管理API服务
 * 提供学生相关的所有API调用方法
 */
class StudentApiService : BaseApiService() {
    
    companion object {
        private const val TAG = "StudentApiService"
        
        // API端点常量
        private const val STUDENTS_ENDPOINT = "/students"
        private const val STUDENTS_PAGE_ENDPOINT = "/students/page"
        private const val STUDENTS_BATCH_ENDPOINT = "/students/batch"
        private const val STUDENTS_IMPORT_ENDPOINT = "/students/import"
        private const val STUDENTS_EXPORT_ENDPOINT = "/students/export"
    }
    
    /**
     * 获取学生分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 学生姓名（可选，用于搜索）
     * @param gender 性别（可选，用于筛选）
     * @param classId 班级ID（可选，用于筛选）
     * @param status 状态（可选，用于筛选）
     * @param birthDateStart 出生日期开始（可选，用于筛选）
     * @param birthDateEnd 出生日期结束（可选，用于筛选）
     * @param joinDateStart 入学日期开始（可选，用于筛选）
     * @param joinDateEnd 入学日期结束（可选，用于筛选）
     * @param token 认证令牌
     * @return 学生分页数据
     */
    suspend fun getStudentPage(
        current: Int = 1,
        size: Int = 9,
        name: String? = null,
        gender: Int? = null,
        classId: Int? = null,
        status: Int? = null,
        birthDateStart: String? = null,
        birthDateEnd: String? = null,
        joinDateStart: String? = null,
        joinDateEnd: String? = null,
        token: String
    ): NetworkResult<SaResult> {
//        Logger.d(TAG, "获取学生分页列表: current=$current, size=$size")
        
        val parameters = mutableMapOf<String, Any>(
            "current" to current,
            "size" to size
        )
        
        // 添加可选的搜索和筛选参数
        name?.let { parameters["name"] = it }
        gender?.let { parameters["gender"] = it }
        classId?.let { parameters["classId"] = it }
        status?.let { parameters["status"] = it }
        birthDateStart?.let { parameters["birthDateStart"] = it }
        birthDateEnd?.let { parameters["birthDateEnd"] = it }
        joinDateStart?.let { parameters["joinDateStart"] = it }
        joinDateEnd?.let { parameters["joinDateEnd"] = it }
        
        return getWithToken(
            endpoint = STUDENTS_PAGE_ENDPOINT,
            token = token,
            parameters = parameters
        )
    }
    
    /**
     * 获取学生详情
     * @param id 学生ID
     * @param token 认证令牌
     * @return 学生详情数据
     */
    suspend fun getStudentById(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取学生详情: id=$id")
        
        return getWithToken(
            endpoint = "$STUDENTS_ENDPOINT/$id",
            token = token
        )
    }
    
    /**
     * 创建学生
     * @param studentCreateDto 创建学生请求数据
     * @param token 认证令牌
     * @return 创建结果
     */
    suspend fun createStudent(
        studentCreateDto: StudentCreateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "创建学生: name=${studentCreateDto.name}")
        
        return postWithToken(
            endpoint = STUDENTS_ENDPOINT,
            token = token,
            body = studentCreateDto
        )
    }
    
    /**
     * 更新学生
     * @param studentUpdateDto 更新学生请求数据
     * @param token 认证令牌
     * @return 更新结果
     */
    suspend fun updateStudent(
        studentUpdateDto: StudentUpdateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "更新学生: id=${studentUpdateDto.id}, name=${studentUpdateDto.name}")
        
        return putWithToken(
            endpoint = STUDENTS_ENDPOINT,
            token = token,
            body = studentUpdateDto
        )
    }
    
    /**
     * 删除学生
     * @param id 学生ID
     * @param token 认证令牌
     * @return 删除结果
     */
    suspend fun deleteStudent(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "删除学生: id=$id")
        
        return deleteWithToken(
            endpoint = "$STUDENTS_ENDPOINT/$id",
            token = token
        )
    }
    
    /**
     * 批量删除学生
     * @param ids 学生ID列表
     * @param token 认证令牌
     * @return 批量删除结果
     */
    suspend fun batchDeleteStudents(
        ids: List<Int>,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "批量删除学生: ids=$ids")
        
        return deleteWithToken(
            endpoint = STUDENTS_BATCH_ENDPOINT,
            token = token,
            parameters = mapOf("ids" to ids)
        )
    }
    
    /**
     * 批量导入学生
     * @param file 导入文件数据
     * @param token 认证令牌
     * @return 导入结果
     */
    suspend fun importStudents(
        file: ByteArray,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "批量导入学生: 文件大小=${file.size}字节")
        
        // 注意：这里需要实现文件上传的逻辑
        // 由于BaseApiService可能不支持文件上传，这里先返回一个占位实现
        return postWithToken(
            endpoint = STUDENTS_IMPORT_ENDPOINT,
            token = token,
            body = mapOf("file" to file)
        )
    }
    
    /**
     * 批量导出学生
     * @param name 学生姓名（可选，用于搜索）
     * @param gender 性别（可选，用于筛选）
     * @param classId 班级ID（可选，用于筛选）
     * @param status 状态（可选，用于筛选）
     * @param birthDateStart 出生日期开始（可选，用于筛选）
     * @param birthDateEnd 出生日期结束（可选，用于筛选）
     * @param joinDateStart 入学日期开始（可选，用于筛选）
     * @param joinDateEnd 入学日期结束（可选，用于筛选）
     * @param token 认证令牌
     * @return 导出文件数据结果
     */
    suspend fun exportStudents(
        name: String? = null,
        gender: Long? = null,
        classId: Long? = null,
        status: Long? = null,
        birthDateStart: String? = null,
        birthDateEnd: String? = null,
        joinDateStart: String? = null,
        joinDateEnd: String? = null,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "批量导出学生")
        
        val parameters = mutableMapOf<String, Any>()
        
        // 添加可选的搜索和筛选参数
        name?.let { parameters["name"] = it }
        gender?.let { parameters["gender"] = it }
        classId?.let { parameters["classId"] = it }
        status?.let { parameters["status"] = it }
        birthDateStart?.let { parameters["birthDateStart"] = it }
        birthDateEnd?.let { parameters["birthDateEnd"] = it }
        joinDateStart?.let { parameters["joinDateStart"] = it }
        joinDateEnd?.let { parameters["joinDateEnd"] = it }
        
        return getWithToken(
            endpoint = STUDENTS_EXPORT_ENDPOINT,
            token = token,
            parameters = parameters
        )
    }
}