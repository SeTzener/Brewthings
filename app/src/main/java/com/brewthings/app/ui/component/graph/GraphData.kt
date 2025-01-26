package com.brewthings.app.ui.component.graph

import kotlinx.datetime.Instant

data class GraphData(
    val from: Instant,
    val to: Instant,
    val series: List<GraphSeries>,
)

data class GraphSeries(val type: DataType, val data: List<DataPoint>)

data class DataPoint(
    val index: Int,
    val x: Float,
    val y: Float?,
    val isOG: Boolean,
    val isFG: Boolean,
)
