package com.brewthings.app.ui.screens.pill

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder
import com.brewthings.app.ui.screens.pill.data.DataType
import com.brewthings.app.ui.screens.pill.graph.toGraphState
import com.brewthings.app.ui.screens.pill.insights.toInsightsState
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GraphScreenViewModel(
    val macAddress: String = ParameterHolder.Graph.macAddress ?: error("macAddress is required"),
    name: String? = ParameterHolder.Graph.name,
) : ViewModel(), KoinComponent {
    var screenState: GraphScreenState by mutableStateOf(createInitialState(name, macAddress))
        private set

    private val repo: RaptPillRepository by inject()

    init {
        loadData()
    }

    fun selectSeries(dataType: DataType) {
        screenState = screenState.copy(selectedDataType = dataType)
    }

    fun onGraphSelect(index: Int?) {
        GraphScreenLogger.logGraphSelect(index)
        onSelect(index)
    }

    fun onPagerSelect(index: Int) {
        GraphScreenLogger.logPagerSelect(index)
        onSelect(index)
    }

    private fun onSelect(index: Int?) {
        screenState = screenState.copy(selectedDataIndex = index)
    }

    private fun loadData() {
        viewModelScope.launch {
            repo.observeData(macAddress)
                .collect { pillData ->
                    val defaultIndex = pillData.lastIndex
                    val graphState = pillData.toGraphState()
                    val insightsState = pillData.toInsightsState()

                    screenState = screenState.copy(
                        selectedDataIndex = screenState.selectedDataIndex ?: defaultIndex,
                        graphState = graphState,
                        insightsState = insightsState
                    )
                }
        }
    }

    fun setIsOG(timestamp: Instant, isOg: Boolean?) {
        viewModelScope.launch {
            if (isOg != null) {
                repo.setIsOG(macAddress = macAddress, timestamp = timestamp, isOg = isOg)
            }
        }
    }

    fun setIsFG(timestamp: Instant, isFg: Boolean?) {
        viewModelScope.launch {
            if (isFg != null) {
                repo.setIsFG(macAddress = macAddress, timestamp = timestamp, isOg = isFg)
            }
        }
    }

    private fun createInitialState(name: String?, macAddress: String) : GraphScreenState {
        val types = DataType.entries.toList()
        return GraphScreenState(
            title = name ?: macAddress,
            dataTypes = types,
            selectedDataType = types[0]
        )
    }
}
