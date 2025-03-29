package com.brewthings.app.ui.screen.graph

import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.model.Brew
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.ui.component.graph.GraphData
import kotlinx.datetime.Instant

data class GraphState(
    // Immutable state
    val title: String,
    val dataTypes: List<DataType>,
    // Graph customizations
    val showInsightsCardActions: Boolean,
    val brew: Brew?,
    // Selection state
    val selectedDataTypes: List<DataType>,
    val selectedDataIndex: Int? = null,
    // Data state
    val graphData: GraphData? = null,
    val insights: List<RaptPillInsights>? = null,
    val feedings: List<Instant> = emptyList(),
)
