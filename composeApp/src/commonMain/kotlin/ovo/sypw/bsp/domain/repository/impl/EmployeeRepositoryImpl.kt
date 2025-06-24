package ovo.sypw.bsp.domain.repository.impl

import ovo.sypw.bsp.data.api.EmployeeApiService
import ovo.sypw.bsp.data.dto.EmployeeCreateDto
import ovo.sypw.bsp.data.dto.EmployeeDto
import ovo.sypw.bsp.data.dto.EmployeeImportDto
import ovo.sypw.bsp.data.dto.EmployeeUpdateDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.isSuccess
import ovo.sypw.bsp.data.dto.result.parseData
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.repository.EmployeeRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 员工管理仓库实现类
 * 整合网络API和本地存储，提供完整的员工管理功能
 */
class EmployeeRepositoryImpl(
    private val employeeApiService: EmployeeApiService,
    private val tokenStorage: TokenStorage
) : EmployeeRepository {

    companion object {
        private const val TAG = "EmployeeRepositoryImpl"
    }

    /**
     * 获取认证令牌
     * @return 认证令牌，如果未登录则返回null
     */
    private suspend fun getAuthToken(): String? {
        return tokenStorage.getAccessToken()
    }

    /**
     * 获取员工分页列表
     */
    override suspend fun getEmployeePage(
        current: Int,
        size: Int,
        username: String?,
        realName: String?,
        gender: Int?,
        job: Int?,
        departmentId: Int?,
        entryDateStart: String?,
        entryDateEnd: String?
    ): NetworkResult<PageResultDto<EmployeeDto>> {
//        Logger.d(TAG, "获取员工分页列表: current=$current, size=$size")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取员工分页列表失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = employeeApiService.getEmployeePage(
            current,
            size,
            username,
            realName,
            gender,
            job,
            departmentId,
            entryDateStart,
            entryDateEnd,
            token
        )) {
            is NetworkResult.Success -> {
//                Logger.i(TAG, "获取员工分页列表请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val pageResult = saResult.parseData<PageResultDto<EmployeeDto>>()
                    if (pageResult != null) {
//                        Logger.i(TAG, "员工分页数据解析成功: ${pageResult.records.size}条记录")
                        NetworkResult.Success(pageResult)
                    } else {
                        Logger.w(TAG, "员工分页数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "员工数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取员工分页列表失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取员工分页列表网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 获取员工详情
     */
    override suspend fun getEmployeeById(id: Int): NetworkResult<EmployeeDto> {
        Logger.d(TAG, "获取员工详情: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取员工详情失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = employeeApiService.getEmployeeById(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取员工详情请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val employee = saResult.parseData<EmployeeDto>()
                    if (employee != null) {
                        Logger.i(TAG, "员工详情数据解析成功: ${employee.realName}")
                        NetworkResult.Success(employee)
                    } else {
                        Logger.w(TAG, "员工详情数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "员工详情数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取员工详情失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取员工详情网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 创建员工
     */
    override suspend fun createEmployee(employeeCreateDto: EmployeeCreateDto): NetworkResult<Unit> {
        Logger.d(
            TAG,
            "创建员工: username=${employeeCreateDto.username}, realName=${employeeCreateDto.realName}"
        )

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "创建员工失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = employeeApiService.createEmployee(employeeCreateDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "创建员工请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "员工创建成功: ${employeeCreateDto.realName}")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "创建员工失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "创建员工网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 更新员工
     */
    override suspend fun updateEmployee(employeeUpdateDto: EmployeeUpdateDto): NetworkResult<Unit> {
        Logger.d(
            TAG,
            "更新员工: id=${employeeUpdateDto.id}, realName=${employeeUpdateDto.realName}"
        )

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "更新员工失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = employeeApiService.updateEmployee(employeeUpdateDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "更新员工请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(
                        TAG,
                        "员工更新成功: id=${employeeUpdateDto.id}, realName=${employeeUpdateDto.realName}"
                    )
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "更新员工失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "更新员工网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 删除员工
     */
    override suspend fun deleteEmployee(id: Int): NetworkResult<Unit> {
        Logger.d(TAG, "删除员工: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "删除员工失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = employeeApiService.deleteEmployee(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "删除员工请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "员工删除成功: id=$id")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "删除员工失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "删除员工网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 批量删除员工
     */
    override suspend fun batchDeleteEmployees(ids: List<Int>): NetworkResult<Unit> {
        Logger.d(TAG, "批量删除员工: ids=$ids")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "批量删除员工失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = employeeApiService.batchDeleteEmployees(ids, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "批量删除员工请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "员工批量删除成功: ${ids.size}个员工")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "批量删除员工失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "批量删除员工网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 批量导入员工
     */
    override suspend fun importEmployees(file: ByteArray): NetworkResult<EmployeeImportDto> {
        Logger.d(TAG, "批量导入员工: 文件大小=${file.size}字节")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "批量导入员工失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = employeeApiService.importEmployees(file, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "批量导入员工请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val importResult = saResult.parseData<EmployeeImportDto>()
                    if (importResult != null) {
                        Logger.i(
                            TAG,
                            "员工导入成功: 成功${importResult.successCount}个，失败${importResult.failureCount}个"
                        )
                        NetworkResult.Success(importResult)
                    } else {
                        Logger.w(TAG, "员工导入结果解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "导入结果解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "批量导入员工失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "批量导入员工网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 批量导出员工
     */
    override suspend fun exportEmployees(
        username: String?,
        realName: String?,
        gender: Int?,
        job: Int?,
        departmentId: Int?,
        entryDateStart: String?,
        entryDateEnd: String?
    ): NetworkResult<ByteArray> {
        Logger.d(TAG, "批量导出员工")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "批量导出员工失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = employeeApiService.exportEmployees(
            username, realName, gender, job, departmentId, entryDateStart, entryDateEnd, token
        )) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "批量导出员工请求成功")
                val fileData = result.data
                Logger.i(TAG, "员工导出成功: 文件大小=${fileData.size}字节")
                NetworkResult.Success(fileData)
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "批量导出员工网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
}