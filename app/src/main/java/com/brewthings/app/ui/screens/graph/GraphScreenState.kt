package com.brewthings.app.ui.screens.graph

data class GraphScreenState(
    val title: String,
    val pillMacAddress: String,
    val dataTypes: List<DataType>,
    val selectedDataType: DataType,
    val selectedDataIndex: Int? = null,
    val graphState: GraphState? = null,
    val insightsState: InsightsState? = null,
)
