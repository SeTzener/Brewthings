package com.brewthings.app.ui.screens.graph

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.brewthings.app.R
import com.brewthings.app.data.model.DataType
import com.brewthings.app.data.model.RaptPillInsights
import com.brewthings.app.ui.android.chart.ChartData
import com.brewthings.app.ui.android.chart.ChartDataSet
import com.brewthings.app.ui.android.chart.MpAndroidLineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Graph(
    modifier: Modifier = Modifier,
    graphData: GraphData?,
    insights: RaptPillInsights?,
    enabledTypes: Set<DataType>,
    toggleSeries: (DataType) -> Unit,
    onValueSelected: (Any?) -> Unit,
) {
    val density: Density = LocalDensity.current
    val textSize = MaterialTheme.typography.labelMedium.fontSize

    val isDarkTheme = isSystemInDarkTheme()
    val textColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    val chartData = graphData?.toChartData(enabledTypes)

    Column(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { context ->
                MpAndroidLineChart(
                    context = context,
                    chartData = chartData,
                    density = density,
                    textSize = textSize,
                    isDarkTheme = isDarkTheme,
                    textColor = textColor,
                    primaryColor = primaryColor,
                    onValueSelected = onValueSelected,
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

        FlowRow(modifier = Modifier.fillMaxWidth()) {
            graphData?.series?.forEach {
                LegendItem(
                    type = it.type,
                    isChecked = enabledTypes.contains(it.type),
                    onCheckedChange = { toggleSeries(it.type) }
                )
            }
        }

        insights?.also {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                RaptPillReadings(data = it)
            }
        }
    }
}

@Composable
fun LegendItem(
    type: DataType,
    isChecked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(top = 24.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckedChange() },
            colors = CheckboxDefaults.colors(
                checkedColor = type.toLineColor(),
                uncheckedColor = type.toLineColor(),
            )
        )
        Text(
            modifier = Modifier.clickable { onCheckedChange() },
            text = type.toLabel(),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun GraphData.toChartData(enabledTypes: Set<DataType>): ChartData = ChartData(
    data = LineData(
        series.mapNotNull {
            if (enabledTypes.contains(it.type)) it.toChartDataSet(isMultiChart = enabledTypes.size > 1) else null
        }
    )
)

@Composable
private fun GraphSeries.toChartDataSet(isMultiChart: Boolean): ILineDataSet = ChartDataSet(
    yVals = if (isMultiChart) data.standardize() else data.convert(),
    label = type.toLabel(),
    lineColor = type.toLineColor().toArgb(),
    formatPattern = type.toFormatPattern(),
)

@Composable
private fun DataType.toLabel(): String = when (this) {
    DataType.TEMPERATURE -> stringResource(id = R.string.graph_data_label_temperature)
    DataType.GRAVITY -> stringResource(id = R.string.graph_data_label_gravity)
    DataType.BATTERY -> stringResource(id = R.string.graph_data_label_battery)
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
 * Converts the data points to entries for plotting on the chart.
 */
private fun List<DataPoint>.convert(): List<Entry> = map { Entry(it.x, it.y, it.data) }

/**
 * Transform the data using z-score normalization so that each sensor's readings are centered around the mean with a
 * standard deviation of 1, for multiline chart plotting.
 */
private fun List<DataPoint>.standardize(): List<Entry> {
    val mean = map { it.y }.average().toFloat()
    val stdDev = sqrt(map { (it.y - mean).pow(2) }.average().toFloat())
    return map { Entry(it.x, (it.y - mean) / stdDev, it.data) }
}
