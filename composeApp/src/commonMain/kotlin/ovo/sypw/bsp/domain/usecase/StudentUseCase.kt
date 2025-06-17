package ovo.sypw.bsp.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.StudentCreateDto
import ovo.sypw.bsp.data.dto.StudentDto
import ovo.sypw.bsp.data.dto.StudentUpdateDto
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.repository.StudentRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 学生管理用例类
 * 封装学生相关的业务逻辑，提供给表现层使用
 */
class StudentUseCase(
    private val studentRepository: StudentRepository
) {

    companion object {
        private const val TAG = "StudentUseCase"
    }

    /**
     * 获取学生分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 学生姓名（可选，模糊查询）
     * @param gender 性别筛选（可选）
     * @param classId 班级ID筛选（可选）
     * @param status 状态筛选（可选）
     * @param birthDateStart 出生日期范围开始（可选）
     * @param birthDateEnd 出生日期范围结束（可选）
     * @param joinDateStart 入学日期范围开始（可选）
     * @param joinDateEnd 入学日期范围结束（可选）
     * @return Flow<NetworkResult<PageResultDto<StudentDto>>>
     */
    fun getStudentPage(
        current: Int = 1,
        size: Int = 9,
        name: String? = null,
        gender: Int? = null,
        classId: Int? = null,
        status: Int? = null,
        birthDateStart: String? = null,
        birthDateEnd: String? = null,
        joinDateStart: String? = null,
        joinDateEnd: String? = null
    ): Flow<NetworkResult<PageResultDto<StudentDto>>> = flow {
        Logger.d(TAG, "开始获取学生分页列表: current=$current, size=$size")

        emit(NetworkResult.Loading)

        try {
            val result = studentRepository.getStudentPage(
                current = current,
                size = size,
                name = name,
                gender = gender,
                classId = classId,
                status = status,
                birthDateStart = birthDateStart,
                birthDateEnd = birthDateEnd,
                joinDateStart = joinDateStart,
                joinDateEnd = joinDateEnd
            )

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "获取学生分页列表成功: ${result.data.records.size}条记录")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "获取学生分页列表失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "获取学生分页列表异常: ${e.message}")
            emit(
                NetworkResult.Error(
                    exception = e,
                    message = e.message ?: "获取学生列表失败"
                )
            )
        }
    }

    /**
     * 获取学生详情
     * @param id 学生ID
     * @return Flow<NetworkResult<StudentDto>>
     */
    fun getStudentById(id: Int): Flow<NetworkResult<StudentDto>> = flow {
        Logger.d(TAG, "开始获取学生详情: id=$id")

        emit(NetworkResult.Loading)

        try {
            val result = studentRepository.getStudentById(id)

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "获取学生详情成功: ${result.data.name}")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "获取学生详情失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "获取学生详情异常: ${e.message}")
            emit(
                NetworkResult.Error(
                    exception = e,
                    message = e.message ?: "获取学生详情失败"
                )
            )
        }
    }

    /**
     * 创建学生
     * @param studentCreateDto 学生创建数据
     * @return Flow<NetworkResult<Unit>>
     */
    fun createStudent(studentCreateDto: StudentCreateDto): Flow<NetworkResult<Unit>> = flow {
        Logger.d(TAG, "开始创建学生: name=${studentCreateDto.name}")

        emit(NetworkResult.Loading)

        try {
            // 验证输入数据
            val validationResult = validateStudentCreateData(studentCreateDto)
            if (validationResult != null) {
                Logger.w(TAG, "学生创建数据验证失败: $validationResult")
                emit(
                    NetworkResult.Error(
                        exception = IllegalArgumentException(validationResult),
                        message = validationResult
                    )
                )
                return@flow
            }

            val result = studentRepository.createStudent(studentCreateDto)

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "创建学生成功: ${studentCreateDto.name}")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "创建学生失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "创建学生异常: ${e.message}")
            emit(
                NetworkResult.Error(
                    exception = e,
                    message = e.message ?: "创建学生失败"
                )
            )
        }
    }

    /**
     * 更新学生
     * @param studentUpdateDto 学生更新数据
     * @return Flow<NetworkResult<Unit>>
     */
    fun updateStudent(studentUpdateDto: StudentUpdateDto): Flow<NetworkResult<Unit>> = flow {
        Logger.d(TAG, "开始更新学生: id=${studentUpdateDto.id}, name=${studentUpdateDto.name}")

        emit(NetworkResult.Loading)

        try {
            // 验证输入数据
            val validationResult = validateStudentUpdateData(studentUpdateDto)
            if (validationResult != null) {
                Logger.w(TAG, "学生更新数据验证失败: $validationResult")
                emit(
                    NetworkResult.Error(
                        exception = IllegalArgumentException(validationResult),
                        message = validationResult
                    )
                )
                return@flow
            }

            val result = studentRepository.updateStudent(studentUpdateDto)

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(
                        TAG,
                        "更新学生成功: id=${studentUpdateDto.id}, name=${studentUpdateDto.name}"
                    )
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "更新学生失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "更新学生异常: ${e.message}")
            emit(
                NetworkResult.Error(
                    exception = e,
                    message = e.message ?: "更新学生失败"
                )
            )
        }
    }

    /**
     * 删除学生
     * @param id 学生ID
     * @return Flow<NetworkResult<Unit>>
     */
    fun deleteStudent(id: Int): Flow<NetworkResult<Unit>> = flow {
        Logger.d(TAG, "开始删除学生: id=$id")

        emit(NetworkResult.Loading)

        try {
            if (id <= 0) {
                Logger.w(TAG, "学生ID无效: $id")
                emit(
                    NetworkResult.Error(
                        exception = IllegalArgumentException("学生ID无效"),
                        message = "学生ID无效"
                    )
                )
                return@flow
            }

            val result = studentRepository.deleteStudent(id)

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "删除学生成功: id=$id")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "删除学生失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "删除学生异常: ${e.message}")
            emit(
                NetworkResult.Error(
                    exception = e,
                    message = e.message ?: "删除学生失败"
                )
            )
        }
    }

    /**
     * 批量删除学生
     * @param ids 学生ID列表
     * @return Flow<NetworkResult<Unit>>
     */
    fun batchDeleteStudents(ids: List<Int>): Flow<NetworkResult<Unit>> = flow {
        Logger.d(TAG, "开始批量删除学生: ids=$ids")

        emit(NetworkResult.Loading)

        try {
            if (ids.isEmpty()) {
                Logger.w(TAG, "学生ID列表为空")
                emit(
                    NetworkResult.Error(
                        exception = IllegalArgumentException("请选择要删除的学生"),
                        message = "请选择要删除的学生"
                    )
                )
                return@flow
            }

            if (ids.any { it <= 0 }) {
                Logger.w(TAG, "学生ID列表包含无效ID: $ids")
                emit(
                    NetworkResult.Error(
                        exception = IllegalArgumentException("学生ID无效"),
                        message = "学生ID无效"
                    )
                )
                return@flow
            }

            val result = studentRepository.batchDeleteStudents(ids)

            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "批量删除学生成功: ${ids.size}个学生")
                    emit(result)
                }

                is NetworkResult.Error -> {
                    Logger.e(TAG, "批量删除学生失败: ${result.message}")
                    emit(result)
                }

                else -> emit(result)
            }
        } catch (e: Exception) {
            Logger.e(TAG, "批量删除学生异常: ${e.message}")
            emit(
                NetworkResult.Error(
                    exception = e,
                    message = e.message ?: "批量删除学生失败"
                )
            )
        }
    }


    /**
     * 验证学生创建数据
     * @param studentCreateDto 学生创建数据
     * @return 验证错误信息，如果验证通过则返回null
     */
    private fun validateStudentCreateData(studentCreateDto: StudentCreateDto): String? {
        if (studentCreateDto.name.isBlank()) {
            return "学生姓名不能为空"
        }

        if (studentCreateDto.name.length > 50) {
            return "学生姓名长度不能超过50个字符"
        }

        if (studentCreateDto.gender !in listOf(1, 2)) {
            return "性别值无效，必须为1（男）或2（女）"
        }

        if (studentCreateDto.classId <= 0) {
            return "班级ID无效"
        }

        if (studentCreateDto.status !in listOf(1, 2, 3, 4)) {
            return "状态值无效，必须为1（在校）、2（休学）、3（退学）或4（毕业）"
        }

        // 验证日期格式（简单验证）
        if (!isValidDateFormat(studentCreateDto.birthDate)) {
            return "出生日期格式无效，请使用yyyy-MM-dd格式"
        }

        if (!isValidDateFormat(studentCreateDto.joinDate)) {
            return "入学日期格式无效，请使用yyyy-MM-dd格式"
        }

        return null
    }

    /**
     * 验证学生更新数据
     * @param studentUpdateDto 学生更新数据
     * @return 验证错误信息，如果验证通过则返回null
     */
    private fun validateStudentUpdateData(studentUpdateDto: StudentUpdateDto): String? {
        if (studentUpdateDto.id <= 0) {
            return "学生ID无效"
        }

        if (studentUpdateDto.name.isBlank()) {
            return "学生姓名不能为空"
        }

        if (studentUpdateDto.name.length > 50) {
            return "学生姓名长度不能超过50个字符"
        }

        if (studentUpdateDto.gender !in listOf(1, 2)) {
            return "性别值无效，必须为1（男）或2（女）"
        }

        if (studentUpdateDto.classId <= 0) {
            return "班级ID无效"
        }

        if (studentUpdateDto.status !in listOf(1, 2, 3, 4)) {
            return "状态值无效，必须为1（在校）、2（休学）、3（退学）或4（毕业）"
        }

        // 验证日期格式（简单验证）
        if (!isValidDateFormat(studentUpdateDto.birthDate)) {
            return "出生日期格式无效，请使用yyyy-MM-dd格式"
        }

        if (!isValidDateFormat(studentUpdateDto.joinDate)) {
            return "入学日期格式无效，请使用yyyy-MM-dd格式"
        }

        return null
    }

    /**
     * 验证日期格式
     * @param date 日期字符串
     * @return 是否为有效的日期格式
     */
    private fun isValidDateFormat(date: String): Boolean {
        // 简单的日期格式验证：yyyy-MM-dd
        val dateRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        return dateRegex.matches(date)
    }
}