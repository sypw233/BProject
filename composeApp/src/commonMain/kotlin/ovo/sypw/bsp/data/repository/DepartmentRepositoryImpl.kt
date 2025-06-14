package ovo.sypw.bsp.data.repository

import ovo.sypw.bsp.data.api.DepartmentApiService
import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.DepartmentRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 部门管理仓库实现类
 * 整合网络API和本地存储，提供完整的部门管理功能
 */
class DepartmentRepositoryImpl(
    private val departmentApiService: DepartmentApiService,
    private val tokenStorage: TokenStorage
) : DepartmentRepository {
    
    companion object {
        private const val TAG = "DepartmentRepositoryImpl"
    }
    
    /**
     * 获取认证令牌
     * @return 认证令牌，如果未登录则返回null
     */
    private suspend fun getAuthToken(): String? {
        return tokenStorage.getAccessToken()
    }
    
    /**
     * 获取部门分页列表
     */
    override suspend fun getDepartmentPage(
        current: Int,
        size: Int,
        name: String?
    ): NetworkResult<PageResultDto<DepartmentDto>> {
//        Logger.d(TAG, "获取部门分页列表: current=$current, size=$size, name=$name")
        
        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取部门分页列表失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }
        
        return when (val result = departmentApiService.getDepartmentPage(current, size, name, token)) {
            is NetworkResult.Success -> {
//                Logger.i(TAG, "获取部门分页列表请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val pageResult = saResult.parseData<PageResultDto<DepartmentDto>>()
                    if (pageResult != null) {
//                        Logger.i(TAG, "部门分页数据解析成功: ${pageResult.records.size}条记录")
                        NetworkResult.Success(pageResult)
                    } else {
                        Logger.w(TAG, "部门分页数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "部门数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取部门分页列表失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }
            is NetworkResult.Error -> {
                Logger.e(TAG, "获取部门分页列表网络请求失败: ${result.message}")
                result
            }
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 获取部门详情
     */
    override suspend fun getDepartmentById(id: Int): NetworkResult<DepartmentDto> {
        Logger.d(TAG, "获取部门详情: id=$id")
        
        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取部门详情失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }
        
        return when (val result = departmentApiService.getDepartmentById(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取部门详情请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val department = saResult.parseData<DepartmentDto>()
                    if (department != null) {
                        Logger.i(TAG, "部门详情数据解析成功: ${department.name}")
                        NetworkResult.Success(department)
                    } else {
                        Logger.w(TAG, "部门详情数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "部门详情数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取部门详情失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }
            is NetworkResult.Error -> {
                Logger.e(TAG, "获取部门详情网络请求失败: ${result.message}")
                result
            }
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 创建部门
     */
    override suspend fun createDepartment(name: String): NetworkResult<Unit> {
        Logger.d(TAG, "创建部门: name=$name")
        
        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "创建部门失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }
        
        val createDto = DepartmentCreateDto(name = name)
        
        return when (val result = departmentApiService.createDepartment(createDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "创建部门请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "部门创建成功: $name")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "创建部门失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }
            is NetworkResult.Error -> {
                Logger.e(TAG, "创建部门网络请求失败: ${result.message}")
                result
            }
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 更新部门
     */
    override suspend fun updateDepartment(id: Int, name: String): NetworkResult<Unit> {
        Logger.d(TAG, "更新部门: id=$id, name=$name")
        
        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "更新部门失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }
        
        val updateDto = DepartmentUpdateDto(id = id, name = name)
        
        return when (val result = departmentApiService.updateDepartment(updateDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "更新部门请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "部门更新成功: id=$id, name=$name")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "更新部门失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }
            is NetworkResult.Error -> {
                Logger.e(TAG, "更新部门网络请求失败: ${result.message}")
                result
            }
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 删除部门
     */
    override suspend fun deleteDepartment(id: Int?): NetworkResult<Unit> {
        Logger.d(TAG, "删除部门: id=$id")
        
        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "删除部门失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }
        
        return when (val result = departmentApiService.deleteDepartment(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "删除部门请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "部门删除成功: id=$id")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "删除部门失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }
            is NetworkResult.Error -> {
                Logger.e(TAG, "删除部门网络请求失败: ${result.message}")
                result
            }
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
    
    /**
     * 批量删除部门
     */
    override suspend fun batchDeleteDepartments(ids: List<Int>): NetworkResult<Unit> {
        Logger.d(TAG, "批量删除部门: ids=$ids")
        
        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "批量删除部门失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }
        
        return when (val result = departmentApiService.batchDeleteDepartments(ids, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "批量删除部门请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "部门批量删除成功: ${ids.size}个部门")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "批量删除部门失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }
            is NetworkResult.Error -> {
                Logger.e(TAG, "批量删除部门网络请求失败: ${result.message}")
                result
            }
            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
}