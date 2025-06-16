package ovo.sypw.bsp.domain.repository

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 学生管理仓库接口
 * 定义学生管理相关的业务操作
 */
interface StudentRepository : BaseRepository {
    
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
     * @return 学生分页数据结果
     */
    suspend fun getStudentPage(
        current: Int = 1,
        size: Int = 10,
        name: String? = null,
        gender: Int? = null,
        classId: Int? = null,
        status: Int? = null,
        birthDateStart: String? = null,
        birthDateEnd: String? = null,
        joinDateStart: String? = null,
        joinDateEnd: String? = null
    ): NetworkResult<PageResultDto<StudentDto>>
    
    /**
     * 获取学生详情
     * @param id 学生ID
     * @return 学生详情数据结果
     */
    suspend fun getStudentById(id: Int): NetworkResult<StudentDto>
    
    /**
     * 创建学生
     * @param studentCreateDto 创建学生请求数据
     * @return 创建结果
     */
    suspend fun createStudent(studentCreateDto: StudentCreateDto): NetworkResult<Unit>
    
    /**
     * 更新学生
     * @param studentUpdateDto 更新学生请求数据
     * @return 更新结果
     */
    suspend fun updateStudent(studentUpdateDto: StudentUpdateDto): NetworkResult<Unit>
    
    /**
     * 删除学生
     * @param id 学生ID
     * @return 删除结果
     */
    suspend fun deleteStudent(id: Int): NetworkResult<Unit>
    
    /**
     * 批量删除学生
     * @param ids 学生ID列表
     * @return 批量删除结果
     */
    suspend fun batchDeleteStudents(ids: List<Int>): NetworkResult<Unit>
    
    /**
     * 批量导入学生
     * @param file 导入文件数据
     * @return 导入结果
     */
    suspend fun importStudents(file: ByteArray): NetworkResult<StudentImportDto>
    
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
        joinDateEnd: String? = null
    ): NetworkResult<ByteArray>
}