package ovo.sypw.bsp.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.DepartmentDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.usecase.DepartmentUseCase
import ovo.sypw.bsp.utils.Logger
import ovo.sypw.bsp.utils.PagingManager
import ovo.sypw.bsp.utils.PagingUtils

/**
 * 部门管理ViewModel
 * 专门负责部门相关的状态管理和业务逻辑
 */
class DepartmentViewModel(
    private val departmentUseCase: DepartmentUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "DepartmentViewModel"
    }
    
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
    private var _departmentPagingManager: PagingManager<DepartmentDto>? = null
    val departmentPagingData: StateFlow<PagingData<DepartmentDto>>
        get() = getDepartmentPagingManager().pagingData
    
    /**
     * 获取部门分页管理器
     */
    fun getDepartmentPagingManager(): PagingManager<DepartmentDto> {
        if (_departmentPagingManager == null) {
            _departmentPagingManager = PagingUtils.createPagingManager(
                loadData = { page, pageSize ->
                    departmentUseCase.getDepartmentPage(
                        current = page,
                        size = pageSize,
                        name = _departmentSearchQuery.value.takeIf { it.isNotBlank() }
                    )
                }
            )
        }
        return _departmentPagingManager!!
    }
    
    /**
     * 更新搜索关键词并刷新分页数据
     * @param query 搜索关键词
     */
    fun updateDepartmentSearchQuery(query: String) {
        _departmentSearchQuery.value = query
        // 重新创建分页器以应用新的搜索条件
        _departmentPagingManager = null
        // 立即应用搜索条件并重新加载数据
        loadDepartments(
            current = 1,
            size = _departmentState.value.pageInfo?.size ?: 5,
            name = query.takeIf { it.isNotBlank() }
        )
    }
    
    /**
     * 清空搜索条件
     */
    fun clearDepartmentSearch() {
        updateDepartmentSearchQuery("")
        // 重新加载所有数据
        loadDepartments(current = 1, size = _departmentState.value.pageInfo?.size ?: 5)
    }
    
    /**
     * 刷新部门数据
     */
    fun refreshDepartments() {
        // 重新创建分页器以刷新数据
        _departmentPagingManager = null
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
        size: Int = 9,
        name: String? = null
    ) {
        viewModelScope.launch {
            _departmentState.value = _departmentState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = departmentUseCase.getDepartmentPage(current, size, name)) {
                is NetworkResult.Success -> {
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
        Logger.d(TAG, "显示添加部门Dialog")
        _departmentDialogState.value = DepartmentDialogState(
            isVisible = true,
            isEditMode = false,
            departmentName = "",
            editingDepartmentId = null
        )
        Logger.d(TAG, "Dialog状态已更新: ${_departmentDialogState.value}")
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
                    _departmentPagingManager = null
                    refreshDepartments()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "部门保存失败: ${result.message}")
                    _departmentDialogState.value = _departmentDialogState.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "保存失败"
                    )
                }
                NetworkResult.Idle -> {
                    _departmentDialogState.value = _departmentDialogState.value.copy(
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