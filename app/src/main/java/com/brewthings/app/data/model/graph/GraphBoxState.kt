package com.brewthings.app.data.model.graph

import java.time.Instant

/**
 *  The state of the DeviceSensorScreen.
 *
 *  @param title The screen title.
 *  @param graphTimeSpan The currently selected [GraphTimeSpan].
 *  @param title The graph title.
 *  @param graphOverviewState The graph overview state.
 *  @param visibleRange The currently visible graph time range.
 *  @param selectedValue The value that is currently selected in the graph.
 *  @param graphOverlayState The state of the graph overlay view.
 */
data class GraphBoxState(
    val graphTimeSpan: GraphTimeSpan,
    val graphOverviewState: GraphOverviewState? = null,
    val visibleRange: ClosedRange<Instant>,
    val selectedValue: GraphSelection? = null,
    val graphOverlayState: GraphOverlayState? = null,
    val graphState: GraphState,
) {
    val dateFormat: DateFormat? get() = selectedValue?.sensorValue?.timestamp?.toDateFormat()
}
