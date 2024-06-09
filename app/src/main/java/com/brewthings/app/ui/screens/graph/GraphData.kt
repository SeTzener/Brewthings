package com.brewthings.app.ui.screens.graph

data class GraphData(val series: List<GraphSeries>)

data class GraphSeries(val type: DataType, val data: List<DataPoint>)

data class DataPoint(val x: Float, val y: Float)

enum class DataType {
    TEMPERATURE,
    GRAVITY,
    BATTERY
}
