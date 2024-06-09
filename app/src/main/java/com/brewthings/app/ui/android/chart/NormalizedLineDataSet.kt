package com.brewthings.app.ui.android.chart

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class NormalizedLineDataSet(
    yVals: List<Entry>,
    label: String,
    coeff: Float,
    lineColor: Int,
    formatPattern: String,
): LineDataSet(emptyList(), label) {
    init {
        color = lineColor
        circleColors = listOf(lineColor)
        values = yVals.map { Entry(it.x, it.y * coeff, it.y) }
        valueFormatter = object : ValueFormatter() {
            override fun getPointLabel(entry: Entry): String {
                return DecimalFormat(formatPattern).format(entry.data)
            }
        }
    }
}
