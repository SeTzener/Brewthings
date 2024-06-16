package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.repository.RaptPillInsightsRepository
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

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
    private val insightsRepo: RaptPillInsightsRepository by inject { parametersOf(macAddress) }

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
            insightsRepo.selectedInsights.collect { insights ->
                screenState = screenState.copy(selectedInsights = insights)
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

    fun onValueSelected(data: Any?) {
        viewModelScope.launch {
            val raptPillData = data as RaptPillData? // Any? is casted to RaptPillData?
            insightsRepo.setTimestamp(raptPillData?.timestamp)
        }
    }

    private fun RaptPillData.toDataPoint(toY: RaptPillData.() -> Float): DataPoint = DataPoint(
        x = timestamp.epochSeconds.toFloat(),
        y = toY(),
        data = this // RaptPillData is passed as Any?
    )

    private fun List<RaptPillData>.toGraphData(): GraphData {
        val series = listOf(
            GraphSeries(
                type = DataType.TEMPERATURE,
                data = map { it.toDataPoint { temperature } }
            ),
            GraphSeries(
                type = DataType.GRAVITY,
                data = map { it.toDataPoint { gravity } }
            ),
            GraphSeries(
                type = DataType.BATTERY,
                data = map { it.toDataPoint { battery } }
            )
        )
        return GraphData(series)
    }
}
