package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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

    fun toggleSeries(dataType: DataType) {
        val graphState = screenState.graphState ?: return
        val enabledTypes = when (dataType !in graphState.enabledTypes) {
            true -> graphState.enabledTypes + dataType
            false -> graphState.enabledTypes - dataType
        }

        if (enabledTypes.isEmpty()) return

        screenState = screenState.copy(
            graphState = graphState.copy(enabledTypes = enabledTypes)
        )
    }

    fun onGraphSelect(index: Int?) {
        onSelect(index)
    }

    fun onPagerSelect(index: Int) {
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
            combine(
                repo.observeOG(screenState.pillMacAddress),
                repo.observeData(screenState.pillMacAddress)
            ) { og, pillData ->
                val data = pillData.toGraphData()
                val insights = pillData.toInsights(og)

                screenState.copy(
                    graphState = GraphState(
                        graphData = data,
                        selectedDataIndex = data.series.lastIndex,
                        enabledTypes = data.series.map { it.type }.toSet()
                    ),
                    insightsPagerState = GraphInsightsPagerState(
                        insights = insights,
                        selectedInsightsIndex = insights.lastIndex
                    )
                )
            }.collect { state ->
                screenState = state
            }
        }
    }
}
