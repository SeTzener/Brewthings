package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.repository.RaptPillRepository
import kotlinx.coroutines.launch

class GraphScreenViewModel(
    name: String?,
    macAddress: String,
    private val repo: RaptPillRepository,
) : ViewModel() {
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
                screenState = screenState.copy(graphData = pillData.toGraphData())
            }
        }
    }
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
