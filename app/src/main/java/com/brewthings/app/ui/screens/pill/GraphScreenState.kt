package com.brewthings.app.ui.screens.pill

import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.ui.screens.pill.graph.DataType
import com.brewthings.app.ui.screens.pill.graph.GraphSeries
import kotlinx.datetime.Instant

data class GraphScreenState(
    // Immutable state
    val title: String,
    val dataTypes: List<DataType>,
    // Selection state
    val selectedDataTypes: List<DataType>,
    val selectedDataIndex: Int? = null,
    // Data state
    val graphSeries: List<GraphSeries>? = null,
    val insights: List<RaptPillInsights>? = null,
    val feedings: List<Instant> = emptyList()
)
