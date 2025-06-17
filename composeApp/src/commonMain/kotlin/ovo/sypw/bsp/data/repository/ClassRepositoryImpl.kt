package ovo.sypw.bsp.data.repository

import ovo.sypw.bsp.data.api.ClassApiService
import ovo.sypw.bsp.data.dto.ClassCreateDto
import ovo.sypw.bsp.data.dto.ClassDto
import ovo.sypw.bsp.data.dto.ClassUpdateDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.isSuccess
import ovo.sypw.bsp.data.dto.result.parseData
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.repository.ClassRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 班级管理仓库实现类
 * 整合网络API和本地存储，提供完整的班级管理功能
 */
class ClassRepositoryImpl(
    private val classApiService: ClassApiService,
    private val tokenStorage: TokenStorage
) : ClassRepository {

    companion object {
        private const val TAG = "ClassRepositoryImpl"
    }

    /**
     * 获取认证令牌
     * @return 认证令牌，如果未登录则返回null
     */
    private suspend fun getAuthToken(): String? {
        return tokenStorage.getAccessToken()
    }

    /**
     * 获取班级分页列表
     */
    override suspend fun getClassPage(
        current: Int,
        size: Int,
        name: String?
    ): NetworkResult<PageResultDto<ClassDto>> {
//        Logger.d(TAG, "获取班级分页列表: current=$current, size=$size, name=$name")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取班级分页列表失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = classApiService.getClassPage(current, size, name, token)) {
            is NetworkResult.Success -> {
//                Logger.i(TAG, "获取班级分页列表请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val pageResult = saResult.parseData<PageResultDto<ClassDto>>()
                    if (pageResult != null) {
//                        Logger.i(TAG, "班级分页数据解析成功: ${pageResult.records.size}条记录")
                        NetworkResult.Success(pageResult)
                    } else {
                        Logger.w(TAG, "班级分页数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "班级数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取班级分页列表失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取班级分页列表网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 获取班级详情
     */
    override suspend fun getClassById(id: Int): NetworkResult<ClassDto> {
        Logger.d(TAG, "获取班级详情: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取班级详情失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = classApiService.getClassById(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取班级详情请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val classData = saResult.parseData<ClassDto>()
                    if (classData != null) {
                        Logger.i(TAG, "班级详情数据解析成功: ${classData.name}")
                        NetworkResult.Success(classData)
                    } else {
                        Logger.w(TAG, "班级详情数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "班级详情数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取班级详情失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取班级详情网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 创建班级
     */
    override suspend fun createClass(name: String, grade: String): NetworkResult<Unit> {
        Logger.d(TAG, "创建班级: name=$name, grade=$grade")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "创建班级失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        val createDto = ClassCreateDto(name = name, grade = grade)

        return when (val result = classApiService.createClass(createDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "创建班级请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "班级创建成功: $name, grade=$grade")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "创建班级失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "创建班级网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 更新班级
     */
    override suspend fun updateClass(id: Int, name: String, grade: String): NetworkResult<Unit> {
        Logger.d(TAG, "更新班级: id=$id, name=$name, grade=$grade")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "更新班级失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        val updateDto = ClassUpdateDto(id = id, name = name, grade = grade)

        return when (val result = classApiService.updateClass(updateDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "更新班级请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "班级更新成功: id=$id, name=$name, grade=$grade")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "更新班级失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "更新班级网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 删除班级
     */
    override suspend fun deleteClass(id: Int?): NetworkResult<Unit> {
        Logger.d(TAG, "删除班级: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "删除班级失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = classApiService.deleteClass(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "删除班级请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "班级删除成功: id=$id")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "删除班级失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "删除班级网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 批量删除班级
     */
    override suspend fun batchDeleteClasses(ids: List<Int>): NetworkResult<Unit> {
        Logger.d(TAG, "批量删除班级: ids=$ids")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "批量删除班级失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = classApiService.batchDeleteClasses(ids, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "批量删除班级请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "班级批量删除成功: ${ids.size}个班级")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "批量删除班级失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "批量删除班级网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
}