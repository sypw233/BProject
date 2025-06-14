package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.DepartmentRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 部门管理用例
 * 封装部门管理相关的业务逻辑
 */
class DepartmentUseCase(
    private val departmentRepository: DepartmentRepository
) {
    
    companion object {
        private const val TAG = "DepartmentUseCase"
    }
    
    /**
     * 获取部门分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 部门名称（可选，用于搜索）
     * @return 部门分页数据结果
     */
    suspend fun getDepartmentPage(
        current: Int = 1,
        size: Int = 5,
        name: String? = null
    ): NetworkResult<PageResultDto<DepartmentDto>> {
//        Logger.d(TAG, "获取部门分页列表用例: current=$current, size=$size, name=$name")
        
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
        
        return departmentRepository.getDepartmentPage(current, size, name)
    }
    
    /**
     * 获取部门详情
     * @param id 部门ID
     * @return 部门详情数据结果
     */
    suspend fun getDepartmentById(id: Int): NetworkResult<DepartmentDto> {
        Logger.d(TAG, "获取部门详情用例: id=$id")
        
        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "部门ID参数无效: $id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("部门ID必须大于0"),
                message = "部门ID参数无效"
            )
        }
        
        return departmentRepository.getDepartmentById(id)
    }
    
    /**
     * 创建部门
     * @param name 部门名称
     * @return 创建结果
     */
    suspend fun createDepartment(name: String): NetworkResult<Unit> {
        Logger.d(TAG, "创建部门用例: name=$name")
        
        // 参数验证
        if (name.isBlank()) {
            Logger.w(TAG, "部门名称不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("部门名称不能为空"),
                message = "部门名称不能为空"
            )
        }
        
        if (name.length > 50) {
            Logger.w(TAG, "部门名称过长: ${name.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("部门名称不能超过50个字符"),
                message = "部门名称不能超过50个字符"
            )
        }
        
        return departmentRepository.createDepartment(name.trim())
    }
    
    /**
     * 更新部门
     * @param id 部门ID
     * @param name 部门名称
     * @return 更新结果
     */
    suspend fun updateDepartment(id: Int, name: String): NetworkResult<Unit> {
        Logger.d(TAG, "更新部门用例: id=$id, name=$name")
        
        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "部门ID参数无效: $id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("部门ID必须大于0"),
                message = "部门ID参数无效"
            )
        }
        
        if (name.isBlank()) {
            Logger.w(TAG, "部门名称不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("部门名称不能为空"),
                message = "部门名称不能为空"
            )
        }
        
        if (name.length > 50) {
            Logger.w(TAG, "部门名称过长: ${name.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("部门名称不能超过50个字符"),
                message = "部门名称不能超过50个字符"
            )
        }
        
        return departmentRepository.updateDepartment(id, name.trim())
    }
    
    /**
     * 删除部门
     * @param id 部门ID
     * @return 删除结果
     */
    suspend fun deleteDepartment(id: Int?): NetworkResult<Unit> {
        Logger.d(TAG, "删除部门用例: id=$id")
        
        // 参数验证
        if (id != null) {
            if (id <= 0) {
                Logger.w(TAG, "部门ID参数无效: $id")
                return NetworkResult.Error(
                    exception = IllegalArgumentException("部门ID必须大于0"),
                    message = "部门ID参数无效"
                )
            }
        }
        
        return departmentRepository.deleteDepartment(id)
    }
    
    /**
     * 批量删除部门
     * @param ids 部门ID列表
     * @return 批量删除结果
     */
    suspend fun batchDeleteDepartments(ids: List<Int>): NetworkResult<Unit> {
        Logger.d(TAG, "批量删除部门用例: ids=$ids")
        
        // 参数验证
        if (ids.isEmpty()) {
            Logger.w(TAG, "部门ID列表不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("部门ID列表不能为空"),
                message = "请选择要删除的部门"
            )
        }
        
        if (ids.size > 50) {
            Logger.w(TAG, "批量删除数量过多: ${ids.size}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("单次批量删除不能超过50个部门"),
                message = "单次批量删除不能超过50个部门"
            )
        }
        
        // 验证所有ID都是有效的
        val invalidIds = ids.filter { it <= 0 }
        if (invalidIds.isNotEmpty()) {
            Logger.w(TAG, "包含无效的部门ID: $invalidIds")
            return NetworkResult.Error(
                exception = IllegalArgumentException("包含无效的部门ID"),
                message = "包含无效的部门ID"
            )
        }
        
        return departmentRepository.batchDeleteDepartments(ids)
    }
}