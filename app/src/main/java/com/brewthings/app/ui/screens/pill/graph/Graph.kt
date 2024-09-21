package com.brewthings.app.ui.screens.pill.graph

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.viewinterop.AndroidView
import com.brewthings.app.ui.android.chart.ChartData
import com.brewthings.app.ui.android.chart.MpAndroidLineChart
import com.brewthings.app.ui.screens.pill.data.toDataSets
import com.brewthings.app.ui.screens.pill.data.toSegments
import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.data.LineData

@Composable
fun Graph(
    state: GraphState,
    dataType: DataType,
    selectedIndex: Int?,
    onSelect: (Int?) -> Unit,
) {
    val density: Density = LocalDensity.current
    val textSize = MaterialTheme.typography.labelMedium.fontSize
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    val chartData = state.toChartData(dataType)

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(Size.Graph.HEIGHT)
            .padding(bottom = Size.Graph.PADDING_BOTTOM),
        factory = { context ->
            MpAndroidLineChart(
                context = context,
                chartData = chartData,
                selectedIndex = selectedIndex,
                density = density,
                textSize = textSize,
                isDarkTheme = isDarkTheme,
                textColor = textColor,
                primaryColor = primaryColor,
                onSelect = onSelect,
            )
        },
        update = { chart ->
            chart.refresh(
                chartData = chartData,
                selectedIndex = selectedIndex,
                isDarkTheme = isDarkTheme,
                textColor = textColor,
                primaryColor = primaryColor
            )
        }
    )
}

@Composable
private fun GraphState.toChartData(dataType: DataType): ChartData = ChartData(
    data = LineData(
        series.map {
            it.data
                .normalize()
                .toSegments()
                .toDataSets(it.type)
        }.flatten()
    )
)

/**
 * Interpolates y-values to the range [0, 1], for multiline chart plotting.
 */
private fun List<DataPoint>.normalize(): List<DataPoint> {
    if (this.isEmpty()) return emptyList()

    // Find the minimum and maximum y-values
    val minY = this.minOf { it.y }
    val maxY = this.maxOf { it.y }

    // Handle the case where all points have the same y-value to avoid division by zero
    if (minY == maxY) {
        return this.map { it.copy(x = it.x, y = 0.5f) } // Normalize to the middle of the target range
    }

    // Interpolate
    return this.map { dataPoint ->
        val normalizedY = (dataPoint.y - minY) / (maxY - minY)
        dataPoint.copy(x = dataPoint.x, y = normalizedY)
    }
}
