package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.components.graph.GraphDataPoint
import com.brewthings.app.ui.components.graph.GraphSeries
import com.brewthings.app.ui.components.graph.GraphState
import java.time.Instant
import java.time.ZoneId
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
            repo.observeData(macAddress)
                .collect { data: List<RaptPillData> ->
                    screenState = screenState.copy(graphState = data.toGraphState())
                }
        }
    }
}

private fun List<RaptPillData>.toGraphState(): GraphState {
    val series = fold(
        initial = List(1) { mutableListOf<GraphDataPoint>() }
    ) { dataPoints, raptPillData ->
        dataPoints.also {
            it[0].add(GraphDataPoint(raptPillData.timestamp.toEpochDay(), raptPillData.gravity))
            //it[1].add(GraphDataPoint(raptPillData.timestamp.toEpochDay(), raptPillData.temperature))
        }
    }.map { dataPoints ->
        GraphSeries(dataPoints)
    }
    return GraphState(series)
}

private fun Instant.toEpochDay(): Float = atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay().toFloat()
