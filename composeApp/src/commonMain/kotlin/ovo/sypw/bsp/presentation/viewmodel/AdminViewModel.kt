package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 后台管理ViewModel
 * 管理部门和员工相关的状态和业务逻辑
 */
class AdminViewModel : ViewModel() {
    
    // 当前选中的Tab索引
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()
    
    // 部门管理状态
    private val _departmentState = MutableStateFlow(DepartmentState())
    val departmentState: StateFlow<DepartmentState> = _departmentState.asStateFlow()
    
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
     * 刷新部门数据
     */
    fun refreshDepartments() {
        _departmentState.value = _departmentState.value.copy(
            isLoading = true
        )
        // TODO: 实现部门数据加载逻辑
        _departmentState.value = _departmentState.value.copy(
            isLoading = false
        )
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
    val departments: List<Department> = emptyList(),
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
 * 部门数据模型
 */
data class Department(
    val id: String,
    val name: String,
    val description: String,
    val employeeCount: Int = 0
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