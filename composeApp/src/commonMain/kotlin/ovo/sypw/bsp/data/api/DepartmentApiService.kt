package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.data.dto.DepartmentCreateDto
import ovo.sypw.bsp.data.dto.DepartmentUpdateDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.utils.Logger

/**
 * 部门管理API服务
 * 提供部门相关的所有API调用方法
 */
class DepartmentApiService : BaseApiService() {

    companion object {
        private const val TAG = "DepartmentApiService"

        // API端点常量
        private const val DEPARTMENTS_ENDPOINT = "/departments"
        private const val DEPARTMENTS_PAGE_ENDPOINT = "/departments/page"
        private const val DEPARTMENTS_BATCH_ENDPOINT = "/departments/batch"
    }

    /**
     * 获取部门分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 部门名称（可选，用于搜索）
     * @param token 认证令牌
     * @return 部门分页数据
     */
    suspend fun getDepartmentPage(
        current: Int = 1,
        size: Int = 9,
        name: String? = null,
        token: String
    ): NetworkResult<SaResult> {
//        Logger.d(TAG, "获取部门分页列表: current=$current, size=$size, name=$name")

        val parameters = mutableMapOf<String, Any>(
            "current" to current,
            "size" to size
        )

        // 如果提供了名称搜索参数，则添加到请求中
        name?.let { parameters["name"] = it }

        return getWithToken(
            endpoint = DEPARTMENTS_PAGE_ENDPOINT,
            token = token,
            parameters = parameters
        )
    }

    /**
     * 获取部门详情
     * @param id 部门ID
     * @param token 认证令牌
     * @return 部门详情数据
     */
    suspend fun getDepartmentById(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取部门详情: id=$id")

        return getWithToken(
            endpoint = "$DEPARTMENTS_ENDPOINT/$id",
            token = token
        )
    }

    /**
     * 创建部门
     * @param departmentCreateDto 创建部门请求数据
     * @param token 认证令牌
     * @return 创建结果
     */
    suspend fun createDepartment(
        departmentCreateDto: DepartmentCreateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "创建部门: name=${departmentCreateDto.name}")

        return postWithToken(
            endpoint = DEPARTMENTS_ENDPOINT,
            token = token,
            body = departmentCreateDto
        )
    }

    /**
     * 更新部门
     * @param departmentUpdateDto 更新部门请求数据
     * @param token 认证令牌
     * @return 更新结果
     */
    suspend fun updateDepartment(
        departmentUpdateDto: DepartmentUpdateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "更新部门: id=${departmentUpdateDto.id}, name=${departmentUpdateDto.name}")

        return putWithToken(
            endpoint = DEPARTMENTS_ENDPOINT,
            token = token,
            body = departmentUpdateDto
        )
    }

    /**
     * 删除部门
     * @param id 部门ID
     * @param token 认证令牌
     * @return 删除结果
     */
    suspend fun deleteDepartment(
        id: Int?,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "删除部门: id=$id")

        return deleteWithToken(
            endpoint = "$DEPARTMENTS_ENDPOINT/$id",
            token = token
        )
    }

    /**
     * 批量删除部门
     * @param ids 部门ID列表
     * @param token 认证令牌
     * @return 批量删除结果
     */
    suspend fun batchDeleteDepartments(
        ids: List<Int>,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "批量删除部门: ids=$ids")

        return deleteWithToken(
            endpoint = DEPARTMENTS_BATCH_ENDPOINT,
            token = token,
            parameters = mapOf("ids" to ids)
        )
    }
}