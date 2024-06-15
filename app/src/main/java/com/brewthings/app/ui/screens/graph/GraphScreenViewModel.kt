package com.brewthings.app.ui.screens.graph

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.model.Insight
import com.brewthings.app.data.model.OGInsight
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.data.repository.RaptPillRepository
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder
import com.brewthings.app.util.Logger
import kotlin.math.abs
import kotlinx.coroutines.launch

class GraphScreenViewModel(
    private val repo: RaptPillRepository,
) : ViewModel() {
    private val name: String? = ParameterHolder.Graph.name
    private val macAddress: String = ParameterHolder.Graph.macAddress ?: error("macAddress is required")

    private val logger = Logger("GraphScreenViewModel")

    private var insights: List<RaptPillInsights> = emptyList()

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
                insights = pillData.toInsights()
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

    fun onValueSelected(data: Any?) {
        val raptPillData = data as RaptPillData // Any? is casted to RaptPillData
        val selectedInsight = insights.find { it.timestamp == raptPillData.timestamp }
        if (selectedInsight != null) {
            screenState = screenState.copy(selectedInsights = selectedInsight)
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

    private fun List<RaptPillData>.toInsights(): List<RaptPillInsights> {
        if (isEmpty()) return emptyList()

        val ogData = first() //TODO: get from db.

        return mapIndexed { index, pillData ->
            if (index == 0 || pillData == ogData) {
                RaptPillInsights(
                    timestamp = pillData.timestamp,
                    temperature = Insight(value = pillData.temperature),
                    gravity = Insight(value = pillData.gravity),
                    battery = Insight(value = pillData.battery),
                    tilt = Insight(value = pillData.floatingAngle),
                )
            } else {
                val abv = calculateABV(ogData.gravity, pillData.gravity)
                val velocity = calculateVelocity(ogData, pillData)?.let { abs(it) }
                val previous = get(index - 1)
                val previousAbv = calculateABV(ogData.gravity, previous.gravity)
                RaptPillInsights(
                    timestamp = pillData.timestamp,
                    temperature = Insight(
                        value = pillData.temperature,
                        deltaFromPrevious = pillData.temperature - previous.temperature,
                        deltaFromOG = pillData.temperature - ogData.temperature,
                    ),
                    gravity = Insight(
                        value = pillData.gravity,
                        deltaFromPrevious = pillData.gravity - previous.gravity,
                        deltaFromOG = pillData.gravity - ogData.gravity,
                    ),
                    battery = Insight(
                        value = pillData.battery,
                        deltaFromPrevious = pillData.battery - previous.battery,
                        deltaFromOG = pillData.battery - ogData.battery,
                    ),
                    tilt = Insight(
                        value = pillData.floatingAngle,
                        deltaFromPrevious = pillData.floatingAngle - previous.floatingAngle,
                        deltaFromOG = pillData.floatingAngle - ogData.floatingAngle,
                    ),
                    abv = OGInsight(
                        value = abv,
                        deltaFromPrevious = abv - previousAbv,
                    ),
                    velocity = velocity?.let {
                        OGInsight(
                            value = it,
                            deltaFromPrevious = calculateVelocity(previous, pillData),
                        )
                    },
                )
            }
        }
    }

    private fun calculateABV(og: Float, fg: Float): Float {
        if (og <= 1.0 || fg <= 1.0) {
            logger.error("Invalid OG or FG values: og=$og, fg=$fg")
            return 0f
        }
        return (og - fg) * 131.25f
    }

    private fun calculateVelocity(ogData: RaptPillData, fgData: RaptPillData): Float? {
        val gravityDrop = fgData.gravity - ogData.gravity
        val timeDifference = (fgData.timestamp - ogData.timestamp).inWholeDays.toFloat()
        val velocity = gravityDrop / timeDifference
        return if (velocity.isInfinite() || velocity.isNaN()) {
            null
        } else velocity
    }
}
