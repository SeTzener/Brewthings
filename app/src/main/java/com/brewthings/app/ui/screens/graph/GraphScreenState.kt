package com.brewthings.app.ui.screens.graph

import com.brewthings.app.ui.components.graph.GraphData

data class GraphScreenState(
    val title: String,
    val pillMacAddress: String,
    val graphData: GraphData? = null,
)
