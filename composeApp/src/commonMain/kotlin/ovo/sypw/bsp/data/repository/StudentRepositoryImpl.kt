package ovo.sypw.bsp.data.repository

import ovo.sypw.bsp.data.api.StudentApiService
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.StudentCreateDto
import ovo.sypw.bsp.data.dto.StudentDto
import ovo.sypw.bsp.data.dto.StudentImportDto
import ovo.sypw.bsp.data.dto.StudentUpdateDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.data.dto.result.isSuccess
import ovo.sypw.bsp.data.dto.result.parseData
import ovo.sypw.bsp.data.storage.TokenStorage
import ovo.sypw.bsp.domain.repository.StudentRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 学生管理仓库实现类
 * 整合网络API和本地存储，提供完整的学生管理功能
 */
class StudentRepositoryImpl(
    private val studentApiService: StudentApiService,
    private val tokenStorage: TokenStorage
) : StudentRepository {

    companion object {
        private const val TAG = "StudentRepositoryImpl"
    }

    /**
     * 获取认证令牌
     * @return 认证令牌，如果未登录则返回null
     */
    private suspend fun getAuthToken(): String? {
        return tokenStorage.getAccessToken()
    }

    /**
     * 获取学生分页列表
     */
    override suspend fun getStudentPage(
        current: Int,
        size: Int,
        name: String?,
        gender: Int?,
        classId: Int?,
        status: Int?,
        birthDateStart: String?,
        birthDateEnd: String?,
        joinDateStart: String?,
        joinDateEnd: String?
    ): NetworkResult<PageResultDto<StudentDto>> {
//        Logger.d(TAG, "获取学生分页列表: current=$current, size=$size")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取学生分页列表失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = studentApiService.getStudentPage(
            current,
            size,
            name,
            gender,
            classId,
            status,
            birthDateStart,
            birthDateEnd,
            joinDateStart,
            joinDateEnd,
            token
        )) {
            is NetworkResult.Success -> {
//                Logger.i(TAG, "获取学生分页列表请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val pageResult = saResult.parseData<PageResultDto<StudentDto>>()
                    if (pageResult != null) {
//                        Logger.i(TAG, "学生分页数据解析成功: ${pageResult.records.size}条记录")
                        NetworkResult.Success(pageResult)
                    } else {
                        Logger.w(TAG, "学生分页数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "学生数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取学生分页列表失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取学生分页列表网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 获取学生详情
     */
    override suspend fun getStudentById(id: Int): NetworkResult<StudentDto> {
        Logger.d(TAG, "获取学生详情: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "获取学生详情失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = studentApiService.getStudentById(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "获取学生详情请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val student = saResult.parseData<StudentDto>()
                    if (student != null) {
                        Logger.i(TAG, "学生详情数据解析成功: ${student.name}")
                        NetworkResult.Success(student)
                    } else {
                        Logger.w(TAG, "学生详情数据解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "学生详情数据解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "获取学生详情失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "获取学生详情网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 创建学生
     */
    override suspend fun createStudent(studentCreateDto: StudentCreateDto): NetworkResult<Unit> {
        Logger.d(TAG, "创建学生: name=${studentCreateDto.name}")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "创建学生失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = studentApiService.createStudent(studentCreateDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "创建学生请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "学生创建成功: ${studentCreateDto.name}")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "创建学生失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "创建学生网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 更新学生
     */
    override suspend fun updateStudent(studentUpdateDto: StudentUpdateDto): NetworkResult<Unit> {
        Logger.d(TAG, "更新学生: id=${studentUpdateDto.id}, name=${studentUpdateDto.name}")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "更新学生失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = studentApiService.updateStudent(studentUpdateDto, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "更新学生请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(
                        TAG,
                        "学生更新成功: id=${studentUpdateDto.id}, name=${studentUpdateDto.name}"
                    )
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "更新学生失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "更新学生网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 删除学生
     */
    override suspend fun deleteStudent(id: Int): NetworkResult<Unit> {
        Logger.d(TAG, "删除学生: id=$id")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "删除学生失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = studentApiService.deleteStudent(id, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "删除学生请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "学生删除成功: id=$id")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "删除学生失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "删除学生网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 批量删除学生
     */
    override suspend fun batchDeleteStudents(ids: List<Int>): NetworkResult<Unit> {
        Logger.d(TAG, "批量删除学生: ids=$ids")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "批量删除学生失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = studentApiService.batchDeleteStudents(ids, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "批量删除学生请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    Logger.i(TAG, "学生批量删除成功: ${ids.size}个学生")
                    NetworkResult.Success(Unit)
                } else {
                    Logger.w(TAG, "批量删除学生失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "批量删除学生网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 批量导入学生
     */
    override suspend fun importStudents(file: ByteArray): NetworkResult<StudentImportDto> {
        Logger.d(TAG, "批量导入学生: 文件大小=${file.size}字节")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "批量导入学生失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = studentApiService.importStudents(file, token)) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "批量导入学生请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    val importResult = saResult.parseData<StudentImportDto>()
                    if (importResult != null) {
                        Logger.i(
                            TAG,
                            "学生导入成功: 成功${importResult.successCount}个，失败${importResult.failureCount}个"
                        )
                        NetworkResult.Success(importResult)
                    } else {
                        Logger.w(TAG, "学生导入结果解析失败")
                        NetworkResult.Error(
                            exception = Exception("数据解析失败"),
                            message = "导入结果解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "批量导入学生失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "批量导入学生网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }

    /**
     * 批量导出学生
     */
    override suspend fun exportStudents(
        name: String?,
        gender: Long?,
        classId: Long?,
        status: Long?,
        birthDateStart: String?,
        birthDateEnd: String?,
        joinDateStart: String?,
        joinDateEnd: String?
    ): NetworkResult<ByteArray> {
        Logger.d(TAG, "批量导出学生")

        val token = getAuthToken()
        if (token == null) {
            Logger.w(TAG, "批量导出学生失败: 未找到认证令牌")
            return NetworkResult.Error(
                exception = Exception("未登录"),
                message = "请先登录"
            )
        }

        return when (val result = studentApiService.exportStudents(
            name,
            gender,
            classId,
            status,
            birthDateStart,
            birthDateEnd,
            joinDateStart,
            joinDateEnd,
            token
        )) {
            is NetworkResult.Success -> {
                Logger.i(TAG, "批量导出学生请求成功")
                val saResult = result.data
                if (saResult.isSuccess()) {
                    // 注意：这里需要根据实际API返回格式处理文件数据
                    // 假设API返回的是Base64编码的文件数据或直接的字节数组
                    val fileData = saResult.parseData<ByteArray>()
                    if (fileData != null) {
                        Logger.i(TAG, "学生导出成功: 文件大小=${fileData.size}字节")
                        NetworkResult.Success(fileData)
                    } else {
                        Logger.w(TAG, "学生导出文件解析失败")
                        NetworkResult.Error(
                            exception = Exception("文件解析失败"),
                            message = "导出文件解析失败"
                        )
                    }
                } else {
                    Logger.w(TAG, "批量导出学生失败: ${saResult.msg}")
                    NetworkResult.Error(
                        exception = Exception(saResult.msg),
                        message = saResult.msg
                    )
                }
            }

            is NetworkResult.Error -> {
                Logger.e(TAG, "批量导出学生网络请求失败: ${result.message}")
                result
            }

            is NetworkResult.Loading -> result
            is NetworkResult.Idle -> result
        }
    }
}