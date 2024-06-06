package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.components.graph.DataType
import com.brewthings.app.ui.components.graph.GraphData
import com.brewthings.app.ui.components.graph.GraphSeries
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
                    screenState = screenState.copy(graphData = data.toGraphData())
                }
        }
    }
}

private fun List<RaptPillData>.toGraphData(): GraphData =
    fold(
        initial = List(4) { mutableListOf<Float>() },
    ) { list, raptPillData ->
        list[0].add(raptPillData.timestamp.toEpochDay())
        list[1].add(raptPillData.gravity)
        list[2].add(raptPillData.temperature)
        list[3].add(raptPillData.battery)
        list
    }.let { (epochDays, gravity, temperature, battery) ->
        GraphData(
            series = listOf(
                GraphSeries(
                    type = DataType.Gravity,
                    minY = (gravity.min() - 0.01f).coerceAtLeast(1f),
                    maxY = (gravity.max() + 0.01f).coerceAtMost(1.13f),
                    xValues = epochDays,
                    yValues = gravity,
                ),
                GraphSeries(
                    type = DataType.Temperature,
                    minY = temperature.min() - 1f,
                    maxY = temperature.max() + 1f,
                    xValues = epochDays,
                    yValues = temperature,
                ),
                GraphSeries(
                    type = DataType.Battery,
                    minY = (battery.min() - 10f).coerceAtLeast(0f),
                    maxY = (battery.max() + 10f).coerceAtMost(100f),
                    xValues = epochDays,
                    yValues = battery,
                ),
            )
        )
    }

private fun Instant.toEpochDay(): Float = atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay().toFloat()
