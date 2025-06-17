package ovo.sypw.bsp.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.dto.ClassDto
import ovo.sypw.bsp.data.dto.PageResultDto
import ovo.sypw.bsp.data.paging.PagingData
import ovo.sypw.bsp.data.dto.result.NetworkResult
import ovo.sypw.bsp.domain.usecase.ClassUseCase
import ovo.sypw.bsp.utils.Logger
import ovo.sypw.bsp.utils.PagingManager
import ovo.sypw.bsp.utils.PagingUtils

/**
 * 班级管理ViewModel
 * 专门负责班级相关的状态管理和业务逻辑
 */
class ClassViewModel(
    private val classUseCase: ClassUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "ClassViewModel"
    }
    
    // 班级管理状态
    private val _classState = MutableStateFlow(ClassState())
    val classState: StateFlow<ClassState> = _classState.asStateFlow()
    
    // 班级Dialog状态
    private val _classDialogState = MutableStateFlow(ClassDialogState())
    val classDialogState: StateFlow<ClassDialogState> = _classDialogState.asStateFlow()
    
    // 班级搜索关键词
    private val _classSearchQuery = MutableStateFlow("")
    val classSearchQuery: StateFlow<String> = _classSearchQuery.asStateFlow()
    
    // 班级分页数据流
    private var _classPagingManager: PagingManager<ClassDto>? = null
    val classPagingData: StateFlow<PagingData<ClassDto>>
        get() = getClassPagingManager().pagingData
    
    /**
     * 获取班级分页管理器
     */
    fun getClassPagingManager(): PagingManager<ClassDto> {
        if (_classPagingManager == null) {
            _classPagingManager = PagingUtils.createPagingManager(
                loadData = { page, pageSize ->
                    classUseCase.getClassPage(
                        current = page,
                        size = pageSize,
                        name = _classSearchQuery.value.takeIf { it.isNotBlank() }
                    )
                }
            )
        }
        return _classPagingManager!!
    }
    
    /**
     * 更新搜索关键词并刷新分页数据
     * @param query 搜索关键词
     */
    fun updateClassSearchQuery(query: String) {
        _classSearchQuery.value = query
        // 重新创建分页器以应用新的搜索条件
        _classPagingManager = null
        // 立即应用搜索条件并重新加载数据
        loadClasses(
            current = 1,
            size = _classState.value.pageInfo?.size ?: 5,
            name = query.takeIf { it.isNotBlank() }
        )
    }
    
    /**
     * 清空搜索条件
     */
    fun clearClassSearch() {
        updateClassSearchQuery("")
        // 重新加载所有数据
        loadClasses(current = 1, size = _classState.value.pageInfo?.size ?: 5)
    }
    
    /**
     * 刷新班级数据
     */
    fun refreshClasses() {
        // 重新创建分页器以刷新数据
        _classPagingManager = null
        // 同时保持原有的加载方法以兼容现有代码
        loadClasses()
    }
    
    /**
     * 加载班级分页数据
     * @param current 当前页码
     * @param size 每页大小
     * @param name 班级名称（可选，用于搜索）
     */
    fun loadClasses(
        current: Int = 1,
        size: Int = 5,
        name: String? = null
    ) {
        viewModelScope.launch {
            _classState.value = _classState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = classUseCase.getClassPage(current, size, name)) {
                is NetworkResult.Success -> {
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        classes = result.data.records,
                        pageInfo = result.data,
                        errorMessage = null
                    )
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "班级数据加载失败: ${result.message}")
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _classState.value = _classState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 创建班级
     * @param name 班级名称
     * @param grade 年级
     */
    fun createClass(name: String, grade: String) {
        Logger.d(TAG, "创建班级: name=$name, grade=$grade")
        
        viewModelScope.launch {
            _classState.value = _classState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = classUseCase.createClass(name, grade)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "班级创建成功")
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载班级列表
                    loadClasses()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "班级创建失败: ${result.message}")
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _classState.value = _classState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 更新班级
     * @param id 班级ID
     * @param name 班级名称
     * @param grade 年级
     */
    fun updateClass(id: Int, name: String, grade: String) {
        Logger.d(TAG, "更新班级: id=$id, name=$name, grade=$grade")
        
        viewModelScope.launch {
            _classState.value = _classState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = classUseCase.updateClass(id, name, grade)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "班级更新成功")
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载班级列表
                    loadClasses()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "班级更新失败: ${result.message}")
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _classState.value = _classState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 删除班级
     * @param id 班级ID
     */
    fun deleteClass(id: Int?) {
        Logger.d(TAG, "删除班级: id=$id")
        
        viewModelScope.launch {
            _classState.value = _classState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = classUseCase.deleteClass(id)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "班级删除成功")
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载班级列表
                    loadClasses()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "班级删除失败: ${result.message}")
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _classState.value = _classState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 批量删除班级
     * @param ids 班级ID列表
     */
    fun batchDeleteClasses(ids: List<Int>) {
        Logger.d(TAG, "批量删除班级: ids=$ids")
        
        viewModelScope.launch {
            _classState.value = _classState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            when (val result = classUseCase.batchDeleteClasses(ids)) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "班级批量删除成功")
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    // 重新加载班级列表
                    loadClasses()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "班级批量删除失败: ${result.message}")
                    _classState.value = _classState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    // 保持加载状态
                }
                is NetworkResult.Idle -> {
                    _classState.value = _classState.value.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearClassError() {
        _classState.value = _classState.value.copy(
            errorMessage = null
        )
    }
    
    /**
     * 显示添加班级Dialog
     */
    fun showAddClassDialog() {
        Logger.d(TAG, "显示添加班级Dialog")
        _classDialogState.value = ClassDialogState(
            isVisible = true,
            isEditMode = false,
            className = "",
            classGrade = "",
            editingClassId = null
        )
        Logger.d(TAG, "Dialog状态已更新: ${_classDialogState.value}")
    }
    
    /**
     * 显示编辑班级Dialog
     * @param classDto 要编辑的班级
     */
    fun showEditClassDialog(classDto: ClassDto) {
        _classDialogState.value = ClassDialogState(
            isVisible = true,
            isEditMode = true,
            className = classDto.name,
            classGrade = classDto.grade,
            editingClassId = classDto.id
        )
    }
    
    /**
     * 隐藏班级Dialog
     */
    fun hideClassDialog() {
        _classDialogState.value = ClassDialogState()
    }
    
    /**
     * 更新班级名称输入
     * @param name 班级名称
     */
    fun updateClassName(name: String) {
        _classDialogState.value = _classDialogState.value.copy(
            className = name
        )
    }
    
    /**
     * 更新年级输入
     * @param grade 年级
     */
    fun updateClassGrade(grade: String) {
        _classDialogState.value = _classDialogState.value.copy(
            classGrade = grade
        )
    }
    
    /**
     * 保存班级（添加或编辑）
     */
    fun saveClass() {
        val dialogState = _classDialogState.value
        val name = dialogState.className.trim()
        val grade = dialogState.classGrade.trim()
        
        if (name.isBlank()) {
            _classDialogState.value = dialogState.copy(
                errorMessage = "班级名称不能为空"
            )
            return
        }
        
        if (name.length > 50) {
            _classDialogState.value = dialogState.copy(
                errorMessage = "班级名称不能超过50个字符"
            )
            return
        }
        
        if (grade.isBlank()) {
            _classDialogState.value = dialogState.copy(
                errorMessage = "年级不能为空"
            )
            return
        }
        
        if (grade.length > 20) {
            _classDialogState.value = dialogState.copy(
                errorMessage = "年级不能超过20个字符"
            )
            return
        }
        
        viewModelScope.launch {
            _classDialogState.value = dialogState.copy(
                isLoading = true,
                errorMessage = null
            )
            
            val result = if (dialogState.isEditMode && dialogState.editingClassId != null) {
                // 编辑模式
                classUseCase.updateClass(dialogState.editingClassId, name, grade)
            } else {
                // 添加模式
                classUseCase.createClass(name, grade)
            }
            
            when (result) {
                is NetworkResult.Success -> {
                    Logger.i(TAG, "班级保存成功")
                    hideClassDialog()
                    // 刷新分页数据和传统列表数据
                    _classPagingManager = null
                    refreshClasses()
                }
                is NetworkResult.Error -> {
                    Logger.e(TAG, "班级保存失败: ${result.message}")
                    _classDialogState.value = _classDialogState.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "保存失败"
                    )
                }
                NetworkResult.Idle -> {
                    _classDialogState.value = _classDialogState.value.copy(
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
 * 班级管理状态
 */
data class ClassState(
    val isLoading: Boolean = false,
    val classes: List<ClassDto> = emptyList(),
    val pageInfo: PageResultDto<ClassDto>? = null,
    val errorMessage: String? = null
)

/**
 * 班级Dialog状态
 */
data class ClassDialogState(
    val isVisible: Boolean = false,
    val isEditMode: Boolean = false,
    val className: String = "",
    val classGrade: String = "",
    val editingClassId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)