package com.brewthings.app.ui.screens.graph

import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.model.RaptPillInsights

data class GraphScreenState(
    val title: String,
    val pillMacAddress: String,
    val graphState: GraphState? = null,
    val insightsPagerState: GraphInsightsPagerState? = null,
)

data class GraphState(
    val graphData: GraphData,
    val selectedDataIndex: Int?,
    val enabledTypes: Set<DataType>,
)

data class GraphInsightsPagerState(
    val selectedInsightsIndex: Int?,
    val insights: List<RaptPillInsights>,
)
