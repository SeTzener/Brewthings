package com.brewthings.app.ui.screen.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.model.Brew
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.data.repository.BrewsRepository
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.component.graph.DataPoint
import com.brewthings.app.ui.component.graph.GraphData
import com.brewthings.app.ui.component.graph.GraphSeries
import com.brewthings.app.ui.component.insights.toInsights
import com.brewthings.app.ui.navigation.ParameterHolders
import com.brewthings.app.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

val graphDataTypes = listOf(
    DataType.GRAVITY,
    DataType.TEMPERATURE,
    DataType.BATTERY,
    DataType.ABV,
    DataType.VELOCITY_MEASURED,
    DataType.VELOCITY_COMPUTED,
)

abstract class GraphScreenViewModel(
    private val screenTitle: String,
    private val showInsightsCardActions: Boolean,
) : ViewModel(), KoinComponent {
    abstract val brew: Brew?

    var screenState: GraphState by mutableStateOf(createInitialState())
        private set

    private val dataPointsMap = mutableMapOf<DataType, List<DataPoint>>()
    private val logger = Logger("GraphScreenViewModel")

    fun toggleDataType(dataType: DataType) {
        val oldDataTypes = screenState.selectedDataTypes

        if (oldDataTypes.size == 1 && oldDataTypes.contains(dataType)) {
            return
        }

        val newDataTypes = if (oldDataTypes.contains(dataType)) {
            oldDataTypes - dataType
        } else {
            oldDataTypes + dataType
        }

        screenState = screenState.copy(
            selectedDataTypes = newDataTypes,
            graphData = screenState.insights?.let { insights ->
                updateGraphData(
                    dataTypes = newDataTypes,
                    insights = insights,
                )
            },
        )
    }

    fun onGraphSelect(index: Int?) {
        logger.info("GraphSelect: index=$index")
        onSelect(index)
    }

    fun onPagerSelect(index: Int) {
        logger.info("PagerSelect: index=$index")
        onSelect(index)
    }

    abstract fun setIsOG(timestamp: Instant, isOg: Boolean?)

    abstract fun setIsFG(timestamp: Instant, isFg: Boolean?)

    abstract fun setFeeding(timestamp: Instant, isFeeding: Boolean?)

    abstract fun deleteMeasurement(timestamp: Instant)

    abstract fun observeRaptPillData(): Flow<List<RaptPillData>>

    private fun createInitialState(): GraphState =
        GraphState(
            title = screenTitle,
            showInsightsCardActions = showInsightsCardActions,
            dataTypes = graphDataTypes,
            selectedDataTypes = listOf(DataType.GRAVITY),
            brew = brew,
        )

    protected fun loadData() {
        viewModelScope.launch {
            observeRaptPillData()
                .collect { pillData ->
                    val feedings = pillData.toFeedingTimestamps()
                    val defaultIndex = pillData.lastIndex
                    val insights = pillData.toInsights()
                    dataPointsMap.clear()
                    screenState = screenState.copy(
                        selectedDataIndex = screenState.selectedDataIndex ?: defaultIndex,
                        insights = insights,
                        graphData = updateGraphData(
                            dataTypes = screenState.selectedDataTypes,
                            insights = insights,
                        ),
                        feedings = feedings,
                        brew = brew,
                    )
                }
        }
    }

    private fun updateGraphData(
        dataTypes: List<DataType>,
        insights: List<RaptPillInsights>,
    ): GraphData =
        GraphData(
            from = insights.first().timestamp,
            to = insights.last().timestamp,
            series = dataTypes.map { dataType ->
                val dataPoints = dataPointsMap[dataType] ?: insights.toDataPoints(dataType).also {
                    dataPointsMap[dataType] = it
                }

                GraphSeries(
                    type = dataType,
                    data = dataPoints,
                )
            },
        )

    private fun onSelect(index: Int?) {
        screenState = screenState.copy(selectedDataIndex = index)
    }

    private fun List<RaptPillData>.toFeedingTimestamps(): List<Instant> =
        filterIndexed { index, item ->
            if (index == 0) {
                false
            } else item.gravity > this[index - 1].gravity
        }.map { it.timestamp }
}

private fun List<RaptPillInsights>.toDataPoints(dataType: DataType): List<DataPoint> {
    val normalizedY = map {
        it.toY(dataType)
    }.normalize()

    return mapIndexed { index, insights ->
        DataPoint(
            index = index,
            x = insights.timestamp.epochSeconds.toFloat(),
            y = normalizedY[index],
            isOG = insights.isOG,
            isFG = insights.isFG,
        )
    }
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
    val notNulls = filterNotNull()

    // Handle the case where there are no values, or all values are null
    if (notNulls.isEmpty()) return this

    // Find the minimum and maximum y-values
    val min = notNulls.min()
    val max = notNulls.max()

    // Handle the case where all points have the same value to avoid division by zero
    if (min == max) {
        return List(size) { 0.5f } // Normalize to the middle of the target range
    }

    // Interpolate
    return map { value ->
        value?.let {
            (it - min) / (max - min)
        }
    }
}

class PillGraphScreenViewModel(
    val macAddress: String = ParameterHolders.PillGraph.macAddress
        ?: error("macAddress is required"),
    name: String? = ParameterHolders.PillGraph.name,
) : GraphScreenViewModel(
    screenTitle = name ?: macAddress,
    showInsightsCardActions = true,
) {
    override val brew = null

    private val repo: RaptPillRepository by inject()

    init {
        // Can't be moved to the superclass, or else the params won't be initialized.
        loadData()
    }

    override fun setIsOG(timestamp: Instant, isOg: Boolean?) {
        viewModelScope.launch {
            if (isOg != null) {
                repo.setIsOG(macAddress = macAddress, timestamp = timestamp, isOg = isOg)
            }
        }
    }

    override fun setIsFG(timestamp: Instant, isFg: Boolean?) {
        viewModelScope.launch {
            if (isFg != null) {
                repo.setIsFG(macAddress = macAddress, timestamp = timestamp, isOg = isFg)
            }
        }
    }

    override fun setFeeding(timestamp: Instant, isFeeding: Boolean?) {
        viewModelScope.launch {
            if (isFeeding != null) {
                repo.setFeeding(
                    macAddress = macAddress,
                    timestamp = timestamp,
                    isFeeding = isFeeding,
                )
            }
        }
    }

    override fun deleteMeasurement(timestamp: Instant) {
        viewModelScope.launch {
            repo.deleteMeasurement(macAddress = macAddress, timestamp = timestamp)
        }
    }

    override fun observeRaptPillData(): Flow<List<RaptPillData>> = repo.observeData(macAddress)
}

class BrewsGraphScreenViewModel(
    override val brew: Brew = ParameterHolders.BrewGraph.brew ?: error("brew is required"),
) : GraphScreenViewModel(
    screenTitle = brew.macAddress, // TODO(walt): change
    showInsightsCardActions = false,
) {
    private val repo: BrewsRepository by inject()

    init {
        // Can't be moved to the superclass, or else the params won't be initialized.
        loadData()
    }

    override fun setIsOG(timestamp: Instant, isOg: Boolean?) {
        // TODO(walt): hidden for now
    }

    override fun setIsFG(timestamp: Instant, isFg: Boolean?) {
        // TODO(walt): hidden for now
    }

    override fun setFeeding(timestamp: Instant, isFeeding: Boolean?) {
        // TODO(walt): hidden for now
    }

    override fun deleteMeasurement(timestamp: Instant) {
        // TODO(walt): hidden for now
    }

    override fun observeRaptPillData(): Flow<List<RaptPillData>> = repo.observeBrewData(brew)
}
