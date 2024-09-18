package com.brewthings.app.ui.screens.pill.graph

import com.brewthings.app.ui.screens.pill.data.DataType

data class GraphState(val series: List<GraphSeries>)

data class GraphSeries(val type: DataType, val data: List<DataPoint>)

data class DataPoint(
    val x: Float,
    val y: Float,
    val isOG: Boolean,
    val isFG: Boolean,
    val data: Any?
)
