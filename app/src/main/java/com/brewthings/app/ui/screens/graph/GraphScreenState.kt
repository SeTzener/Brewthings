package com.brewthings.app.ui.screens.graph

import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.model.RaptPillInsights

data class GraphScreenState(
    val title: String,
    val pillMacAddress: String,
    val graphData: GraphData? = null,
    val enabledTypes: Set<DataType> = emptySet(),
    val selectedInsights: RaptPillInsights? = null,
)
