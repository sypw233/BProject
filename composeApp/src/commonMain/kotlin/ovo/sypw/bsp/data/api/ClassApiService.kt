package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.utils.Logger

/**
 * 班级管理API服务
 * 提供班级相关的所有API调用方法
 */
class ClassApiService : BaseApiService() {
    
    companion object {
        private const val TAG = "ClassApiService"
        
        // API端点常量
        private const val CLASSES_ENDPOINT = "/classes"
        private const val CLASSES_PAGE_ENDPOINT = "/classes/page"
        private const val CLASSES_BATCH_ENDPOINT = "/classes/batch"
    }
    
    /**
     * 获取班级分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 班级名称（可选，用于搜索）
     * @param token 认证令牌
     * @return 班级分页数据
     */
    suspend fun getClassPage(
        current: Int = 1,
        size: Int = 9,
        name: String? = null,
        token: String
    ): NetworkResult<SaResult> {
//        Logger.d(TAG, "获取班级分页列表: current=$current, size=$size, name=$name")
        
        val parameters = mutableMapOf<String, Any>(
            "current" to current,
            "size" to size
        )
        
        // 如果提供了名称搜索参数，则添加到请求中
        name?.let { parameters["name"] = it }
        
        return getWithToken(
            endpoint = CLASSES_PAGE_ENDPOINT,
            token = token,
            parameters = parameters
        )
    }
    
    /**
     * 获取班级详情
     * @param id 班级ID
     * @param token 认证令牌
     * @return 班级详情数据
     */
    suspend fun getClassById(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取班级详情: id=$id")
        
        return getWithToken(
            endpoint = "$CLASSES_ENDPOINT/$id",
            token = token
        )
    }
    
    /**
     * 创建班级
     * @param classCreateDto 创建班级请求数据
     * @param token 认证令牌
     * @return 创建结果
     */
    suspend fun createClass(
        classCreateDto: ClassCreateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "创建班级: name=${classCreateDto.name}")
        
        return postWithToken(
            endpoint = CLASSES_ENDPOINT,
            token = token,
            body = classCreateDto
        )
    }
    
    /**
     * 更新班级
     * @param classUpdateDto 更新班级请求数据
     * @param token 认证令牌
     * @return 更新结果
     */
    suspend fun updateClass(
        classUpdateDto: ClassUpdateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "更新班级: id=${classUpdateDto.id}, name=${classUpdateDto.name}")
        
        return putWithToken(
            endpoint = CLASSES_ENDPOINT,
            token = token,
            body = classUpdateDto
        )
    }
    
    /**
     * 删除班级
     * @param id 班级ID
     * @param token 认证令牌
     * @return 删除结果
     */
    suspend fun deleteClass(
        id: Int?,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "删除班级: id=$id")
        
        return deleteWithToken(
            endpoint = "$CLASSES_ENDPOINT/$id",
            token = token
        )
    }
    
    /**
     * 批量删除班级
     * @param ids 班级ID列表
     * @param token 认证令牌
     * @return 批量删除结果
     */
    suspend fun batchDeleteClasses(
        ids: List<Int>,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "批量删除班级: ids=$ids")
        
        return deleteWithToken(
            endpoint = CLASSES_BATCH_ENDPOINT,
            token = token,
            parameters = mapOf("ids" to ids)
        )
    }
}