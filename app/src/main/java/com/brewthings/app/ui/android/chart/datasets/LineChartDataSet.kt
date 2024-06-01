package com.brewthings.app.ui.android.chart.datasets

import com.brewthings.app.data.model.graph.GraphDataPoint
import com.brewthings.app.ui.components.graph.GraphConstants.Size

/**
 * A [SensorValuesDataSet] that shows the values of each given [sensor value][GraphDataPoint]. This data set
 * draws the values as a line in the chart.
 */
class LineChartDataSet(
    sensorValues: List<GraphDataPoint>,
    highlightEnabled: Boolean,
    lineColor: Int,
    asEntryYValue: GraphDataPoint.() -> Float
) : SensorValuesDataSet(sensorValues = sensorValues, asEntryYValue = asEntryYValue) {

    init {
        setDrawValues(false)
        setDrawCircles(sensorValues.size == 1)
        color = lineColor
        circleRadius = Size.LINE_WIDTH.value
        isHighlightEnabled = highlightEnabled
        setDrawHighlightIndicators(false)
        lineWidth = Size.LINE_WIDTH.value
        mode = Mode.HORIZONTAL_BEZIER
    }
}
