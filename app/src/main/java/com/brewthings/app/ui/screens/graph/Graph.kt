package com.brewthings.app.ui.screens.graph

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.viewinterop.AndroidView
import com.brewthings.app.R
import com.brewthings.app.ui.android.chart.ChartData
import com.brewthings.app.ui.android.chart.ChartDataSet
import com.brewthings.app.ui.android.chart.MpAndroidLineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun Graph(
    modifier: Modifier = Modifier,
    graphData: GraphData?,
) {
    val density: Density = LocalDensity.current
    val textSize = MaterialTheme.typography.labelMedium.fontSize

    val isDarkTheme = isSystemInDarkTheme()
    val textColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    val chartData = graphData?.toChartData()

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            MpAndroidLineChart(
                context = context,
                chartData = chartData,
                density = density,
                textSize = textSize,
                isDarkTheme = isDarkTheme,
                textColor = textColor,
                primaryColor = primaryColor
            )
        },
        update = { chart ->
            chart.refresh(
                chartData = chartData,
                isDarkTheme = isDarkTheme,
                textColor = textColor,
                primaryColor = primaryColor
            )
        }
    )
}

@Composable
private fun GraphData.toChartData(): ChartData = ChartData(
    data = LineData(
        series.map { it.toChartDataSet() }
    )
)

@Composable
private fun GraphSeries.toChartDataSet(): ILineDataSet = ChartDataSet(
    yVals = data.standardize(),
    label = type.toLabel(),
    lineColor = type.toLineColor().toArgb(),
    formatPattern = type.toFormatPattern(),
)

@Composable
private fun DataType.toLabel(): String = when (this) {
    DataType.TEMPERATURE -> stringResource(id = R.string.graph_dat_label_temperature)
    DataType.GRAVITY -> stringResource(id = R.string.graph_dat_label_gravity)
    DataType.BATTERY -> stringResource(id = R.string.graph_dat_label_battery)
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

/**
 * Normalizes the y values of the data points to a range between 0 and 1, for multiline chart plotting.
 * THe original y values are stored in the data field of the [Entry].
 */
/*private fun List<DataPoint>.normalize(): List<Entry> {
    val minY = minOf { it.y }
    val maxY = maxOf { it.y }
    return map { Entry(it.x, (it.y - minY) / (maxY - minY), it.y) }
}*/

/**
 * Transform the data using z-score normalization so that each sensor's readings are centered around the mean with a
 * standard deviation of 1, for multiline chart plotting.
 * The original y values are stored in the data field of the [Entry].
 */
private fun List<DataPoint>.standardize(): List<Entry> {
    val mean = map { it.y }.average().toFloat()
    val stdDev = sqrt(map { (it.y - mean).pow(2) }.average().toFloat())
    return map { Entry(it.x, (it.y - mean) / stdDev, it.y) }
}
