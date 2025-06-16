package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.StudentDto
import ovo.sypw.bsp.data.dto.StudentCreateDto
import ovo.sypw.bsp.data.dto.StudentUpdateDto
import ovo.sypw.bsp.data.dto.StudentImportDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.dto.ClassDto
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.StudentUseCase
import ovo.sypw.bsp.domain.usecase.ClassUseCase
import ovo.sypw.bsp.utils.Logger
import ovo.sypw.bsp.utils.PagingManager
import ovo.sypw.bsp.utils.PagingUtils
import ovo.sypw.bsp.utils.FileUtils
import ovo.sypw.bsp.utils.createFileUtils
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 学生管理ViewModel
 * 专门负责学生相关的状态管理和业务逻辑
 */
class StudentViewModel(
    private val studentUseCase: StudentUseCase,
    private val classUseCase: ClassUseCase
) : ViewModel() {
    
    // 文件工具类实例
    private val fileUtils: FileUtils = createFileUtils()
    
    companion object {
        private const val TAG = "StudentViewModel"
    }
    
    // 学生管理状态
    private val _studentState = MutableStateFlow(StudentState())
    val studentState: StateFlow<StudentState> = _studentState.asStateFlow()
    
    // 学生Dialog状态
    private val _studentDialogState = MutableStateFlow(StudentDialogState())
    val studentDialogState: StateFlow<StudentDialogState> = _studentDialogState.asStateFlow()
    
    // 学生搜索关键词
    private val _studentSearchQuery = MutableStateFlow("")
    val studentSearchQuery: StateFlow<String> = _studentSearchQuery.asStateFlow()
    
    // 学生筛选状态
    private val _studentFilterState = MutableStateFlow(StudentFilterState())
    val studentFilterState: StateFlow<StudentFilterState> = _studentFilterState.asStateFlow()
    
    // 班级列表状态
    private val _classes = MutableStateFlow<List<ClassDto>>(emptyList())
    val classes: StateFlow<List<ClassDto>> = _classes.asStateFlow()
    
    // 学生分页数据流
    private var _studentPagingManager: PagingManager<StudentDto>? = null
    val studentPagingData: StateFlow<PagingData<StudentDto>>
        get() = getStudentPagingManager().pagingData
    
    /**
     * 获取学生分页管理器
     */
    fun getStudentPagingManager(): PagingManager<StudentDto> {
        if (_studentPagingManager == null) {
            _studentPagingManager = PagingUtils.createPagingManager(
                loadData = { page, pageSize ->
                    val filterState = _studentFilterState.value
                    studentUseCase.getStudentPage(
                        current = page,
                        size = pageSize,
                        name = _studentSearchQuery.value.takeIf { it.isNotBlank() },
                        gender = filterState.selectedGender,
                        classId = filterState.selectedClassId,
                        status = filterState.selectedStatus,
                        birthDateStart = filterState.birthDateStart,
                        birthDateEnd = filterState.birthDateEnd,
                        joinDateStart = filterState.joinDateStart,
                        joinDateEnd = filterState.joinDateEnd
                    ) as NetworkResult<PageResultDto<StudentDto>>
                }
            )
        }
        return _studentPagingManager!!
    }
    
    /**
     * 更新搜索关键词并刷新分页数据
     * @param query 搜索关键词
     */
    fun updateStudentSearchQuery(query: String) {
        _studentSearchQuery.value = query
        // 重新创建分页器以应用新的搜索条件
        _studentPagingManager = null
        // 立即加载数据
        loadStudents()
    }
    
    /**
     * 清空搜索条件
     */
    fun clearStudentSearch() {
        updateStudentSearchQuery("")
    }
    
    /**
     * 更新筛选条件
     */
    fun updateStudentFilter(filterState: StudentFilterState) {
        _studentFilterState.value = filterState
        // 重新创建分页器以应用新的筛选条件
        _studentPagingManager = null
        // 立即加载数据
        loadStudents()
    }
    
    /**
     * 切换筛选面板展开状态
     */
    fun toggleFilterExpanded() {
        _studentFilterState.value = _studentFilterState.value.copy(
            isFilterExpanded = !_studentFilterState.value.isFilterExpanded
        )
    }
    
    /**
     * 清空所有筛选条件
     */
    fun clearAllFilters() {
        _studentFilterState.value = StudentFilterState()
        _studentSearchQuery.value = ""
        // 重新创建分页器以应用清空的条件
        _studentPagingManager = null
        // 立即加载数据
        loadStudents()
    }
    
    /**
     * 设置性别筛选
     */
    fun setGenderFilter(gender: Int?) {
        _studentFilterState.value = _studentFilterState.value.copy(selectedGender = gender)
        _studentPagingManager = null
        // 立即加载数据
        loadStudents()
    }
    
    /**
     * 设置班级筛选
     */
    fun setClassFilter(classId: Int?) {
        _studentFilterState.value = _studentFilterState.value.copy(selectedClassId = classId)
        _studentPagingManager = null
        // 立即加载数据
        loadStudents()
    }
    
    /**
     * 设置状态筛选
     */
    fun setStatusFilter(status: Int?) {
        _studentFilterState.value = _studentFilterState.value.copy(selectedStatus = status)
        _studentPagingManager = null
        // 立即加载数据
        loadStudents()
    }
    
    /**
     * 设置出生日期范围筛选
     */
    fun setBirthDateFilter(startDate: String?, endDate: String?) {
        _studentFilterState.value = _studentFilterState.value.copy(
            birthDateStart = startDate,
            birthDateEnd = endDate
        )
        _studentPagingManager = null
        // 立即加载数据
        loadStudents()
    }
    
    /**
     * 设置入学日期范围筛选
     */
    fun setJoinDateFilter(startDate: String?, endDate: String?) {
        _studentFilterState.value = _studentFilterState.value.copy(
            joinDateStart = startDate,
            joinDateEnd = endDate
        )
        _studentPagingManager = null
        // 立即加载数据
        loadStudents()
    }
    
    /**
     * 刷新学生数据
     */
    fun refreshStudents() {
        // 重新创建分页器以刷新数据
        _studentPagingManager = null
        // 同时保持原有的加载方法以兼容现有代码
        loadStudents()
    }
    
    /**
     * 加载学生分页数据
     * @param current 当前页码
     * @param size 每页大小
     * @param name 学生姓名（可选，用于搜索）
     */
    fun loadStudents(
        current: Int = 1,
        size: Int = 5,
        name: String? = null
    ) {
        viewModelScope.launch {
            _studentState.value = _studentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            val filterState = _studentFilterState.value
            val searchQuery = name ?: _studentSearchQuery.value.takeIf { it.isNotBlank() }
            
            studentUseCase.getStudentPage(
                current = current,
                size = size,
                name = searchQuery,
                gender = filterState.selectedGender,
                classId = filterState.selectedClassId,
                status = filterState.selectedStatus,
                birthDateStart = filterState.birthDateStart,
                birthDateEnd = filterState.birthDateEnd,
                joinDateStart = filterState.joinDateStart,
                joinDateEnd = filterState.joinDateEnd
            ).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            students = result.data.records,
                            pageInfo = result.data,
                            errorMessage = null
                        )
                    }
                    is NetworkResult.Error -> {
                        Logger.e(TAG, "学生数据加载失败: ${result.message}")
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    is NetworkResult.Loading -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = true
                        )
                    }
                    is NetworkResult.Idle -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    
    /**
     * 创建学生
     * @param studentCreateDto 学生创建数据
     */
    fun createStudent(studentCreateDto: StudentCreateDto) {
        Logger.d(TAG, "创建学生: name=${studentCreateDto.name}")
        
        viewModelScope.launch {
            _studentState.value = _studentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            studentUseCase.createStudent(studentCreateDto).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        Logger.i(TAG, "学生创建成功")
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                        // 重新加载学生列表
                        loadStudents()
                    }
                    is NetworkResult.Error -> {
                        Logger.e(TAG, "学生创建失败: ${result.message}")
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    is NetworkResult.Loading -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = true
                        )
                    }
                    is NetworkResult.Idle -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    
    /**
     * 更新学生
     * @param studentUpdateDto 学生更新数据
     */
    fun updateStudent(studentUpdateDto: StudentUpdateDto) {
        Logger.d(TAG, "更新学生: id=${studentUpdateDto.id}, name=${studentUpdateDto.name}")
        
        viewModelScope.launch {
            _studentState.value = _studentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            studentUseCase.updateStudent(studentUpdateDto).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        Logger.i(TAG, "学生更新成功")
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                        // 重新加载学生列表
                        loadStudents()
                    }
                    is NetworkResult.Error -> {
                        Logger.e(TAG, "学生更新失败: ${result.message}")
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    is NetworkResult.Loading -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = true
                        )
                    }
                    is NetworkResult.Idle -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    
    /**
     * 删除学生
     * @param id 学生ID
     */
    fun deleteStudent(id: Int?) {
        Logger.d(TAG, "删除学生: id=$id")
        
        viewModelScope.launch {
            _studentState.value = _studentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            studentUseCase.deleteStudent(id ?: 0).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        Logger.i(TAG, "学生删除成功")
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                        // 重新加载学生列表
                        loadStudents()
                    }
                    is NetworkResult.Error -> {
                        Logger.e(TAG, "学生删除失败: ${result.message}")
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    is NetworkResult.Loading -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = true
                        )
                    }
                    is NetworkResult.Idle -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
    
    /**
     * 批量删除学生
     * @param ids 学生ID列表
     */
    fun batchDeleteStudents(ids: List<Int>) {
        Logger.d(TAG, "批量删除学生: ids=$ids")
        
        viewModelScope.launch {
            _studentState.value = _studentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            studentUseCase.batchDeleteStudents(ids).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        Logger.i(TAG, "学生批量删除成功")
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                        // 重新加载学生列表
                        loadStudents()
                    }
                    is NetworkResult.Error -> {
                        Logger.e(TAG, "学生批量删除失败: ${result.message}")
                        _studentState.value = _studentState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                    is NetworkResult.Loading -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = true
                        )
                    }
                    is NetworkResult.Idle -> {
                        _studentState.value = _studentState.value.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    
    /**
     * 清除错误消息
     */
    fun clearStudentError() {
        _studentState.value = _studentState.value.copy(
            errorMessage = null
        )
    }
    
    /**
     * 清除导入结果
     */
    fun clearImportResult() {
        _studentState.value = _studentState.value.copy(
            importResult = null
        )
    }
    
    /**
     * 清除导出数据
     */
    fun clearExportData() {
        _studentState.value = _studentState.value.copy(
            exportData = null
        )
    }
    
    /**
     * 加载班级列表数据
     * 按照用户要求直接查询1000条数据
     */
    fun loadClasses() {
        viewModelScope.launch {
            val result = classUseCase.getClassPage(current = 1, size = 1000)
            when (result) {
                is NetworkResult.Success -> {
                    _classes.value = result.data.records
                    Logger.d(TAG, "班级数据加载成功，共${result.data.records.size}条")
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "班级数据加载失败: ${result.message}")
                }
                else -> {
                    // 其他状态不处理
                }
            }
        }
    }
    
    /**
     * 显示添加学生Dialog
     */
    fun showAddStudentDialog() {
        Logger.d(TAG, "显示添加学生Dialog")
        // 加载班级数据
        loadClasses()
        _studentDialogState.value = StudentDialogState(
            isVisible = true,
            isEditMode = false,
            name = "",
            gender = 1,
            birthDate = "",
            joinDate = "",
            classId = 1,
            status = 1,
            editingStudentId = null
        )
        Logger.d(TAG, "Dialog状态已更新: ${_studentDialogState.value}")
    }
    
    /**
     * 显示编辑学生Dialog
     * @param student 要编辑的学生
     */
    fun showEditStudentDialog(student: StudentDto) {
        // 加载班级数据
        loadClasses()
        _studentDialogState.value = StudentDialogState(
            isVisible = true,
            isEditMode = true,
            name = student.name,
            gender = student.gender,
            birthDate = student.birthDate,
            joinDate = student.joinDate,
            classId = student.classId,
            status = student.status,
            editingStudentId = student.id
        )
    }
    
    /**
     * 隐藏学生Dialog
     */
    fun hideStudentDialog() {
        _studentDialogState.value = StudentDialogState()
    }
    
    /**
     * 更新学生姓名输入
     * @param name 学生姓名
     */
    fun updateStudentName(name: String) {
        _studentDialogState.value = _studentDialogState.value.copy(
            name = name
        )
    }
    
    /**
     * 更新学生性别
     * @param gender 性别
     */
    fun updateStudentGender(gender: Int) {
        _studentDialogState.value = _studentDialogState.value.copy(
            gender = gender
        )
    }
    
    /**
     * 更新学生出生日期
     * @param birthDate 出生日期
     */
    fun updateStudentBirthDate(birthDate: String) {
        _studentDialogState.value = _studentDialogState.value.copy(
            birthDate = birthDate
        )
    }
    
    /**
     * 更新学生入学日期
     * @param joinDate 入学日期
     */
    fun updateStudentJoinDate(joinDate: String) {
        _studentDialogState.value = _studentDialogState.value.copy(
            joinDate = joinDate
        )
    }
    
    /**
     * 更新学生班级ID
     * @param classId 班级ID
     */
    fun updateStudentClassId(classId: Int) {
        _studentDialogState.value = _studentDialogState.value.copy(
            classId = classId
        )
    }
    
    /**
     * 更新学生状态
     * @param status 学生状态
     */
    fun updateStudentStatus(status: Int) {
        _studentDialogState.value = _studentDialogState.value.copy(
            status = status
        )
    }
    
    /**
     * 提交学生表单
     */
    fun submitStudentForm() {
        val dialogState = _studentDialogState.value
        
        if (dialogState.isEditMode) {
            // 编辑模式
            val studentUpdateDto = StudentUpdateDto(
                id = dialogState.editingStudentId ?: 0,
                name = dialogState.name,
                gender = dialogState.gender,
                birthDate = dialogState.birthDate,
                joinDate = dialogState.joinDate,
                classId = dialogState.classId,
                status = dialogState.status
            )
            updateStudent(studentUpdateDto)
        } else {
            // 创建模式
            val studentCreateDto = StudentCreateDto(
                name = dialogState.name,
                gender = dialogState.gender,
                birthDate = dialogState.birthDate,
                joinDate = dialogState.joinDate,
                classId = dialogState.classId,
                status = dialogState.status
            )
            createStudent(studentCreateDto)
        }
        
        // 隐藏Dialog
        hideStudentDialog()
    }
}

/**
 * 学生管理状态
 */
data class StudentState(
    val isLoading: Boolean = false,
    val students: List<StudentDto> = emptyList(),
    val pageInfo: PageResultDto<StudentDto>? = null,
    val errorMessage: String? = null,
    val importResult: StudentImportDto? = null,
    val exportData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        
        other as StudentState
        
        if (isLoading != other.isLoading) return false
        if (students != other.students) return false
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
        result = 31 * result + students.hashCode()
        result = 31 * result + (pageInfo?.hashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + (importResult?.hashCode() ?: 0)
        result = 31 * result + (exportData?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * 学生Dialog状态
 */
data class StudentDialogState(
    val isVisible: Boolean = false,
    val isEditMode: Boolean = false,
    val name: String = "",
    val gender: Int = 1,
    val birthDate: String = "",
    val joinDate: String = "",
    val classId: Int = 1,
    val status: Int = 1,
    val editingStudentId: Int? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) {
    /**
     * 验证表单是否有效
     */
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                birthDate.isNotBlank() &&
                joinDate.isNotBlank()
    }
}

/**
 * 学生筛选状态
 */
data class StudentFilterState(
    val isFilterExpanded: Boolean = false,
    val selectedGender: Int? = null,
    val selectedClassId: Int? = null,
    val selectedStatus: Int? = null,
    val birthDateStart: String? = null,
    val birthDateEnd: String? = null,
    val joinDateStart: String? = null,
    val joinDateEnd: String? = null
) {
    /**
     * 检查是否有活跃的筛选条件
     */
    fun hasActiveFilters(): Boolean {
        return selectedGender != null ||
                selectedClassId != null ||
                selectedStatus != null ||
                !birthDateStart.isNullOrBlank() ||
                !birthDateEnd.isNullOrBlank() ||
                !joinDateStart.isNullOrBlank() ||
                !joinDateEnd.isNullOrBlank()
    }
    
    /**
     * 获取性别显示名称
     */
    fun getGenderName(gender: Int): String {
        return when (gender) {
            1 -> "男"
            2 -> "女"
            else -> "未知"
        }
    }
    
    /**
     * 获取状态显示名称
     */
    fun getStatusName(status: Int): String {
        return when (status) {
            1 -> "在读"
            2 -> "已毕业"
            3 -> "休学"
            4 -> "退学"
            else -> "未知"
        }
    }
}