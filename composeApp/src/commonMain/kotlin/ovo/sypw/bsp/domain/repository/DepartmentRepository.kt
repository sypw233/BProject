package ovo.sypw.bsp.domain.repository

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 部门管理仓库接口
 * 定义部门管理相关的业务操作
 */
interface DepartmentRepository : BaseRepository {
    
    /**
     * 获取部门分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 部门名称（可选，用于搜索）
     * @return 部门分页数据结果
     */
    suspend fun getDepartmentPage(
        current: Int = 1,
        size: Int = 10,
        name: String? = null
    ): NetworkResult<PageResultDto<DepartmentDto>>
    
    /**
     * 获取部门详情
     * @param id 部门ID
     * @return 部门详情数据结果
     */
    suspend fun getDepartmentById(id: Int): NetworkResult<DepartmentDto>
    
    /**
     * 创建部门
     * @param name 部门名称
     * @return 创建结果
     */
    suspend fun createDepartment(name: String): NetworkResult<Unit>
    
    /**
     * 更新部门
     * @param id 部门ID
     * @param name 部门名称
     * @return 更新结果
     */
    suspend fun updateDepartment(id: Int, name: String): NetworkResult<Unit>
    
    /**
     * 删除部门
     * @param id 部门ID
     * @return 删除结果
     */
    suspend fun deleteDepartment(id: Int?): NetworkResult<Unit>
    
    /**
     * 批量删除部门
     * @param ids 部门ID列表
     * @return 批量删除结果
     */
    suspend fun batchDeleteDepartments(ids: List<Int>): NetworkResult<Unit>
}