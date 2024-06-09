package com.brewthings.app.ui.screens.graph

import com.brewthings.app.util.maxOfOrDefault

data class GraphData(val series: List<GraphSeries>) {
    val yMax = series.maxOfOrDefault { it.yMax }
}

data class GraphSeries(val type: DataType, val data: List<DataPoint>) {
    val yMax = data.maxOfOrDefault { it.y }
}

data class DataPoint(val x: Float, val y: Float)

enum class DataType {
    TEMPERATURE,
    GRAVITY,
    BATTERY
}
