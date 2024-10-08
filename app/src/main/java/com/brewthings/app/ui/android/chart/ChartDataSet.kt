package com.brewthings.app.ui.android.chart

import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class ChartDataSet(
    yVals: List<Entry>,
    label: String,
    lineColor: Int,
    formatPattern: String,
) : LineDataSet(emptyList(), label) {
    init {
        color = lineColor
        circleColors = listOf(lineColor)
        setDrawCircleHole(false)
        circleRadius = Size.Graph.CIRCLE_RADIUS.value
        lineWidth = Size.Graph.LINE_WIDTH.value
        mode = Mode.HORIZONTAL_BEZIER
        setDrawValues(false)

        values = yVals

        valueFormatter = object : ValueFormatter() {
            override fun getPointLabel(entry: Entry): String {
                return DecimalFormat(formatPattern).format(entry.data)
            }
        }
    }
}
