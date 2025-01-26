package com.brewthings.app.ui.component.graph

data class GraphSeries(val type: DataType, val data: List<DataPoint>)

data class DataPoint(
    val index: Int,
    val x: Float,
    val y: Float?,
    val isOG: Boolean,
    val isFG: Boolean,
)
