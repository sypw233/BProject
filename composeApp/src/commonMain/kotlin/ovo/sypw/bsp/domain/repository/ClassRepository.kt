package ovo.sypw.bsp.domain.repository

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult

/**
 * 班级管理仓库接口
 * 定义班级管理相关的业务操作
 */
interface ClassRepository : BaseRepository {
    
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
    ): NetworkResult<PageResultDto<ClassDto>>
    
    /**
     * 获取班级详情
     * @param id 班级ID
     * @return 班级详情数据结果
     */
    suspend fun getClassById(id: Int): NetworkResult<ClassDto>
    
    /**
     * 创建班级
     * @param name 班级名称
     * @param grade 年级
     * @return 创建结果
     */
    suspend fun createClass(name: String, grade: String): NetworkResult<Unit>
    
    /**
     * 更新班级
     * @param id 班级ID
     * @param name 班级名称
     * @param grade 年级
     * @return 更新结果
     */
    suspend fun updateClass(id: Int, name: String, grade: String): NetworkResult<Unit>
    
    /**
     * 删除班级
     * @param id 班级ID
     * @return 删除结果
     */
    suspend fun deleteClass(id: Int?): NetworkResult<Unit>
    
    /**
     * 批量删除班级
     * @param ids 班级ID列表
     * @return 批量删除结果
     */
    suspend fun batchDeleteClasses(ids: List<Int>): NetworkResult<Unit>
}