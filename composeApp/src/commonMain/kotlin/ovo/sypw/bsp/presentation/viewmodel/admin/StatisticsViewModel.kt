package ovo.sypw.bsp.presentation.viewmodel.admin

import com.hoc081098.kmp.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ovo.sypw.bsp.data.model.StatisticsData
import ovo.sypw.bsp.domain.usecase.StatisticsUseCase
import ovo.sypw.bsp.utils.Logger

/**
 * 统计数据ViewModel
 */
class StatisticsViewModel(
    private val statisticsUseCase: StatisticsUseCase
) : ViewModel() {

    private val _statisticsState = MutableStateFlow(StatisticsState())
    val statisticsState: StateFlow<StatisticsState> = _statisticsState.asStateFlow()

    init {
        loadStatistics()
    }

    /**
     * 加载统计数据
     */
    fun loadStatistics() {
        Logger.d("StatisticsViewModel", "开始加载统计数据")

        viewModelScope.launch {
            _statisticsState.value = _statisticsState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val data = statisticsUseCase.getDashboardStatistics()
                _statisticsState.value = _statisticsState.value.copy(
                    isLoading = false,
                    data = data,
                    errorMessage = null
                )
                Logger.d("StatisticsViewModel", "统计数据加载成功")
            } catch (e: Exception) {
                _statisticsState.value = _statisticsState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "加载统计数据失败"
                )
                Logger.e("StatisticsViewModel", "统计数据加载失败", e)
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _statisticsState.value = _statisticsState.value.copy(errorMessage = null)
    }
}

/**
 * 统计数据状态
 */
data class StatisticsState(
    val isLoading: Boolean = false,
    val data: StatisticsData? = null,
    val errorMessage: String? = null
)