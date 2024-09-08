package com.brewthings.app.ui.screens.graph

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.brewthings.app.data.model.DataType
import com.brewthings.app.ui.android.chart.ChartDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

data class Segment(
    val isValid: Boolean,
    val entries: List<Entry>
)

fun List<DataPoint>.toSegments(): List<Segment> = emptyList()

@Composable
fun List<Segment>.toDataSets(type: DataType): List<ILineDataSet> = map { segment ->
    ChartDataSet(
        yVals = segment.entries,
        label = type.name,
        lineColor = type.toLineColor().copy(alpha = if (segment.isValid) 1f else 0.2f).toArgb(),
        formatPattern = type.toFormatPattern(),
    )
}

@Composable
private fun DataType.toLineColor(): Color = when (this) {
    DataType.GRAVITY -> MaterialTheme.colorScheme.primary
    DataType.TEMPERATURE -> MaterialTheme.colorScheme.secondary
    DataType.BATTERY -> MaterialTheme.colorScheme.tertiary
}

@Composable
private fun DataType.toFormatPattern(): String = when (this) {
    DataType.GRAVITY -> "0.000"
    DataType.TEMPERATURE,
    DataType.BATTERY -> "#.#"
}
