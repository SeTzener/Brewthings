package com.brewthings.app.ui.android.chart

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

class NormalizedLineDataSet(
    yVals: List<Entry>,
    label: String,
    coeff: Float,
    lineColor: Int,
): LineDataSet(emptyList(), label) {
    init {
        color = lineColor
        values = yVals.map { Entry(it.x, it.y * coeff) }
    }
}
