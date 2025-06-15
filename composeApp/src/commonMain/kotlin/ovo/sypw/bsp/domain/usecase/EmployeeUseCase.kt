package ovo.sypw.bsp.domain.usecase

import ovo.sypw.bsp.data.dto.*
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.repository.EmployeeRepository
import ovo.sypw.bsp.utils.Logger

/**
 * 员工管理业务逻辑用例类
 * 封装员工相关的业务操作，提供给ViewModel层调用
 */
class EmployeeUseCase(
    private val employeeRepository: EmployeeRepository
) {
    
    companion object {
        private const val TAG = "EmployeeUseCase"
    }
    
    /**
     * 获取员工分页列表
     * @param current 当前页码
     * @param size 每页大小
     * @param username 用户名筛选
     * @param realName 真实姓名筛选
     * @param gender 性别筛选 (1-男, 2-女)
     * @param job 职位筛选
     * @param departmentId 部门ID筛选
     * @param entryDateStart 入职日期开始
     * @param entryDateEnd 入职日期结束
     * @return 员工分页结果
     */
    suspend fun getEmployeePage(
        current: Int = 1,
        size: Int = 10,
        username: String? = null,
        realName: String? = null,
        gender: Int? = null,
        job: Int? = null,
        departmentId: Int? = null,
        entryDateStart: String? = null,
        entryDateEnd: String? = null
    ): NetworkResult<PageResultDto<EmployeeDto>> {
        Logger.d(TAG, "获取员工分页列表: current=$current, size=$size")
        
        // 参数验证
        if (current < 1) {
            Logger.w(TAG, "页码参数无效: current=$current")
            return NetworkResult.Error(
                exception = IllegalArgumentException("页码必须大于0"),
                message = "页码参数无效"
            )
        }
        
        if (size < 1 || size > 100) {
            Logger.w(TAG, "页面大小参数无效: size=$size")
            return NetworkResult.Error(
                exception = IllegalArgumentException("页面大小必须在1-100之间"),
                message = "页面大小参数无效"
            )
        }
        
        return try {
            employeeRepository.getEmployeePage(
                current = current,
                size = size,
                username = username?.trim()?.takeIf { it.isNotEmpty() },
                realName = realName?.trim()?.takeIf { it.isNotEmpty() },
                gender = gender,
                job = job,
                departmentId = departmentId,
                entryDateStart = entryDateStart?.trim()?.takeIf { it.isNotEmpty() },
                entryDateEnd = entryDateEnd?.trim()?.takeIf { it.isNotEmpty() }
            )
        } catch (e: Exception) {
            Logger.e(TAG, "获取员工分页列表异常: ${e.message}")
            NetworkResult.Error(
                exception = e,
                message = "获取员工列表失败: ${e.message}"
            )
        }
    }
    
    /**
     * 获取员工详情
     * @param id 员工ID
     * @return 员工详情
     */
    suspend fun getEmployeeById(id: Int): NetworkResult<EmployeeDto> {
        Logger.d(TAG, "获取员工详情: id=$id")
        
        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "员工ID参数无效: id=$id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("员工ID必须大于0"),
                message = "员工ID参数无效"
            )
        }
        
        return try {
            employeeRepository.getEmployeeById(id)
        } catch (e: Exception) {
            Logger.e(TAG, "获取员工详情异常: ${e.message}")
            NetworkResult.Error(
                exception = e,
                message = "获取员工详情失败: ${e.message}"
            )
        }
    }
    
    /**
     * 创建员工
     * @param employeeCreateDto 员工创建数据
     * @return 创建结果
     */
    suspend fun createEmployee(employeeCreateDto: EmployeeCreateDto): NetworkResult<Unit> {
        Logger.d(TAG, "创建员工: username=${employeeCreateDto.username}, realName=${employeeCreateDto.realName}")
        
        // 参数验证
        val validationResult = validateEmployeeCreateData(employeeCreateDto)
        if (validationResult != null) {
            Logger.w(TAG, "员工创建数据验证失败: $validationResult")
            return NetworkResult.Error(
                exception = IllegalArgumentException(validationResult),
                message = validationResult
            )
        }
        
        return try {
            employeeRepository.createEmployee(employeeCreateDto)
        } catch (e: Exception) {
            Logger.e(TAG, "创建员工异常: ${e.message}")
            NetworkResult.Error(
                exception = e,
                message = "创建员工失败: ${e.message}"
            )
        }
    }
    
    /**
     * 更新员工
     * @param employeeUpdateDto 员工更新数据
     * @return 更新结果
     */
    suspend fun updateEmployee(employeeUpdateDto: EmployeeUpdateDto): NetworkResult<Unit> {
        Logger.d(TAG, "更新员工: id=${employeeUpdateDto.id}, realName=${employeeUpdateDto.realName}")
        
        // 参数验证
        val validationResult = validateEmployeeUpdateData(employeeUpdateDto)
        if (validationResult != null) {
            Logger.w(TAG, "员工更新数据验证失败: $validationResult")
            return NetworkResult.Error(
                exception = IllegalArgumentException(validationResult),
                message = validationResult
            )
        }
        
        return try {
            employeeRepository.updateEmployee(employeeUpdateDto)
        } catch (e: Exception) {
            Logger.e(TAG, "更新员工异常: ${e.message}")
            NetworkResult.Error(
                exception = e,
                message = "更新员工失败: ${e.message}"
            )
        }
    }
    
    /**
     * 删除员工
     * @param id 员工ID
     * @return 删除结果
     */
    suspend fun deleteEmployee(id: Int): NetworkResult<Unit> {
        Logger.d(TAG, "删除员工: id=$id")
        
        // 参数验证
        if (id <= 0) {
            Logger.w(TAG, "员工ID参数无效: id=$id")
            return NetworkResult.Error(
                exception = IllegalArgumentException("员工ID必须大于0"),
                message = "员工ID参数无效"
            )
        }
        
        return try {
            employeeRepository.deleteEmployee(id)
        } catch (e: Exception) {
            Logger.e(TAG, "删除员工异常: ${e.message}")
            NetworkResult.Error(
                exception = e,
                message = "删除员工失败: ${e.message}"
            )
        }
    }
    
    /**
     * 批量删除员工
     * @param ids 员工ID列表
     * @return 删除结果
     */
    suspend fun batchDeleteEmployees(ids: List<Int>): NetworkResult<Unit> {
        Logger.d(TAG, "批量删除员工: ids=$ids")
        
        // 参数验证
        if (ids.isEmpty()) {
            Logger.w(TAG, "员工ID列表为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("请选择要删除的员工"),
                message = "请选择要删除的员工"
            )
        }
        
        if (ids.any { it <= 0 }) {
            Logger.w(TAG, "员工ID列表包含无效ID: $ids")
            return NetworkResult.Error(
                exception = IllegalArgumentException("员工ID必须大于0"),
                message = "员工ID参数无效"
            )
        }
        
        if (ids.size > 100) {
            Logger.w(TAG, "批量删除员工数量过多: ${ids.size}")
            return NetworkResult.Error(
                exception = IllegalArgumentException("单次最多删除100个员工"),
                message = "单次最多删除100个员工"
            )
        }
        
        return try {
            employeeRepository.batchDeleteEmployees(ids)
        } catch (e: Exception) {
            Logger.e(TAG, "批量删除员工异常: ${e.message}")
            NetworkResult.Error(
                exception = e,
                message = "批量删除员工失败: ${e.message}"
            )
        }
    }
    
    /**
     * 批量导入员工
     * @param file 导入文件数据
     * @return 导入结果
     */
    suspend fun importEmployees(file: ByteArray): NetworkResult<EmployeeImportDto> {
        Logger.d(TAG, "批量导入员工: 文件大小=${file.size}字节")
        
        // 参数验证
        if (file.isEmpty()) {
            Logger.w(TAG, "导入文件为空")
            return NetworkResult.Error(
                exception = IllegalArgumentException("请选择要导入的文件"),
                message = "请选择要导入的文件"
            )
        }
        
        // 文件大小限制 (10MB)
        if (file.size > 10 * 1024 * 1024) {
            Logger.w(TAG, "导入文件过大: ${file.size}字节")
            return NetworkResult.Error(
                exception = IllegalArgumentException("文件大小不能超过10MB"),
                message = "文件大小不能超过10MB"
            )
        }
        
        return try {
            employeeRepository.importEmployees(file)
        } catch (e: Exception) {
            Logger.e(TAG, "批量导入员工异常: ${e.message}")
            NetworkResult.Error(
                exception = e,
                message = "批量导入员工失败: ${e.message}"
            )
        }
    }
    
    /**
     * 批量导出员工
     * @param username 用户名筛选
     * @param realName 真实姓名筛选
     * @param gender 性别筛选
     * @param job 职位筛选
     * @param departmentId 部门ID筛选
     * @param entryDateStart 入职日期开始
     * @param entryDateEnd 入职日期结束
     * @return 导出文件数据
     */
    suspend fun exportEmployees(
        username: String? = null,
        realName: String? = null,
        gender: Int? = null,
        job: Int? = null,
        departmentId: Int? = null,
        entryDateStart: String? = null,
        entryDateEnd: String? = null
    ): NetworkResult<ByteArray> {
        Logger.d(TAG, "批量导出员工")
        
        return try {
            employeeRepository.exportEmployees(
                username = username?.trim()?.takeIf { it.isNotEmpty() },
                realName = realName?.trim()?.takeIf { it.isNotEmpty() },
                gender = gender,
                job = job,
                departmentId = departmentId,
                entryDateStart = entryDateStart?.trim()?.takeIf { it.isNotEmpty() },
                entryDateEnd = entryDateEnd?.trim()?.takeIf { it.isNotEmpty() }
            )
        } catch (e: Exception) {
            Logger.e(TAG, "批量导出员工异常: ${e.message}")
            NetworkResult.Error(
                exception = e,
                message = "批量导出员工失败: ${e.message}"
            )
        }
    }
    
    /**
     * 验证员工创建数据
     * @param employeeCreateDto 员工创建数据
     * @return 验证错误信息，null表示验证通过
     */
    private fun validateEmployeeCreateData(employeeCreateDto: EmployeeCreateDto): String? {
        // 用户名验证
        if (employeeCreateDto.username.isBlank()) {
            return "用户名不能为空"
        }
        if (employeeCreateDto.username.length < 3 || employeeCreateDto.username.length > 20) {
            return "用户名长度必须在3-20个字符之间"
        }
        if (!employeeCreateDto.username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            return "用户名只能包含字母、数字和下划线"
        }
        
        // 真实姓名验证
        if (employeeCreateDto.realName.isBlank()) {
            return "真实姓名不能为空"
        }
        if (employeeCreateDto.realName.length > 50) {
            return "真实姓名长度不能超过50个字符"
        }
        
        // 密码验证
        if (employeeCreateDto.password.isBlank()) {
            return "密码不能为空"
        }
        if (employeeCreateDto.password.length < 6 || employeeCreateDto.password.length > 20) {
            return "密码长度必须在6-20个字符之间"
        }
        
        // 性别验证
        if (employeeCreateDto.gender !in listOf(1, 2)) {
            return "性别参数无效"
        }
        
        // 部门ID验证
        if (employeeCreateDto.departmentId <= 0) {
            return "请选择部门"
        }
        
        return null
    }
    
    /**
     * 验证员工更新数据
     * @param employeeUpdateDto 员工更新数据
     * @return 验证错误信息，null表示验证通过
     */
    private fun validateEmployeeUpdateData(employeeUpdateDto: EmployeeUpdateDto): String? {
        // ID验证
        if (employeeUpdateDto.id <= 0) {
            return "员工ID无效"
        }
        
        // 真实姓名验证
        if (employeeUpdateDto.realName.isBlank()) {
            return "真实姓名不能为空"
        }
        if (employeeUpdateDto.realName.length > 50) {
            return "真实姓名长度不能超过50个字符"
        }

        
        // 性别验证
        if (employeeUpdateDto.gender !in listOf(1, 2)) {
            return "性别参数无效"
        }
        
        // 部门ID验证
        if (employeeUpdateDto.departmentId <= 0) {
            return "请选择部门"
        }
        
        return null
    }
}