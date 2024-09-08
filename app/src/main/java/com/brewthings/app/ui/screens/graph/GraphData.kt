package com.brewthings.app.ui.screens.graph

import com.brewthings.app.data.model.DataType

data class GraphData(val series: List<GraphSeries>)

data class GraphSeries(val type: DataType, val data: List<DataPoint>)

data class DataPoint(
    val x: Float,
    val y: Float,
    val isOG: Boolean,
    val isFG: Boolean,
    val data: Any?
)
