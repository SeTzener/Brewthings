package com.brewthings.app.ui.components.graph

data class GraphData (
    val series: List<GraphSeries>
)

data class GraphSeries (
    val type: DataType,
    val minY: Float,
    val maxY: Float,
    val xValues: List<Float>,
    val yValues: List<Float>,
)

enum class DataType {
    Gravity,
    Temperature,
    Battery,
}
