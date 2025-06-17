package ovo.sypw.bsp.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.EmployeeDto
import ovo.sypw.bsp.data.dto.EmployeeCreateDto
import ovo.sypw.bsp.data.dto.EmployeeUpdateDto
import ovo.sypw.bsp.data.dto.EmployeeImportDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.usecase.EmployeeUseCase
import ovo.sypw.bsp.domain.usecase.DepartmentUseCase
import ovo.sypw.bsp.domain.usecase.FileUploadUseCase
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.utils.Logger
import ovo.sypw.bsp.utils.PagingManager
import ovo.sypw.bsp.utils.PagingUtils
import ovo.sypw.bsp.utils.file.FileUtils

import kotlinx.coroutines.flow.catch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 员工管理ViewModel
 * 专门负责员工相关的状态管理和业务逻辑
 */
class EmployeeViewModel(
    private val employeeUseCase: EmployeeUseCase,
    private val departmentUseCase: DepartmentUseCase,
    private val fileUploadUseCase: FileUploadUseCase,
    private val fileUtils: FileUtils
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

    // 员工筛选状态
    private val _employeeFilterState = MutableStateFlow(EmployeeFilterState())
    val employeeFilterState: StateFlow<EmployeeFilterState> = _employeeFilterState.asStateFlow()

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
                    val filterState = _employeeFilterState.value
                    employeeUseCase.getEmployeePage(
                        current = page,
                        size = pageSize,
                        realName = _employeeSearchQuery.value.takeIf { it.isNotBlank() },
                        gender = filterState.selectedGender,
                        job = filterState.selectedJob,
                        departmentId = filterState.selectedDepartmentId,
                        entryDateStart = filterState.entryDateStart,
                        entryDateEnd = filterState.entryDateEnd
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
        // 立即加载数据
        loadEmployees()
    }

    /**
     * 清空搜索条件
     */
    fun clearEmployeeSearch() {
        updateEmployeeSearchQuery("")
    }

    /**
     * 更新筛选条件
     */
    fun updateEmployeeFilter(filterState: EmployeeFilterState) {
        _employeeFilterState.value = filterState
        // 重新创建分页器以应用新的筛选条件
        _employeePagingManager = null
        // 立即加载数据
        loadEmployees()
    }

    /**
     * 切换筛选面板展开状态
     */
    fun toggleFilterExpanded() {
        _employeeFilterState.value = _employeeFilterState.value.copy(
            isFilterExpanded = !_employeeFilterState.value.isFilterExpanded
        )
    }

    /**
     * 清空所有筛选条件
     */
    fun clearAllFilters() {
        _employeeFilterState.value = EmployeeFilterState()
        _employeeSearchQuery.value = ""
        // 重新创建分页器以应用清空的条件
        _employeePagingManager = null
        // 立即加载数据
        loadEmployees()
    }

    /**
     * 设置性别筛选
     */
    fun setGenderFilter(gender: Int?) {
        _employeeFilterState.value = _employeeFilterState.value.copy(selectedGender = gender)
        _employeePagingManager = null
        // 立即加载数据
        loadEmployees()
    }

    /**
     * 设置职位筛选
     */
    fun setJobFilter(job: Int?) {
        _employeeFilterState.value = _employeeFilterState.value.copy(selectedJob = job)
        _employeePagingManager = null
        // 立即加载数据
        loadEmployees()
    }

    /**
     * 设置部门筛选
     */
    fun setDepartmentFilter(departmentId: Int?) {
        _employeeFilterState.value = _employeeFilterState.value.copy(selectedDepartmentId = departmentId)
        _employeePagingManager = null
        // 立即加载数据
        loadEmployees()
    }

    /**
     * 设置入职日期范围筛选
     */
    fun setEntryDateFilter(startDate: String?, endDate: String?) {
        _employeeFilterState.value = _employeeFilterState.value.copy(
            entryDateStart = startDate,
            entryDateEnd = endDate
        )
        _employeePagingManager = null
        // 立即加载数据
        loadEmployees()
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
        size: Int = 9,
        realName: String? = null
    ) {
        viewModelScope.launch {
            _employeeState.value = _employeeState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val filterState = _employeeFilterState.value
            val searchQuery = realName ?: _employeeSearchQuery.value.takeIf { it.isNotBlank() }

            when (val result = employeeUseCase.getEmployeePage(
                current = current,
                size = size,
                realName = searchQuery,
                gender = filterState.selectedGender,
                job = filterState.selectedJob,
                departmentId = filterState.selectedDepartmentId,
                entryDateStart = filterState.entryDateStart,
                entryDateEnd = filterState.entryDateEnd
            )) {
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
     * 导入员工数据
     * @param fileBytes 文件字节数组
     */
    fun importEmployees(fileBytes: ByteArray) {
        Logger.d(TAG, "开始导入员工数据，文件大小: ${fileBytes.size} bytes")

        viewModelScope.launch {
            _employeeState.value = _employeeState.value.copy(
                isLoading = true,
                errorMessage = null,
                importResult = null
            )

            val result = employeeUseCase.importEmployees(fileBytes)
            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "员工导入成功: ${result.data}")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        importResult = result.data,
                        errorMessage = null
                    )
                    // 刷新员工列表
                    refreshEmployees()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "员工导入失败: ${result.message}")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = true
                    )
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
     * 导出员工数据
     * @param username 用户名筛选
     * @param realName 真实姓名筛选
     * @param gender 性别筛选
     * @param job 职位筛选
     * @param departmentId 部门ID筛选
     * @param entryDateStart 入职开始日期
     * @param entryDateEnd 入职结束日期
     */
    fun exportEmployees(
        username: String? = null,
        realName: String? = null,
        gender: Int? = null,
        job: Int? = null,
        departmentId: Int? = null,
        entryDateStart: String? = null,
        entryDateEnd: String? = null
    ) {
        Logger.d(TAG, "开始导出员工数据")

        viewModelScope.launch {
            _employeeState.value = _employeeState.value.copy(
                isLoading = true,
                errorMessage = null,
                exportData = null
            )

            val result = employeeUseCase.exportEmployees(
                username = username,
                realName = realName,
                gender = gender,
                job = job,
                departmentId = departmentId,
                entryDateStart = entryDateStart,
                entryDateEnd = entryDateEnd
            )
            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "员工导出成功，数据大小: ${result.data.size} bytes")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        exportData = result.data,
                        errorMessage = null
                    )
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "员工导出失败: ${result.message}")
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    _employeeState.value = _employeeState.value.copy(
                        isLoading = true
                    )
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
     * 清除导入结果
     */
    fun clearImportResult() {
        _employeeState.value = _employeeState.value.copy(
            importResult = null
        )
    }

    /**
     * 清除导出数据
     */
    fun clearExportData() {
        _employeeState.value = _employeeState.value.copy(
            exportData = null
        )
    }

    /**
     * 选择并导入员工文件
     */
    fun selectAndImportEmployeeFile() {
        viewModelScope.launch {
            try {
                val fileBytes = fileUtils.selectFile()
                if (fileBytes != null) {
                    importEmployees(fileBytes)
                } else {
                    Logger.w(TAG, "未选择文件或选择被取消")
                }
            } catch (e: Exception) {
                Logger.e(TAG, "选择文件失败", e)
                _employeeState.value = _employeeState.value.copy(
                    errorMessage = "选择文件失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 保存导出的员工数据到文件
     */
    @OptIn(ExperimentalTime::class)
    fun saveExportedEmployeeData() {
        val exportData = _employeeState.value.exportData
        if (exportData!=null && exportData.isEmpty()) {
            Logger.w(TAG, "没有可保存的导出数据")
            return
        }

        viewModelScope.launch {
            try {
                if(exportData==null)return@launch
                val success = fileUtils.saveFile(
                    data = exportData,
                    fileName = "employees_export_${Clock.System.now().toEpochMilliseconds()}.xlsx",
                    mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )

                if (success) {
                    Logger.i(TAG, "员工数据导出文件保存成功")
                    // 清除导出数据
                    clearExportData()
                } else {
                    Logger.w(TAG, "员工数据导出文件保存失败")
                    _employeeState.value = _employeeState.value.copy(
                        errorMessage = "文件保存失败"
                    )
                }
            } catch (e: Exception) {
                Logger.e(TAG, "保存导出文件失败", e)
                _employeeState.value = _employeeState.value.copy(
                    errorMessage = "保存文件失败: ${e.message}"
                )
            }
        }
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
            editingEmployeeId = employee.id,
            avatarUrl = employee.avatar // 加载现有头像URL
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
     * 选择头像图片
     */
    fun selectAvatar() {
        viewModelScope.launch {
            try {
                _employeeDialogState.value = _employeeDialogState.value.copy(
                    isUploadingAvatar = true,
                    errorMessage = null
                )

                val imageBytes = fileUtils.selectImage()
                if (imageBytes != null) {
                    _employeeDialogState.value = _employeeDialogState.value.copy(
                        selectedAvatarBytes = imageBytes,
                        isUploadingAvatar = false
                    )
                    Logger.i(TAG, "头像选择成功，大小: ${imageBytes.size} bytes")
                } else {
                    _employeeDialogState.value = _employeeDialogState.value.copy(
                        isUploadingAvatar = false,
                        errorMessage = "未选择图片或选择被取消"
                    )
                }
            } catch (e: Exception) {
                Logger.e(TAG, "选择头像失败", e)
                _employeeDialogState.value = _employeeDialogState.value.copy(
                    isUploadingAvatar = false,
                    errorMessage = "选择头像失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 清除选择的头像
     */
    fun clearSelectedAvatar() {
        _employeeDialogState.value = _employeeDialogState.value.copy(
            selectedAvatarBytes = null
        )
    }

    /**
     * 上传头像并获取URL
     * @return 上传成功的头像URL，失败返回null
     */
    @OptIn(ExperimentalTime::class)
    private suspend fun uploadAvatarIfNeeded(): String? {
        val dialogState = _employeeDialogState.value
        val avatarBytes = dialogState.selectedAvatarBytes ?: return dialogState.avatarUrl

        try {
            Logger.i(TAG, "开始上传头像")
            _employeeDialogState.value = dialogState.copy(
                isUploadingAvatar = true
            )

            // 生成文件名
            val fileName = "avatar_${Clock.System.now().toEpochMilliseconds()}.jpg"

            var uploadedUrl: String? = null

            // 上传头像
            fileUploadUseCase.uploadImage(
                imageBytes = avatarBytes,
                fileName = fileName,
                quality = 85
            ).catch { e ->
                Logger.e(TAG, "上传头像失败", e)
                throw e
            }.collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val avatarUrl = result.data.fileUrl
                        Logger.i(TAG, "头像上传成功: $avatarUrl")
                        _employeeDialogState.value = _employeeDialogState.value.copy(
                            avatarUrl = avatarUrl,
                            isUploadingAvatar = false
                        )
                        uploadedUrl = avatarUrl
                    }
                    is NetworkResult.Error -> {
                        Logger.e(TAG, "头像上传失败: ${result.message}")
                        _employeeDialogState.value = _employeeDialogState.value.copy(
                            isUploadingAvatar = false,
                            errorMessage = "头像上传失败: ${result.message}"
                        )
                        throw Exception(result.message)
                    }
                    is NetworkResult.Loading -> {
                        // 保持加载状态
                    }
                    else -> {}
                }
            }

            return uploadedUrl ?: dialogState.avatarUrl
        } catch (e: Exception) {
            Logger.e(TAG, "上传头像异常", e)
            _employeeDialogState.value = _employeeDialogState.value.copy(
                isUploadingAvatar = false,
                errorMessage = "头像上传失败: ${e.message}"
            )
            return null
        }
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

            try {
                // 先上传头像（如果有选择的话）
                val avatarUrl = uploadAvatarIfNeeded()

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
                        avatar = avatarUrl
                    )
                    employeeUseCase.updateEmployee(updateDto)
                } else {
                    // 添加模式
                    val createDto = EmployeeCreateDto(
                        username = dialogState.username,
                        realName = dialogState.realName,
                        password = dialogState.password,
                        avatar = avatarUrl,
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
            } catch (e: Exception) {
                Logger.e(TAG, "保存员工异常", e)
                _employeeDialogState.value = _employeeDialogState.value.copy(
                    isLoading = false,
                    errorMessage = "保存失败: ${e.message}"
                )
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
    val errorMessage: String? = null,
    val importResult: EmployeeImportDto? = null,
    val exportData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EmployeeState

        if (isLoading != other.isLoading) return false
        if (employees != other.employees) return false
        if (pageInfo != other.pageInfo) return false
        if (errorMessage != other.errorMessage) return false
        if (importResult != other.importResult) return false
        if (exportData != null) {
            if (other.exportData == null) return false
            if (!exportData.contentEquals(other.exportData)) return false
        } else if (other.exportData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + employees.hashCode()
        result = 31 * result + (pageInfo?.hashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + (importResult?.hashCode() ?: 0)
        result = 31 * result + (exportData?.contentHashCode() ?: 0)
        return result
    }
}

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
    val errorMessage: String? = null,
    // 头像相关字段
    val avatarUrl: String? = null,
    val selectedAvatarBytes: ByteArray? = null,
    val isUploadingAvatar: Boolean = false
) {
    // 重写equals和hashCode以正确处理ByteArray
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EmployeeDialogState

        if (isVisible != other.isVisible) return false
        if (isEditMode != other.isEditMode) return false
        if (username != other.username) return false
        if (realName != other.realName) return false
        if (password != other.password) return false
        if (gender != other.gender) return false
        if (job != other.job) return false
        if (departmentId != other.departmentId) return false
        if (entryDate != other.entryDate) return false
        if (editingEmployeeId != other.editingEmployeeId) return false
        if (isLoading != other.isLoading) return false
        if (errorMessage != other.errorMessage) return false
        if (avatarUrl != other.avatarUrl) return false
        if (selectedAvatarBytes != null) {
            if (other.selectedAvatarBytes == null) return false
            if (!selectedAvatarBytes.contentEquals(other.selectedAvatarBytes)) return false
        } else if (other.selectedAvatarBytes != null) return false
        if (isUploadingAvatar != other.isUploadingAvatar) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isVisible.hashCode()
        result = 31 * result + isEditMode.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + realName.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + gender
        result = 31 * result + job
        result = 31 * result + departmentId
        result = 31 * result + entryDate.hashCode()
        result = 31 * result + (editingEmployeeId ?: 0)
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + (avatarUrl?.hashCode() ?: 0)
        result = 31 * result + (selectedAvatarBytes?.contentHashCode() ?: 0)
        result = 31 * result + isUploadingAvatar.hashCode()
        return result
    }
}

/**
 * 员工筛选状态
 */
data class EmployeeFilterState(
    val selectedGender: Int? = null, // null-全部, 1-男, 2-女
    val selectedJob: Int? = null, // null-全部, 1-5对应不同职位
    val selectedDepartmentId: Int? = null, // null-全部, 其他值对应部门ID
    val entryDateStart: String? = null, // 入职开始日期
    val entryDateEnd: String? = null, // 入职结束日期
    val isFilterExpanded: Boolean = false // 筛选面板是否展开
) {
    /**
     * 检查是否有任何筛选条件被设置
     */
    fun hasActiveFilters(): Boolean {
        return selectedGender != null ||
               selectedJob != null ||
               selectedDepartmentId != null ||
               !entryDateStart.isNullOrBlank() ||
               !entryDateEnd.isNullOrBlank()
    }

    /**
     * 获取职位名称
     */
    fun getJobName(jobId: Int): String {
        return when (jobId) {
            1 -> "经理"
            2 -> "主管"
            3 -> "员工"
            4 -> "实习生"
            5 -> "顾问"
            else -> "未知"
        }
    }

    /**
     * 获取性别名称
     */
    fun getGenderName(gender: Int): String {
        return when (gender) {
            1 -> "男"
            2 -> "女"
            else -> "未知"
        }
    }
}