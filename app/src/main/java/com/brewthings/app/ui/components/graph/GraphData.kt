package com.brewthings.app.ui.components.graph

data class GraphData (
    val series: List<GraphSeries>
)

data class GraphSeries (
    val dataPoints: List<GraphDataPoint>
)

data class GraphDataPoint (
    val x: Long,
    val y: Float
)
