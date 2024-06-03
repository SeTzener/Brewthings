package com.brewthings.app.ui.screens.graph

import com.brewthings.app.ui.components.graph.GraphState

data class GraphScreenState(
    val title: String,
    val pillMacAddress: String,
    val graphState: GraphState? = null,
)
