package com.brewthings.app.data.model.graph

import java.time.Instant

/**
 * Events that the graph view can handle.
 */
sealed interface GraphEvent {

    /**
     * This event occurs when the user changes the selected time span.
     */
    data object TimeSpanChanged : GraphEvent

    /**
     * When this event occurs, the graph should move the view port to the end of the graph.
     */
    data object MoveToEnd : GraphEvent

    /**
     * When this event occurs, the graph should center its' view on the given [instant].
     */
    data class CenterOn(val instant: Instant) : GraphEvent

    /**
     * When this event occurs, the graph should center its' view on the given [instant] with an animated transition.
     */
    data class CenterAnimatedOn(val instant: Instant) : GraphEvent

    /**
     * When this event occurs, the graph should clear selected value.
     */
    data object ClearSelectedValue : GraphEvent
}
