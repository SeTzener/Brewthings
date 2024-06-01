package com.brewthings.app.data.model.graph

import java.time.Instant

/**
 * Events that the graph view can handle.
 */
sealed interface SensorValuesGraphEvent {

    /**
     * This event occurs when the user changes the selected time span.
     */
    data object TimeSpanChanged : SensorValuesGraphEvent

    /**
     * When this event occurs, the graph should move the view port to the end of the graph.
     */
    data object MoveToEnd : SensorValuesGraphEvent

    /**
     * When this event occurs, the graph should center its' view on the given [instant].
     */
    data class CenterOn(val instant: Instant) : SensorValuesGraphEvent

    /**
     * When this event occurs, the graph should center its' view on the given [instant] with an animated transition.
     */
    data class CenterAnimatedOn(val instant: Instant) : SensorValuesGraphEvent

    /**
     * When this event occurs, the graph should clear selected value.
     */
    data object ClearSelectedValue : SensorValuesGraphEvent
}
