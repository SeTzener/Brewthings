package com.brewthings.app.ui.android.chart.datasets

import com.brewthings.app.data.model.graph.SegmentSensorValue
import com.brewthings.app.ui.theme.Size

/**
 * A [SensorValuesDataSet] that shows the values of each given [sensor value][SegmentSensorValue]. This data set
 * draws the values as a line in the chart.
 */
class LineChartDataSet(
    sensorValues: List<SegmentSensorValue>,
    highlightEnabled: Boolean,
    lineColor: Int,
    asEntryYValue: SegmentSensorValue.() -> Float
) : SensorValuesDataSet(sensorValues = sensorValues, asEntryYValue = asEntryYValue) {

    init {
        setDrawValues(false)
        setDrawCircles(sensorValues.size == 1)
        color = lineColor
        circleRadius = Size.Graph.LINE_WIDTH.value
        isHighlightEnabled = highlightEnabled
        setDrawHighlightIndicators(false)
        lineWidth = Size.Graph.LINE_WIDTH.value
        mode = Mode.HORIZONTAL_BEZIER
    }
}
