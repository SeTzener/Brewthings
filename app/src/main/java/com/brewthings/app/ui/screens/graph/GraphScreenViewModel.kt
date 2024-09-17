package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GraphScreenViewModel(
    name: String? = ParameterHolder.Graph.name,
    macAddress: String = ParameterHolder.Graph.macAddress ?: error("macAddress is required")
) : ViewModel(), KoinComponent {
    var screenState: GraphScreenState by mutableStateOf(
        GraphScreenState(
            title = name ?: macAddress,
            pillMacAddress = macAddress,
        )
    )
        private set

    private val repo: RaptPillRepository by inject()

    init {
        loadData()
    }

    fun selectSeries(dataType: DataType) {
        val graphState = screenState.graphState ?: return

        screenState = screenState.copy(
            graphState = graphState.copy(selectedDataType = dataType)
        )
    }

    fun onGraphSelect(index: Int?) {
        GraphSelectionLogger.logGraphSelect(index)
        onSelect(index)
    }

    fun onPagerSelect(index: Int) {
        GraphSelectionLogger.logPagerSelect(index)
        onSelect(index)
    }

    private fun onSelect(index: Int?) {
        screenState = screenState.copy(
            insightsPagerState = screenState.insightsPagerState?.copy(selectedInsightsIndex = index),
            graphState = screenState.graphState?.copy(selectedDataIndex = index),
        )
    }

    private fun loadData() {
        viewModelScope.launch {
            repo.observeData(screenState.pillMacAddress)
                .collect { pillData ->
                    val data = pillData.toGraphData()
                    val insights = pillData.toInsights()
                    val defaultIndex = insights.lastIndex
                    val types = DataType.entries.toList()
                    val previousType = screenState.graphState?.selectedDataType
                    val type = previousType ?: types[0]

                    screenState = screenState.copy(
                        graphState = GraphState(
                            graphData = data,
                            selectedDataIndex = screenState.graphState?.selectedDataIndex
                                ?: defaultIndex,
                            selectedDataType = type,
                            dataTypes = types,
                        ),
                        insightsPagerState = GraphInsightsPagerState(
                            insights = insights,
                            selectedDataType = type,
                            selectedInsightsIndex = screenState.insightsPagerState?.selectedInsightsIndex
                                ?: defaultIndex
                        )
                    )
                }
        }
    }

    fun setIsOG(macAddress: String, timestamp: Instant, isOg: Boolean?) {
        viewModelScope.launch {
            if (isOg != null) {
                repo.setIsOG(macAddress = macAddress, timestamp = timestamp, isOg = isOg)
            }
        }
    }

    fun setIsFG(macAddress: String, timestamp: Instant, isFg: Boolean?) {
        viewModelScope.launch {
            if (isFg != null) {
                repo.setIsFG(macAddress = macAddress, timestamp = timestamp, isOg = isFg)
            }
        }
    }
}
