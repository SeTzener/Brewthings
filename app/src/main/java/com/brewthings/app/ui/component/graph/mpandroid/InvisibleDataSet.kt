package com.brewthings.app.ui.component.graph.mpandroid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

class InvisibleDataSet(
    dataSet: List<Entry>,
    label: String,
) : LineDataSet(dataSet, label) {
    init {
        setDrawValues(false)
        setDrawCircles(false)
        color = Color.Transparent.toArgb()
        isHighlightEnabled = false
    }
}
