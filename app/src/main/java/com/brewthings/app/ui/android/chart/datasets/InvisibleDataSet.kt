package com.brewthings.app.ui.android.chart.datasets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

/**
 * An invisible [LineDataSet].
 */
@Suppress("LeakingThis")
open class InvisibleDataSet(entries: MutableList<Entry>) : LineDataSet(entries, "") {

    constructor(xValues: List<Float>, yValue: Float) :
        this(entries = xValues.map<Float, Entry> { Entry(it, yValue) }.toMutableList<Entry>())

    init {
        setDrawValues(false)
        setDrawCircles(false)
        color = Color.Transparent.toArgb()
        isHighlightEnabled = false
    }
}
