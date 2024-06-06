package com.brewthings.app.ui.components.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.data.AxisValueOverrider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerValueFormatter
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarkerValueFormatter
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

@Composable
fun Graph(
    modifier: Modifier,
    color: Color,
    series: GraphSeries,
) {
    val modelProducer = rememberModelProducer(series)

    CartesianChartHost(
        modifier = modifier,
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                axisValueOverrider = AxisValueOverrider.fixed(minY = series.minY, maxY = series.maxY),
                lines = listOf(
                    rememberLineSpec(
                        shader = DynamicShader.color(color),
                        backgroundShader = null
                    )
                )
            ),
            startAxis = rememberStartAxis(valueFormatter = series.type.toYFormatter()),
            bottomAxis = rememberBottomAxis(valueFormatter = dateValueFormatter()),
        ),
        modelProducer = modelProducer,
        marker = rememberDefaultCartesianMarker(
            label = rememberMarkerTextComponent(),
            valueFormatter = series.type.toMarkerFormatter(),
        ),
        runInitialAnimation = true,
        zoomState = rememberVicoZoomState(zoomEnabled = true),
    )
}

@Composable
fun rememberModelProducer(series: GraphSeries): CartesianChartModelProducer {
    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(series) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    lineSeries {
                        series(series.xValues, series.yValues)
                    }
                }
            }
        }
    }

    return modelProducer
}

private fun dateValueFormatter(): CartesianValueFormatter {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
    return CartesianValueFormatter { x, _, _ ->
        LocalDate.ofEpochDay(x.toLong()).format(dateTimeFormatter)
    }
}

private fun gravityValueFormatter(): CartesianValueFormatter =
    CartesianValueFormatter { y, _, _ -> "%d".format((y * 1000 - 1000).toInt()) }

private fun integerValueFormatter(): CartesianValueFormatter =
    CartesianValueFormatter { y, _, _ -> "%d".format(y.toInt()) }

private fun DataType.toYFormatter(): CartesianValueFormatter = when (this) {
    DataType.Gravity -> gravityValueFormatter()
    DataType.Temperature,
    DataType.Battery -> integerValueFormatter()
}

private fun gravityMarkerFormatter(): CartesianMarkerValueFormatter =
    DefaultCartesianMarkerValueFormatter(
        decimalFormat = DecimalFormat("0.000"),
        colorCode = true
    )

private fun decimalMarkerFormatter(): CartesianMarkerValueFormatter =
    DefaultCartesianMarkerValueFormatter(
        decimalFormat = DecimalFormat("#.#"),
        colorCode = true
    )

private fun DataType.toMarkerFormatter(): CartesianMarkerValueFormatter = when (this) {
    DataType.Gravity -> gravityMarkerFormatter()
    DataType.Temperature,
    DataType.Battery -> decimalMarkerFormatter()
}
