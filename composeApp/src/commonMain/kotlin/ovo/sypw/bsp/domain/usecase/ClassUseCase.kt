package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.ClassDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.ClassRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 班级管理用例
 * 封装班级管理相关的业务逻辑
 */
class ClassUseCase(
    private val classRepository: ClassRepository
) {
    
    companion object {
        private const val TAG = "ClassUseCase"
    }
    
    /**
     * 获取班级分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 班级名称（可选，用于搜索）
     * @return 班级分页数据结果
     */
    suspend fun getClassPage(
        current: Int = 1,
        size: Int = 9,
        name: String? = null
    ): NetworkResult<PageResultDto<ClassDto>> {
//        Logger.d(TAG, "获取班级分页列表用例: current=$current, size=$size, name=$name")
        
        // 参数验证
        if (current < 1) {
            Logger.w(TAG, "页码参数无效: $current")
            return NetworkResult.Error(
                exception = IllegalArgumentException("页码必须大于0"),
                message = "页码参数无效"
            )
        }
        
        if (size < 1 || size > 9999) {
            Logger.w(TAG, "每页大小参数无效: $size")
            return NetworkResult.Error(
                exception = IllegalArgumentException("每页大小必须在1-100之间"),
                message = "每页大小参数无效"
            )
        }
        
        return classRepository.getClassPage(current, size, name)
    }
    
    /**
     * 获取班级详情
     * @param id 班级ID
     * @return 班级详情数据结果
     */
    suspend fun getClassById(id: Int): NetworkResult<ClassDto> {
        Logger.d(TAG, "获取班级详情用例: id=$id")
        
        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "班级ID参数无效: $id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("班级ID必须大于0"),
                message = "班级ID参数无效"
            )
        }
        
        return classRepository.getClassById(id)
    }
    
    /**
     * 创建班级
     * @param name 班级名称
     * @param grade 年级
     * @return 创建结果
     */
    suspend fun createClass(name: String, grade: String): NetworkResult<Unit> {
        Logger.d(TAG, "创建班级用例: name=$name, grade=$grade")
        
        // 参数验证
        if (name.isBlank()) {
            Logger.w(TAG, "班级名称不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("班级名称不能为空"),
                message = "班级名称不能为空"
            )
        }
        
        if (name.length > 50) {
            Logger.w(TAG, "班级名称过长: ${name.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("班级名称不能超过50个字符"),
                message = "班级名称不能超过50个字符"
            )
        }
        
        if (grade.isBlank()) {
            Logger.w(TAG, "年级不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("年级不能为空"),
                message = "年级不能为空"
            )
        }
        
        if (grade.length > 20) {
            Logger.w(TAG, "年级过长: ${grade.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("年级不能超过20个字符"),
                message = "年级不能超过20个字符"
            )
        }
        
        return classRepository.createClass(name.trim(), grade.trim())
    }
    
    /**
     * 更新班级
     * @param id 班级ID
     * @param name 班级名称
     * @param grade 年级
     * @return 更新结果
     */
    suspend fun updateClass(id: Int, name: String, grade: String): NetworkResult<Unit> {
        Logger.d(TAG, "更新班级用例: id=$id, name=$name, grade=$grade")
        
        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "班级ID参数无效: $id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("班级ID必须大于0"),
                message = "班级ID参数无效"
            )
        }
        
        if (name.isBlank()) {
            Logger.w(TAG, "班级名称不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("班级名称不能为空"),
                message = "班级名称不能为空"
            )
        }
        
        if (name.length > 50) {
            Logger.w(TAG, "班级名称过长: ${name.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("班级名称不能超过50个字符"),
                message = "班级名称不能超过50个字符"
            )
        }
        
        if (grade.isBlank()) {
            Logger.w(TAG, "年级不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("年级不能为空"),
                message = "年级不能为空"
            )
        }
        
        if (grade.length > 20) {
            Logger.w(TAG, "年级过长: ${grade.length}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("年级不能超过20个字符"),
                message = "年级不能超过20个字符"
            )
        }
        
        return classRepository.updateClass(id, name.trim(), grade.trim())
    }
    
    /**
     * 删除班级
     * @param id 班级ID
     * @return 删除结果
     */
    suspend fun deleteClass(id: Int?): NetworkResult<Unit> {
        Logger.d(TAG, "删除班级用例: id=$id")
        
        // 参数验证
        if (id != null) {
            if (id <= 0) {
                Logger.w(TAG, "班级ID参数无效: $id")
                return NetworkResult.Error(
                    exception = IllegalArgumentException("班级ID必须大于0"),
                    message = "班级ID参数无效"
                )
            }
        }
        
        return classRepository.deleteClass(id)
    }
    
    /**
     * 批量删除班级
     * @param ids 班级ID列表
     * @return 批量删除结果
     */
    suspend fun batchDeleteClasses(ids: List<Int>): NetworkResult<Unit> {
        Logger.d(TAG, "批量删除班级用例: ids=$ids")
        
        // 参数验证
        if (ids.isEmpty()) {
            Logger.w(TAG, "班级ID列表不能为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("班级ID列表不能为空"),
                message = "请选择要删除的班级"
            )
        }
        
        if (ids.size > 50) {
            Logger.w(TAG, "批量删除数量过多: ${ids.size}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("单次批量删除不能超过50个班级"),
                message = "单次批量删除不能超过50个班级"
            )
        }
        
        // 验证所有ID都是有效的
        val invalidIds = ids.filter { it <= 0 }
        if (invalidIds.isNotEmpty()) {
            Logger.w(TAG, "包含无效的班级ID: $invalidIds")
            return NetworkResult.Error(
                exception = IllegalArgumentException("包含无效的班级ID"),
                message = "包含无效的班级ID"
            )
        }
        
        return classRepository.batchDeleteClasses(ids)
    }
}