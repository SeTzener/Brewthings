package com.brewthings.app.ui.screens.pill.graph

data class GraphState(val series: Map<DataType, List<DataPoint>>)

enum class DataType {
    GRAVITY,
    TEMPERATURE,
    BATTERY,
    TILT,
    ABV,
    MEASURED_VELOCITY,
    COMPUTED_VELOCITY
}

data class DataPoint(
    val x: Float,
    val y: Float,
    val isOG: Boolean,
    val isFG: Boolean,
    val data: Any?
)
