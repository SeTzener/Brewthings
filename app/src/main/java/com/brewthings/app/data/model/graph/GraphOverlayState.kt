package com.brewthings.app.data.model.graph

/**
 * The state of the view that is drawn on top of above the graph.
 */
sealed interface GraphOverlayState {

    /**
     * Loading state.
     */
    data object Loading : GraphOverlayState

    /**
     * Error state.
     */
    data object Error : GraphOverlayState

    /**
     * The state for when the sensor is calibrating.
     */
    data class SensorCalibrating(val daysLeft: Int) : GraphOverlayState

    /**
     * The entire segment was loaded and it contains no sensor values.
     */
    data object EmptySegment : GraphOverlayState

    /**
     * No sensor data is visible in the graph, but there might be sensor values to show.
     *
     * @param canShowAvailableData True if a button that allows animating the graph to show sensor values should be
     * shown.
     */
    data class NoVisibleData(val canShowAvailableData: Boolean) : GraphOverlayState
}
