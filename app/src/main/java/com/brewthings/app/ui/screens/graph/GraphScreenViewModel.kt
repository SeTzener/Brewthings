package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder
import kotlinx.coroutines.launch

class GraphScreenViewModel(
    private val repo: RaptPillRepository,
) : ViewModel() {
    private val name: String? = ParameterHolder.Graph.name
    private val macAddress: String = ParameterHolder.Graph.macAddress ?: error("macAddress is required")

    var screenState: GraphScreenState by mutableStateOf(
        GraphScreenState(
            title = name ?: macAddress,
            pillMacAddress = macAddress,
        )
    )
        private set

    init {
        loadGraphData(macAddress)
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

    fun toggleSeries(dataType: DataType) {
        val enabledTypes = when (dataType !in screenState.enabledTypes) {
            true -> screenState.enabledTypes + dataType
            false -> screenState.enabledTypes - dataType
        }

        if (enabledTypes.isEmpty()) return

        screenState = screenState.copy(enabledTypes = enabledTypes)
    }

    private fun RaptPillData.toDataPoint(toY: RaptPillData.() -> Float): DataPoint = DataPoint(
        x = timestamp.epochSeconds.toFloat(),
        y = toY()
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
