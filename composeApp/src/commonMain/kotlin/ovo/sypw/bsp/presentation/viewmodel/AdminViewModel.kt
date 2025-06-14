package ovo.sypw.bsp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ovo.sypw.bsp.utils.Logger

/**
 * 后台管理ViewModel
 * 负责管理后台页面的Tab切换和基础状态
 */
class AdminViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "AdminViewModel"
    }
    
    // 当前选中的Tab索引
    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()
    
    /**
     * 切换Tab
     * @param index Tab索引
     */
    fun selectTab(index: Int) {
        Logger.d(TAG, "切换Tab: index=$index")
        _selectedTabIndex.value = index
    }
}