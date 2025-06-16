package ovo.sypw.bsp.domain.repository

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 员工管理仓库接口
 * 定义员工管理相关的业务操作
 */
interface EmployeeRepository : BaseRepository {
    
    /**
     * 获取员工分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param username 用户名（可选，用于搜索）
     * @param realName 真实姓名（可选，用于搜索）
     * @param gender 性别（可选，用于筛选）
     * @param job 职位（可选，用于筛选）
     * @param departmentId 部门ID（可选，用于筛选）
     * @param entryDateStart 入职开始日期（可选，用于筛选）
     * @param entryDateEnd 入职结束日期（可选，用于筛选）
     * @return 员工分页数据结果
     */
    suspend fun getEmployeePage(
        current: Int = 1,
        size: Int = 9,
        username: String? = null,
        realName: String? = null,
        gender: Int? = null,
        job: Int? = null,
        departmentId: Int? = null,
        entryDateStart: String? = null,
        entryDateEnd: String? = null
    ): NetworkResult<PageResultDto<EmployeeDto>>
    
    /**
     * 获取员工详情
     * @param id 员工ID
     * @return 员工详情数据结果
     */
    suspend fun getEmployeeById(id: Int): NetworkResult<EmployeeDto>
    
    /**
     * 创建员工
     * @param employeeCreateDto 创建员工请求数据
     * @return 创建结果
     */
    suspend fun createEmployee(employeeCreateDto: EmployeeCreateDto): NetworkResult<Unit>
    
    /**
     * 更新员工
     * @param employeeUpdateDto 更新员工请求数据
     * @return 更新结果
     */
    suspend fun updateEmployee(employeeUpdateDto: EmployeeUpdateDto): NetworkResult<Unit>
    
    /**
     * 删除员工
     * @param id 员工ID
     * @return 删除结果
     */
    suspend fun deleteEmployee(id: Int): NetworkResult<Unit>
    
    /**
     * 批量删除员工
     * @param ids 员工ID列表
     * @return 批量删除结果
     */
    suspend fun batchDeleteEmployees(ids: List<Int>): NetworkResult<Unit>
    
    /**
     * 批量导入员工
     * @param file 导入文件数据
     * @return 导入结果
     */
    suspend fun importEmployees(file: ByteArray): NetworkResult<EmployeeImportDto>
    
    /**
     * 批量导出员工
     * @param username 用户名（可选，用于搜索）
     * @param realName 真实姓名（可选，用于搜索）
     * @param gender 性别（可选，用于筛选）
     * @param job 职位（可选，用于筛选）
     * @param departmentId 部门ID（可选，用于筛选）
     * @param entryDateStart 入职开始日期（可选，用于筛选）
     * @param entryDateEnd 入职结束日期（可选，用于筛选）
     * @return 导出文件数据结果
     */
    suspend fun exportEmployees(
        username: String? = null,
        realName: String? = null,
        gender: Int? = null,
        job: Int? = null,
        departmentId: Int? = null,
        entryDateStart: String? = null,
        entryDateEnd: String? = null
    ): NetworkResult<ByteArray>
}