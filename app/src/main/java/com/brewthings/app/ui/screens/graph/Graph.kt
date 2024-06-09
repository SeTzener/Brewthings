package com.brewthings.app.ui.screens.graph

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.brewthings.app.R
import com.brewthings.app.ui.android.chart.ChartData
import com.brewthings.app.ui.android.chart.MpAndroidLineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

@Composable
fun Graph(
    modifier: Modifier = Modifier,
    graphData: GraphData?,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val chartData = graphData?.toChartData()
    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .background(color = surfaceColor),
        factory = { context ->
            MpAndroidLineChart(
                context = context,
                chartData = chartData,
            )
        },
        update = { chart ->
            chartData?.also { data ->
                chart.showData(data)
            }
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
private fun GraphSeries.toChartDataSet(): ILineDataSet = LineDataSet(
    data.map { Entry(it.x, it.y) },
    type.toLabel()
)

@Composable
private fun DataType.toLabel(): String = when (this) {
    DataType.TEMPERATURE -> stringResource(id = R.string.graph_dat_label_temperature)
    DataType.GRAVITY -> stringResource(id = R.string.graph_dat_label_gravity)
    DataType.BATTERY -> stringResource(id = R.string.graph_dat_label_battery)
}
