package com.brewthings.app.ui.component.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.brewthings.app.ui.component.graph.mpandroid.InvisibleDataSet
import com.brewthings.app.ui.component.graph.mpandroid.MpAndroidChartData
import com.brewthings.app.ui.component.graph.mpandroid.VisibleDataSet
import com.brewthings.app.ui.screen.graph.toLabel
import com.brewthings.app.ui.theme.Coral
import com.brewthings.app.ui.theme.DarkTurquoise
import com.brewthings.app.ui.theme.Gold
import com.brewthings.app.ui.theme.LimeGreen
import com.brewthings.app.ui.theme.MediumPurple
import com.brewthings.app.ui.theme.RedAlert
import com.brewthings.app.ui.theme.SteelBlue
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

@Composable
fun List<GraphSeries>.toChartData(): MpAndroidChartData = MpAndroidChartData(
    map {
        it.toChartDataSet()
    }.flatten(),
)

@Composable
private fun GraphSeries.toChartDataSet(): List<ILineDataSet> {
    val chartDataSets = mutableListOf<ILineDataSet>()
    var currentValidData = mutableListOf<Entry>()
    var currentInvalidData = mutableListOf<Entry>()
    var startedWithOG = false

    // Derive values from type
    val label = type.toLabel()
    val lineColor = type.toLineColor()
    val formatPattern = type.toFormatPattern()

    // Helper function to finalize and add a valid dataset
    fun finalizeValidSequence() {
        if (currentValidData.isNotEmpty()) {
            val color = lineColor.takeIf { startedWithOG } ?: lineColor.copy(alpha = 0.2f)
            chartDataSets.add(
                VisibleDataSet(currentValidData, label, color.toArgb(), formatPattern),
            )
            currentValidData = mutableListOf()
        }
    }

    // Helper function to finalize and add an invalid dataset
    fun finalizeInvalidSequence() {
        if (currentInvalidData.isNotEmpty()) {
            chartDataSets.add(
                InvisibleDataSet(currentInvalidData, label),
            )
            currentInvalidData = mutableListOf()
        }
    }

    // Iterate through all data points
    for (dataPoint in data) {
        val entry = Entry(dataPoint.x, dataPoint.y ?: 0f, dataPoint.index)

        if (dataPoint.y == null) {
            // Data point is invalid, finalize any valid sequence and start invalid
            finalizeValidSequence()

            // Add the entry to the current invalid sequence
            currentInvalidData.add(entry)
        } else {
            // Data point is valid
            finalizeInvalidSequence()

            // Add the entry to the current valid sequence
            currentValidData.add(entry)

            // Handle OG and FG
            if (dataPoint.isOG || dataPoint.isFG) {
                // Close the current valid sequence
                finalizeValidSequence()

                // Add the entry to the new sequence
                currentValidData.add(entry)
            }
        }

        // Handle startedWithOG
        startedWithOG = when {
            dataPoint.isOG -> true
            dataPoint.isFG -> false
            else -> startedWithOG
        }
    }

    // Finalize any remaining sequences
    finalizeValidSequence()
    finalizeInvalidSequence()

    return chartDataSets
}

@Composable
fun DataType.toLineColor(): Color = when (this) {
    DataType.GRAVITY -> SteelBlue
    DataType.TEMPERATURE -> MediumPurple
    DataType.BATTERY -> RedAlert
    DataType.TILT -> DarkTurquoise
    DataType.ABV -> LimeGreen
    DataType.VELOCITY_MEASURED -> Coral
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
    DataType.VELOCITY_COMPUTED,
    -> "#.#"
}
