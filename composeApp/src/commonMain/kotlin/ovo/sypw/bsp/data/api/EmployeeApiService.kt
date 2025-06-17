package ovo.sypw.bsp.data.api

import ovo.sypw.bsp.data.dto.EmployeeCreateDto
import ovo.sypw.bsp.data.dto.EmployeeUpdateDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.SaResult
import ovo.sypw.bsp.utils.Logger

/**
 * 员工管理API服务
 * 提供员工相关的所有API调用方法
 */
class EmployeeApiService : BaseApiService() {

    companion object {
        private const val TAG = "EmployeeApiService"

        // API端点常量
        private const val EMPLOYEES_ENDPOINT = "/employees"
        private const val EMPLOYEES_PAGE_ENDPOINT = "/employees/page"
        private const val EMPLOYEES_BATCH_ENDPOINT = "/employees/batch"
        private const val EMPLOYEES_IMPORT_ENDPOINT = "/employees/import"
        private const val EMPLOYEES_EXPORT_ENDPOINT = "/employees/export"
    }

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
     * @param token 认证令牌
     * @return 员工分页数据
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
        entryDateEnd: String? = null,
        token: String
    ): NetworkResult<SaResult> {
//        Logger.d(TAG, "获取员工分页列表: current=$current, size=$size")

        val parameters = mutableMapOf<String, Any>(
            "current" to current,
            "size" to size
        )

        // 添加可选的搜索和筛选参数
        username?.let { parameters["username"] = it }
        realName?.let { parameters["realName"] = it }
        gender?.let { parameters["gender"] = it }
        job?.let { parameters["job"] = it }
        departmentId?.let { parameters["departmentId"] = it }
        entryDateStart?.let { parameters["entryDateStart"] = it }
        entryDateEnd?.let { parameters["entryDateEnd"] = it }

        return getWithToken(
            endpoint = EMPLOYEES_PAGE_ENDPOINT,
            token = token,
            parameters = parameters
        )
    }

    /**
     * 获取员工详情
     * @param id 员工ID
     * @param token 认证令牌
     * @return 员工详情数据
     */
    suspend fun getEmployeeById(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "获取员工详情: id=$id")

        return getWithToken(
            endpoint = "$EMPLOYEES_ENDPOINT/$id",
            token = token
        )
    }

    /**
     * 创建员工
     * @param employeeCreateDto 创建员工请求数据
     * @param token 认证令牌
     * @return 创建结果
     */
    suspend fun createEmployee(
        employeeCreateDto: EmployeeCreateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(
            TAG,
            "创建员工: username=${employeeCreateDto.username}, realName=${employeeCreateDto.realName}"
        )

        return postWithToken(
            endpoint = EMPLOYEES_ENDPOINT,
            token = token,
            body = employeeCreateDto
        )
    }

    /**
     * 更新员工
     * @param employeeUpdateDto 更新员工请求数据
     * @param token 认证令牌
     * @return 更新结果
     */
    suspend fun updateEmployee(
        employeeUpdateDto: EmployeeUpdateDto,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(
            TAG,
            "更新员工: id=${employeeUpdateDto.id}, username=${employeeUpdateDto.username}"
        )

        return putWithToken(
            endpoint = EMPLOYEES_ENDPOINT,
            token = token,
            body = employeeUpdateDto
        )
    }

    /**
     * 删除员工
     * @param id 员工ID
     * @param token 认证令牌
     * @return 删除结果
     */
    suspend fun deleteEmployee(
        id: Int,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "删除员工: id=$id")

        return deleteWithToken(
            endpoint = "$EMPLOYEES_ENDPOINT/$id",
            token = token
        )
    }

    /**
     * 批量删除员工
     * @param ids 员工ID列表
     * @param token 认证令牌
     * @return 批量删除结果
     */
    suspend fun batchDeleteEmployees(
        ids: List<Int>,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "批量删除员工: ids=$ids")

        return deleteWithToken(
            endpoint = EMPLOYEES_BATCH_ENDPOINT,
            token = token,
            parameters = mapOf("ids" to ids)
        )
    }

    /**
     * 批量导入员工
     * @param file 导入文件数据
     * @param token 认证令牌
     * @return 导入结果
     */
    suspend fun importEmployees(
        file: ByteArray,
        token: String
    ): NetworkResult<SaResult> {
        Logger.d(TAG, "批量导入员工: 文件大小=${file.size}字节")

        // 注意：这里需要实现文件上传的逻辑
        // 由于BaseApiService可能不支持文件上传，这里先返回一个占位实现
        return postWithToken(
            endpoint = EMPLOYEES_IMPORT_ENDPOINT,
            token = token,
            body = mapOf("file" to file)
        )
    }

    /**
     * 批量导出员工
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param gender 性别（可选）
     * @param job 职位（可选）
     * @param departmentId 部门ID（可选）
     * @param entryDateStart 入职开始日期（可选）
     * @param entryDateEnd 入职结束日期（可选）
     * @param token 认证令牌
     * @return 导出文件数据结果
     */
    suspend fun exportEmployees(
        username: String? = null,
        realName: String? = null,
        gender: Int? = null,
        job: Int? = null,
        departmentId: Int? = null,
        entryDateStart: String? = null,
        entryDateEnd: String? = null,
        token: String
    ): NetworkResult<ByteArray> {
        Logger.d(TAG, "批量导出员工")

        val parameters = mutableMapOf<String, Any>()

        // 添加可选的搜索和筛选参数
        username?.let { parameters["username"] = it }
        realName?.let { parameters["realName"] = it }
        gender?.let { parameters["gender"] = it }
        job?.let { parameters["job"] = it }
        departmentId?.let { parameters["departmentId"] = it }
        entryDateStart?.let { parameters["entryDateStart"] = it }
        entryDateEnd?.let { parameters["entryDateEnd"] = it }

        return getFileWithToken(
            endpoint = EMPLOYEES_EXPORT_ENDPOINT,
            token = token,
            parameters = parameters
        )
    }
}