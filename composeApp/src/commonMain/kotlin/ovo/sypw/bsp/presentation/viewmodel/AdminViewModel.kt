package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.paging.DepartmentPagingSource
import ovo.sypw.bsp.domain.model.NetworkResult
import ovo.sypw.bsp.domain.usecase.DepartmentUseCase
import ovo.sypw.bsp.utils.Logger

/**
 * 后台管理ViewModel
 * 管理部门和员工相关的状态和业务逻辑
 */
class AdminViewModel(
    private val departmentUseCase: DepartmentUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "AdminViewModel"
    }
    
    // 当前选中的Tab索引
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()
    
    // 部门管理状态
    private val _departmentState = MutableStateFlow(DepartmentState())
    val departmentState: StateFlow<DepartmentState> = _departmentState.asStateFlow()
    
    // 部门Dialog状态
    private val _departmentDialogState = MutableStateFlow(DepartmentDialogState())
    val departmentDialogState: StateFlow<DepartmentDialogState> = _departmentDialogState.asStateFlow()
    
    // 部门搜索关键词
    private val _departmentSearchQuery = MutableStateFlow("")
    val departmentSearchQuery: StateFlow<String> = _departmentSearchQuery.asStateFlow()
    
    // 部门分页数据流
    private var _departmentPagingData: Flow<PagingData<DepartmentDto>>? = null
    val departmentPagingData: Flow<PagingData<DepartmentDto>>
        get() = _departmentPagingData ?: createDepartmentPager().also { _departmentPagingData = it }
    
    // 员工管理状态
    private val _employeeState = MutableStateFlow(EmployeeState())
    val employeeState: StateFlow<EmployeeState> = _employeeState.asStateFlow()
    
    /**
     * 切换Tab
     * @param index Tab索引
     */
    fun selectTab(index: Int) {
        _selectedTabIndex.value = index
    }
    
    /**
     * 创建部门分页器
     */
    private fun createDepartmentPager(): Flow<PagingData<DepartmentDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = {
                DepartmentPagingSource(
                    departmentUseCase = departmentUseCase,
                    searchName = _departmentSearchQuery.value.takeIf { it.isNotBlank() }
                )
            }
        ).flow.cachedIn(viewModelScope)
    }
    
    /**
     * 更新搜索关键词并刷新分页数据
     * @param query 搜索关键词
     */
    fun updateDepartmentSearchQuery(query: String) {
        _departmentSearchQuery.value = query
        // 重新创建分页器以应用新的搜索条件
        _departmentPagingData = null
    }
    
    /**
     * 清空搜索条件
     */
    fun clearDepartmentSearch() {
        updateDepartmentSearchQuery("")
    }
    
    /**
     * 刷新部门数据
     */
    fun refreshDepartments() {
        // 重新创建分页器以刷新数据
        _departmentPagingData = null
        // 同时保持原有的加载方法以兼容现有代码
        loadDepartments()
    }
    
    /**
     * 加载部门分页数据
     * @param current 当前页码
     * @param size 每页大小
     * @param name 部门名称（可选，用于搜索）
     */
    fun loadDepartments(
        current: Int = 1,
        size: Int = 10,
        name: String? = null
    ) {
        Logger.d(TAG, "加载部门数据: current=$current, size=$size, name=$name")
        
        viewModelScope.launch {
            _departmentState.value = _departmentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = departmentUseCase.getDepartmentPage(current, size, name)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "部门数据加载成功")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        departments = result.data.records,
                        pageInfo = result.data,
                        errorMessage = null
                    )
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门数据加载失败: ${result.message}")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 创建部门
     * @param name 部门名称
     */
    fun createDepartment(name: String) {
        Logger.d(TAG, "创建部门: name=$name")
        
        viewModelScope.launch {
            _departmentState.value = _departmentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = departmentUseCase.createDepartment(name)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "部门创建成功")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载部门列表
                    loadDepartments()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门创建失败: ${result.message}")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 更新部门
     * @param id 部门ID
     * @param name 部门名称
     */
    fun updateDepartment(id: Int, name: String) {
        Logger.d(TAG, "更新部门: id=$id, name=$name")
        
        viewModelScope.launch {
            _departmentState.value = _departmentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = departmentUseCase.updateDepartment(id, name)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "部门更新成功")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载部门列表
                    loadDepartments()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门更新失败: ${result.message}")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 删除部门
     * @param id 部门ID
     */
    fun deleteDepartment(id: Int?) {
        Logger.d(TAG, "删除部门: id=$id")
        
        viewModelScope.launch {
            _departmentState.value = _departmentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = departmentUseCase.deleteDepartment(id)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "部门删除成功")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载部门列表
                    loadDepartments()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门删除失败: ${result.message}")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 批量删除部门
     * @param ids 部门ID列表
     */
    fun batchDeleteDepartments(ids: List<Int>) {
        Logger.d(TAG, "批量删除部门: ids=$ids")
        
        viewModelScope.launch {
            _departmentState.value = _departmentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = departmentUseCase.batchDeleteDepartments(ids)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "部门批量删除成功")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载部门列表
                    loadDepartments()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门批量删除失败: ${result.message}")
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _departmentState.value = _departmentState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearDepartmentError() {
        _departmentState.value = _departmentState.value.copy(
            errorMessage = null
        )
    }
    
    /**
     * 显示添加部门Dialog
     */
    fun showAddDepartmentDialog() {
        _departmentDialogState.value = DepartmentDialogState(
            isVisible = true,
            isEditMode = false,
            departmentName = "",
            editingDepartmentId = null
        )
    }
    
    /**
     * 显示编辑部门Dialog
     * @param department 要编辑的部门
     */
    fun showEditDepartmentDialog(department: DepartmentDto) {
        _departmentDialogState.value = DepartmentDialogState(
            isVisible = true,
            isEditMode = true,
            departmentName = department.name,
            editingDepartmentId = department.id
        )
    }
    
    /**
     * 隐藏部门Dialog
     */
    fun hideDepartmentDialog() {
        _departmentDialogState.value = DepartmentDialogState()
    }
    
    /**
     * 更新部门名称输入
     * @param name 部门名称
     */
    fun updateDepartmentName(name: String) {
        _departmentDialogState.value = _departmentDialogState.value.copy(
            departmentName = name
        )
    }
    
    /**
     * 保存部门（添加或编辑）
     */
    fun saveDepartment() {
        val dialogState = _departmentDialogState.value
        val name = dialogState.departmentName.trim()
        
        if (name.isBlank()) {
            _departmentDialogState.value = dialogState.copy(
                errorMessage = "部门名称不能为空"
            )
            return
        }
        
        if (name.length > 50) {
            _departmentDialogState.value = dialogState.copy(
                errorMessage = "部门名称不能超过50个字符"
            )
            return
        }
        
        viewModelScope.launch {
            _departmentDialogState.value = dialogState.copy(
                isLoading = true,
                errorMessage = null
            )
            
            val result = if (dialogState.isEditMode && dialogState.editingDepartmentId != null) {
                // 编辑模式
                departmentUseCase.updateDepartment(dialogState.editingDepartmentId, name)
            } else {
                // 添加模式
                departmentUseCase.createDepartment(name)
            }
            
            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "部门保存成功")
                    hideDepartmentDialog()
                    // 刷新分页数据和传统列表数据
                    _departmentPagingData = null
                    refreshDepartments()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门保存失败: ${result.message}")
                    _departmentDialogState.value = _departmentDialogState.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "保存失败"
                    )
                }

                NetworkResult.Idle -> TODO()
                NetworkResult.Loading -> TODO()
            }
        }
    }
    

    /**
     * 刷新员工数据
     */
    fun refreshEmployees() {
        _employeeState.value = _employeeState.value.copy(
            isLoading = true
        )
        // TODO: 实现员工数据加载逻辑
        _employeeState.value = _employeeState.value.copy(
            isLoading = false
        )
    }
}

/**
 * 部门管理状态
 */
data class DepartmentState(
    val isLoading: Boolean = false,
    val departments: List<DepartmentDto> = emptyList(),
    val pageInfo: PageResultDto<DepartmentDto>? = null,
    val errorMessage: String? = null
)

/**
 * 部门Dialog状态
 */
data class DepartmentDialogState(
    val isVisible: Boolean = false,
    val isEditMode: Boolean = false,
    val departmentName: String = "",
    val editingDepartmentId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * 员工管理状态
 */
data class EmployeeState(
    val isLoading: Boolean = false,
    val employees: List<Employee> = emptyList(),
    val errorMessage: String? = null
)



/**
 * 员工数据模型
 */
data class Employee(
    val id: String,
    val name: String,
    val email: String,
    val department: String,
    val position: String
)