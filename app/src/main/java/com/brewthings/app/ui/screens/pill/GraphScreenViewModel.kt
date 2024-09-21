package com.brewthings.app.ui.screens.pill

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder
import com.brewthings.app.ui.screens.pill.graph.DataPoint
import com.brewthings.app.ui.screens.pill.graph.DataType
import com.brewthings.app.ui.screens.pill.graph.GraphSeries
import com.brewthings.app.ui.screens.pill.insights.toInsights
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

    private val dataPointsMap = mutableMapOf<DataType, List<DataPoint>>()

    init {
        loadData()
    }

    fun toggleDataType(dataType: DataType) {
        val oldDataTypes = screenState.selectedDataTypes
        val newDataTypes = if (oldDataTypes.contains(dataType)) {
            oldDataTypes - dataType
        } else {
            oldDataTypes + dataType
        }

        screenState = screenState.copy(
            selectedDataTypes = newDataTypes,
            graphSeries = updateGraphSeries(
                dataTypes = newDataTypes,
                insights = screenState.insights,
            ),
        )
    }

    fun onGraphSelect(index: Int?) {
        GraphScreenLogger.logGraphSelect(index)
        onSelect(index)
    }

    fun onPagerSelect(index: Int) {
        GraphScreenLogger.logPagerSelect(index)
        onSelect(index)
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

    private fun createInitialState(name: String?, macAddress: String): GraphScreenState =
        GraphScreenState(
            title = name ?: macAddress,
            dataTypes = DataType.entries,
            selectedDataTypes = listOf(DataType.GRAVITY, DataType.TEMPERATURE, DataType.VELOCITY_MEASURED, DataType.ABV)
        )

    private fun loadData() {
        viewModelScope.launch {
            repo.observeData(macAddress)
                .collect { pillData ->
                    val defaultIndex = pillData.lastIndex
                    val insights = pillData.toInsights()
                    dataPointsMap.clear()

                    screenState = screenState.copy(
                        selectedDataIndex = screenState.selectedDataIndex ?: defaultIndex,
                        insights = insights,
                        graphSeries = updateGraphSeries(
                            dataTypes = screenState.selectedDataTypes,
                            insights = insights,
                        ),
                    )
                }
        }
    }

    private fun updateGraphSeries(
        dataTypes: List<DataType>,
        insights: List<RaptPillInsights>,
    ): List<GraphSeries> =
        dataTypes.map { dataType ->
            val dataPoints = dataPointsMap[dataType] ?: insights.toDataPoints(dataType).also {
                dataPointsMap[dataType] = it
            }

            GraphSeries(
                type = dataType,
                data = dataPoints,
            )
        }

    private fun onSelect(index: Int?) {
        screenState = screenState.copy(selectedDataIndex = index)
    }
}

private fun List<RaptPillInsights>.toDataPoints(dataType: DataType): List<DataPoint> =
    mapIndexed { index, insights ->
        DataPoint(
            index = index,
            x = insights.timestamp.epochSeconds.toFloat(),
            y = insights.toY(dataType),
            isOG = insights.isOG,
            isFG = insights.isFG,
        )
    }

private fun RaptPillInsights.toY(dataType: DataType): Float? =
    when (dataType) {
        DataType.GRAVITY -> gravity
        DataType.TEMPERATURE -> temperature
        DataType.BATTERY -> battery
        DataType.TILT -> tilt
        DataType.ABV -> abv
        DataType.VELOCITY_MEASURED -> gravityVelocity
        DataType.VELOCITY_COMPUTED -> calculatedVelocity
    }?.value

/**
 * Interpolates y-values to the range [0, 1], for multiline chart plotting.
 */
private fun List<Float?>.normalize(): List<Float?> {
    val listNotNull = filterNotNull()

    if (listNotNull.isEmpty()) return emptyList()

    // Find the minimum and maximum y-values
    val min = listNotNull.min()
    val max = listNotNull.max()

    // Handle the case where all points have the same y-value to avoid division by zero
    if (min == max) {
        return List(size) { 0.5f } // Normalize to the middle of the target range
    }

    // Interpolate
    return map {
        it?.let { value ->
            (value - min) / (max - min)
        }
    }
}
