package com.brewthings.app.ui.screens.graph

import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.model.RaptPillInsights

data class GraphScreenState(
    val title: String,
    val pillMacAddress: String,
    val graphData: GraphData? = null,
    val insights: List<RaptPillInsights> = emptyList(),
    val selectedInsights: Int = -1,
    val enabledTypes: Set<DataType> = emptySet(),
)
