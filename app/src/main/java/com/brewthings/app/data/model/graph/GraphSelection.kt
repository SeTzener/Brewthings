package com.brewthings.app.data.model.graph

/**
 * The selected graph value and its' screen coordinates.
 *
 * @param sensorValue The [GraphDataPoint] to show.
 * @param xPos The screen 'x' position of the selected value in the graph.
 * @param yPos The screen 'y' position of the selected value in the graph.
 */
data class GraphSelection(
    val sensorValue: GraphDataPoint,
    val xPos: Float,
    val yPos: Float
)
