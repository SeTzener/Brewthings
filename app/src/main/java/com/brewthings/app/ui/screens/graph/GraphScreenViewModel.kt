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
        loadGraphData(macAddress)
        loadInsights()
    }

    private fun loadGraphData(macAddress: String) {
        viewModelScope.launch {
            repo.observeData(macAddress).collect { pillData ->
                val data = pillData.toGraphData()
                screenState = screenState.copy(
                    graphData = data,
                    enabledTypes = data.series.map { it.type }.toSet()
                )
            }
        }
    }

    private fun loadInsights() {
        viewModelScope.launch {
            combine(
                repo.observeOG(screenState.pillMacAddress),
                repo.observeData(screenState.pillMacAddress)
            ) { og, data ->
                data.toInsights(og)
            }.collect { insights ->
                screenState = screenState.copy(
                    insights = insights,
                    selectedInsights = insights.lastIndex
                )
            }
        }
    }

    fun toggleSeries(dataType: DataType) {
        val enabledTypes = when (dataType !in screenState.enabledTypes) {
            true -> screenState.enabledTypes + dataType
            false -> screenState.enabledTypes - dataType
        }

        if (enabledTypes.isEmpty()) return

        screenState = screenState.copy(enabledTypes = enabledTypes)
    }

    fun onSelect(index: Int) {
        screenState = screenState.copy(selectedInsights = index)
    }
}
