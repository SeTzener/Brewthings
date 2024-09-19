package com.brewthings.app.ui.screens.pill

import com.brewthings.app.ui.screens.pill.data.DataType
import com.brewthings.app.ui.screens.pill.graph.GraphState
import com.brewthings.app.ui.screens.pill.insights.InsightsState

data class GraphScreenState(
    val title: String,
    val dataTypes: List<DataType>,
    val selectedDataType: DataType,
    val selectedDataIndex: Int? = null,
    val graphState: GraphState? = null,
    val insightsState: InsightsState? = null,
)
