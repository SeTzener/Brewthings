package com.brewthings.app.ui.screens.pill.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.brewthings.app.ui.android.chart.ChartDataSet
import com.brewthings.app.ui.android.chart.MpAndroidLineChartData
import com.brewthings.app.ui.screens.pill.toLabel
import com.brewthings.app.ui.theme.DarkTurquoise
import com.brewthings.app.ui.theme.Gold
import com.brewthings.app.ui.theme.LimeGreen
import com.brewthings.app.ui.theme.MediumPurple
import com.brewthings.app.ui.theme.Orange
import com.brewthings.app.ui.theme.SteelBlue
import com.brewthings.app.ui.theme.Tomato
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData

@Composable
fun List<GraphSeries>.toChartData(): MpAndroidLineChartData = LineData(
    map {
        it.toChartDataSet()
    }.flatten()
)

@Composable
private fun GraphSeries.toChartDataSet(): List<ChartDataSet> {
    val chartDataSets = mutableListOf<ChartDataSet>()
    val currentValidData = mutableListOf<Entry>()
    val currentInvalidData = mutableListOf<Entry>()
    var startedWithOG = false

    // Derive values from type
    val label = type.toLabel()
    val lineColor = type.toLineColor().toArgb()
    val formatPattern = type.toFormatPattern()

    // Helper function to finalize and add a valid dataset
    fun finalizeValidSequence() {
        if (currentValidData.isNotEmpty()) {
            val color = if (startedWithOG) lineColor else lineColor.alpha(0.2f)
            chartDataSets.add(
                ChartDataSet.Valid(currentValidData, label, color, formatPattern)
            )
            currentValidData.clear()
            startedWithOG = false
        }
    }

    // Helper function to finalize and add an invalid dataset
    fun finalizeInvalidSequence() {
        if (currentInvalidData.isNotEmpty()) {
            chartDataSets.add(
                ChartDataSet.Invalid(currentInvalidData, label)
            )
            currentInvalidData.clear()
        }
    }

    // Iterate through all data points
    for (dataPoint in data) {
        val entry = Entry(dataPoint.x, dataPoint.y ?: 0f, dataPoint.y)

        if (dataPoint.y == null) {
            // Data point is invalid, finalize any valid sequence and start invalid
            finalizeValidSequence()
            currentInvalidData.add(entry)
        } else {
            // Data point is valid
            finalizeInvalidSequence()

            // Handle OG and FG cases
            if (dataPoint.isOG) {
                finalizeValidSequence() // Close the previous valid sequence if any
                startedWithOG = true
            }

            currentValidData.add(entry)

            if (dataPoint.isFG) {
                finalizeValidSequence() // Close the current valid sequence on FG
            }
        }
    }

    // Finalize any remaining sequences
    finalizeValidSequence()
    finalizeInvalidSequence()

    return chartDataSets
}

@Composable
private fun DataType.toLineColor(): Color = when (this) {
    DataType.GRAVITY -> SteelBlue
    DataType.TEMPERATURE -> MediumPurple
    DataType.BATTERY -> Tomato
    DataType.TILT -> DarkTurquoise
    DataType.ABV -> LimeGreen
    DataType.VELOCITY_MEASURED -> Orange
    DataType.VELOCITY_COMPUTED -> Gold
}

@Composable
private fun DataType.toFormatPattern(): String = when (this) {
    DataType.GRAVITY -> "0.000"
    DataType.TEMPERATURE,
    DataType.BATTERY,
    DataType.TILT,
    DataType.ABV,
    DataType.VELOCITY_MEASURED,
    DataType.VELOCITY_COMPUTED -> "#.#"
}

private fun Int.alpha(alpha: Float) : Int = ColorUtils.setAlphaComponent(this, (alpha * 255).toInt())
