package com.brewthings.app.ui.android.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

sealed class ChartDataSet(
    dataSet: List<Entry>,
    label: String,
) : LineDataSet(dataSet, label) {
    class Valid(
        dataSet: List<Entry>,
        label: String,
        lineColor: Int,
        formatPattern: String,
    ) : ChartDataSet(dataSet, label) {
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

    class Invalid(
        dataSet: List<Entry>,
        label: String,
    ) : ChartDataSet(dataSet, label) {
        init {
            setDrawValues(false)
            setDrawCircles(false)
            color = Color.Transparent.toArgb()
            isHighlightEnabled = false
        }
    }
}
