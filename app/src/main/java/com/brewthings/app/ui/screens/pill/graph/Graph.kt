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
import com.brewthings.app.ui.screens.pill.data.DataType
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
                primaryColor = primaryColor,
            )
        },
    )
}

@Composable
private fun GraphState.toChartData(dataType: DataType): ChartData = ChartData(
    data = LineData(
        series.find { it.type == dataType }
            ?.data
            ?.toSegments()
            ?.toDataSets(dataType),
    ),
)

/**
 * Transform the data using z-score normalization so that each sensor's readings are centered around the mean with a
 * standard deviation of 1 (for multiline chart plotting).
 */
/*private fun List<DataPoint>.standardize(): List<Entry> {
    val mean = map { it.y }.average().toFloat()
    val stdDev = sqrt(map { (it.y - mean).pow(2) }.average().toFloat())
    return map { Entry(it.x, (it.y - mean) / stdDev, it.data) }
}*/
