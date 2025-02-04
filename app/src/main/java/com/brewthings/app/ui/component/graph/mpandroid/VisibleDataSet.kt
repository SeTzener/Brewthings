package com.brewthings.app.ui.component.graph.mpandroid

import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class VisibleDataSet(
    dataSet: List<Entry>,
    label: String,
    lineColor: Int,
    formatPattern: String,
) : LineDataSet(dataSet, label) {
    init {
        setDrawValues(false)
        setDrawCircleHole(false)
        color = lineColor
        circleColors = listOf(lineColor)
        circleRadius = Size.Graph.CIRCLE_RADIUS.value
        lineWidth = Size.Graph.LINE_WIDTH.value
        mode = Mode.HORIZONTAL_BEZIER

        valueFormatter = object : ValueFormatter() {
            override fun getPointLabel(entry: Entry): String {
                return DecimalFormat(formatPattern).format(entry.data)
            }
        }
    }
}
