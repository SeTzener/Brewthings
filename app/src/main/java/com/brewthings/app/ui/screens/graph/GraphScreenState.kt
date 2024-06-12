package com.brewthings.app.ui.screens.graph

data class GraphScreenState(
    val title: String,
    val pillMacAddress: String,
    val graphData: GraphData? = null,
    val enabledTypes: Set<DataType> = emptySet(),
)
