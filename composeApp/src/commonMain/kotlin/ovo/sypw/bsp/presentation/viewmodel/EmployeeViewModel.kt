package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.EmployeeDto
import ovo.sypw.bsp.data.dto.EmployeeCreateDto
import ovo.sypw.bsp.data.dto.EmployeeUpdateDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.EmployeeUseCase
import ovo.sypw.bsp.domain.usecase.DepartmentUseCase
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.utils.Logger
import ovo.sypw.bsp.utils.PagingManager
import ovo.sypw.bsp.utils.PagingUtils

/**
 * 员工管理ViewModel
 * 专门负责员工相关的状态管理和业务逻辑
 */
class EmployeeViewModel(
    private val employeeUseCase: EmployeeUseCase,
    private val departmentUseCase: DepartmentUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "EmployeeViewModel"
    }
    
    // 员工管理状态
    private val _employeeState = MutableStateFlow(EmployeeState())
    val employeeState: StateFlow<EmployeeState> = _employeeState.asStateFlow()
    
    // 员工Dialog状态
    private val _employeeDialogState = MutableStateFlow(EmployeeDialogState())
    val employeeDialogState: StateFlow<EmployeeDialogState> = _employeeDialogState.asStateFlow()
    
    // 员工搜索关键词
    private val _employeeSearchQuery = MutableStateFlow("")
    val employeeSearchQuery: StateFlow<String> = _employeeSearchQuery.asStateFlow()
    
    // 部门列表状态
    private val _departments = MutableStateFlow<List<DepartmentDto>>(emptyList())
    val departments: StateFlow<List<DepartmentDto>> = _departments.asStateFlow()
    
    // 员工分页数据流
    private var _employeePagingManager: PagingManager<EmployeeDto>? = null
    val employeePagingData: StateFlow<PagingData<EmployeeDto>>
        get() = getEmployeePagingManager().pagingData
    
    /**
     * 获取员工分页管理器
     */
    fun getEmployeePagingManager(): PagingManager<EmployeeDto> {
        if (_employeePagingManager == null) {
            _employeePagingManager = PagingUtils.createPagingManager(
                loadData = { page, pageSize ->
                    employeeUseCase.getEmployeePage(
                        current = page,
                        size = pageSize,
                        realName = _employeeSearchQuery.value.takeIf { it.isNotBlank() }
                    )
                }
            )
        }
        return _employeePagingManager!!
    }
    
    /**
     * 更新搜索关键词并刷新分页数据
     * @param query 搜索关键词
     */
    fun updateEmployeeSearchQuery(query: String) {
        _employeeSearchQuery.value = query
        // 重新创建分页器以应用新的搜索条件
        _employeePagingManager = null
    }
    
    /**
     * 清空搜索条件
     */
    fun clearEmployeeSearch() {
        updateEmployeeSearchQuery("")
    }
    
    /**
     * 刷新员工数据
     */
    fun refreshEmployees() {
        // 重新创建分页器以刷新数据
        _employeePagingManager = null
        // 同时保持原有的加载方法以兼容现有代码
        loadEmployees()
    }
    
    /**
     * 加载员工分页数据
     * @param current 当前页码
     * @param size 每页大小
     * @param realName 员工姓名（可选，用于搜索）
     */
    fun loadEmployees(
        current: Int = 1,
        size: Int = 5,
        realName: String? = null
    ) {
        viewModelScope.launch {
            _employeeState.value = _employeeState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = employeeUseCase.getEmployeePage(current, size, realName = realName)) {
                is NetworkResult.Success -> {
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        employees = result.data.records,
                        pageInfo = result.data,
                        errorMessage = null
                    )
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "员工数据加载失败: ${result.message}")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 创建员工
     * @param employeeCreateDto 员工创建数据
     */
    fun createEmployee(employeeCreateDto: EmployeeCreateDto) {
        Logger.d(TAG, "创建员工: username=${employeeCreateDto.username}, realName=${employeeCreateDto.realName}")
        
        viewModelScope.launch {
            _employeeState.value = _employeeState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = employeeUseCase.createEmployee(employeeCreateDto)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "员工创建成功")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载员工列表
                    loadEmployees()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "员工创建失败: ${result.message}")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 更新员工
     * @param employeeUpdateDto 员工更新数据
     */
    fun updateEmployee(employeeUpdateDto: EmployeeUpdateDto) {
        Logger.d(TAG, "更新员工: id=${employeeUpdateDto.id}, realName=${employeeUpdateDto.realName}")
        
        viewModelScope.launch {
            _employeeState.value = _employeeState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = employeeUseCase.updateEmployee(employeeUpdateDto)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "员工更新成功")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载员工列表
                    loadEmployees()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "员工更新失败: ${result.message}")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 删除员工
     * @param id 员工ID
     */
    fun deleteEmployee(id: Int?) {
        Logger.d(TAG, "删除员工: id=$id")
        
        viewModelScope.launch {
            _employeeState.value = _employeeState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = employeeUseCase.deleteEmployee(id ?: 0)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "员工删除成功")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载员工列表
                    loadEmployees()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "员工删除失败: ${result.message}")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 批量删除员工
     * @param ids 员工ID列表
     */
    fun batchDeleteEmployees(ids: List<Int>) {
        Logger.d(TAG, "批量删除员工: ids=$ids")
        
        viewModelScope.launch {
            _employeeState.value = _employeeState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = employeeUseCase.batchDeleteEmployees(ids)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "员工批量删除成功")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载员工列表
                    loadEmployees()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "员工批量删除失败: ${result.message}")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearEmployeeError() {
        _employeeState.value = _employeeState.value.copy(
            errorMessage = null
        )
    }
    
    /**
     * 加载部门列表数据
     * 按照用户要求直接查询1000条数据
     */
    fun loadDepartments() {
        viewModelScope.launch {
            when (val result = departmentUseCase.getDepartmentPage(current = 1, size = 1000)) {
                is NetworkResult.Success -> {
                    _departments.value = result.data.records
                    Logger.d(TAG, "部门数据加载成功，共${result.data.records.size}条")
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门数据加载失败: ${result.message}")
                }
                else -> {
                    // 其他状态不处理
                }
            }
        }
    }
    
    /**
     * 显示添加员工Dialog
     */
    fun showAddEmployeeDialog() {
        Logger.d(TAG, "显示添加员工Dialog")
        // 加载部门数据
        loadDepartments()
        _employeeDialogState.value = EmployeeDialogState(
            isVisible = true,
            isEditMode = false,
            username = "",
            realName = "",
            password = "",
            gender = 1,
            job = 1,
            departmentId = 1,
            entryDate = "",
            editingEmployeeId = null
        )
        Logger.d(TAG, "Dialog状态已更新: ${_employeeDialogState.value}")
    }
    
    /**
     * 显示编辑员工Dialog
     * @param employee 要编辑的员工
     */
    fun showEditEmployeeDialog(employee: EmployeeDto) {
        // 加载部门数据
        loadDepartments()
        _employeeDialogState.value = EmployeeDialogState(
            isVisible = true,
            isEditMode = true,
            username = employee.username,
            realName = employee.realName,
            password = "", // 编辑时不显示密码
            gender = employee.gender,
            job = employee.job,
            departmentId = employee.departmentId,
            entryDate = employee.entryDate ?: "",
            editingEmployeeId = employee.id
        )
    }
    
    /**
     * 隐藏员工Dialog
     */
    fun hideEmployeeDialog() {
        _employeeDialogState.value = EmployeeDialogState()
    }
    
    /**
     * 更新员工用户名输入
     * @param username 用户名
     */
    fun updateEmployeeUsername(username: String) {
        _employeeDialogState.value = _employeeDialogState.value.copy(
            username = username
        )
    }
    
    /**
     * 更新员工真实姓名输入
     * @param realName 真实姓名
     */
    fun updateEmployeeRealName(realName: String) {
        _employeeDialogState.value = _employeeDialogState.value.copy(
            realName = realName
        )
    }
    
    /**
     * 更新员工密码输入
     * @param password 密码
     */
    fun updateEmployeePassword(password: String) {
        _employeeDialogState.value = _employeeDialogState.value.copy(
            password = password
        )
    }
    
    /**
     * 更新员工性别
     * @param gender 性别 (1-男, 2-女)
     */
    fun updateEmployeeGender(gender: Int) {
        _employeeDialogState.value = _employeeDialogState.value.copy(
            gender = gender
        )
    }
    
    /**
     * 更新员工职位
     * @param job 职位
     */
    fun updateEmployeeJob(job: Int) {
        _employeeDialogState.value = _employeeDialogState.value.copy(
            job = job
        )
    }
    
    /**
     * 更新员工部门ID
     * @param departmentId 部门ID
     */
    fun updateEmployeeDepartmentId(departmentId: Int) {
        _employeeDialogState.value = _employeeDialogState.value.copy(
            departmentId = departmentId
        )
    }
    
    /**
     * 更新员工入职日期
     * @param entryDate 入职日期
     */
    fun updateEmployeeEntryDate(entryDate: String) {
        _employeeDialogState.value = _employeeDialogState.value.copy(
            entryDate = entryDate
        )
    }
    
    /**
     * 保存员工（添加或编辑）
     */
    fun saveEmployee() {
        val dialogState = _employeeDialogState.value
        
        // 基本验证
        if (dialogState.username.isBlank()) {
            _employeeDialogState.value = dialogState.copy(
                errorMessage = "用户名不能为空"
            )
            return
        }
        
        if (dialogState.realName.isBlank()) {
            _employeeDialogState.value = dialogState.copy(
                errorMessage = "真实姓名不能为空"
            )
            return
        }
        
        if (!dialogState.isEditMode && dialogState.password.isBlank()) {
            _employeeDialogState.value = dialogState.copy(
                errorMessage = "密码不能为空"
            )
            return
        }
        
        viewModelScope.launch {
            _employeeDialogState.value = dialogState.copy(
                isLoading = true,
                errorMessage = null
            )
            
            val result = if (dialogState.isEditMode && dialogState.editingEmployeeId != null) {
                // 编辑模式
                val updateDto = EmployeeUpdateDto(
                    id = dialogState.editingEmployeeId,
                    realName = dialogState.realName,
                    gender = dialogState.gender,
                    job = dialogState.job,
                    departmentId = dialogState.departmentId,
                    entryDate = dialogState.entryDate.takeIf { it.isNotBlank() },
                    username = dialogState.username,
                    password = dialogState.password,
                    avatar = null
                )
                employeeUseCase.updateEmployee(updateDto)
            } else {
                // 添加模式
                val createDto = EmployeeCreateDto(
                    username = dialogState.username,
                    realName = dialogState.realName,
                    password = dialogState.password,
                    avatar = null,
                    gender = dialogState.gender,
                    job = dialogState.job,
                    departmentId = dialogState.departmentId,
                    entryDate = dialogState.entryDate.takeIf { it.isNotBlank() }
                )
                employeeUseCase.createEmployee(createDto)
            }
            
            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "员工保存成功")
                    hideEmployeeDialog()
                    // 刷新分页数据和传统列表数据
                    _employeePagingManager = null
                    refreshEmployees()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "员工保存失败: ${result.message}")
                    _employeeDialogState.value = _employeeDialogState.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "保存失败"
                    )
                }
                NetworkResult.Idle -> {
                    _employeeDialogState.value = _employeeDialogState.value.copy(
                        isLoading = false
                    )
                }
                NetworkResult.Loading -> {
                    // 保持加载状态
                }
            }
        }
    }
}

/**
 * 员工管理状态
 */
data class EmployeeState(
    val isLoading: Boolean = false,
    val employees: List<EmployeeDto> = emptyList(),
    val pageInfo: PageResultDto<EmployeeDto>? = null,
    val errorMessage: String? = null
)

/**
 * 员工Dialog状态
 */
data class EmployeeDialogState(
    val isVisible: Boolean = false,
    val isEditMode: Boolean = false,
    val username: String = "",
    val realName: String = "",
    val password: String = "",
    val gender: Int = 1, // 1-男, 2-女
    val job: Int = 1,
    val departmentId: Int = 1,
    val entryDate: String = "",
    val editingEmployeeId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)