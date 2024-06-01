package com.brewthings.app.ui.components.graph

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.viewinterop.AndroidView
import com.brewthings.app.data.model.graph.GraphState
import com.brewthings.app.data.model.graph.GraphTheme
import com.brewthings.app.data.model.graph.GraphTimeSpan
import com.brewthings.app.data.model.graph.GraphSelection
import com.brewthings.app.data.model.graph.GraphEvent
import com.brewthings.app.ui.android.chart.SensorValuesChart
import java.time.Instant
import kotlinx.coroutines.flow.Flow

/**
 * A composable [AndroidView] wrapper for a [SensorValuesGraph] that shows sensor values in a graph.
 *
 * @param onVisibleRangeChanged A function that the graph is expected to call when the graph visible time range changes.
 * @param onSelectedValueChanged A function that the graph is expected to call when the graph selected value changes.
 * @param graphState The graph state.
 * @param events A [Flow] of [GraphEvent] events that are handled by [SensorValuesChart].
 */
@Composable
fun SensorValuesGraph(
    graphState: GraphState,
    graphTimeSpan: GraphTimeSpan,
    events: Flow<GraphEvent>,
    onVisibleRangeChanged: (ClosedRange<Instant>) -> Unit,
    onSelectedValueChanged: (GraphSelection?) -> Unit,
) {
    val density: Density = LocalDensity.current
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColor = MaterialTheme.colorScheme.secondary.toArgb()
    val theme = if (isSystemInDarkTheme()) GraphTheme.DARK else GraphTheme.LIGHT
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        factory = { context ->
            SensorValuesChart(
                context = context,
                density = density,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                theme = theme,
                graphState = graphState,
                graphTimeSpan = graphTimeSpan,
                events = events,
                onVisibleRangeChanged = onVisibleRangeChanged,
                onSelectedValueChanged = onSelectedValueChanged,
            )
        },
        update = {
            it.showData(
                graphState = graphState,
                graphTimeSpan = graphTimeSpan
            )
        }
    )
}
