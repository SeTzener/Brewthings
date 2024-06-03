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
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

@Composable
fun Graph(
    modifier: Modifier,
    colors: List<Color>,
    state: GraphState,
) {
    val modelProducer = rememberModelProducer(state)

    CartesianChartHost(
        modifier = modifier,
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = List(state.series.size) { index ->
                    rememberLineSpec(
                        shader = DynamicShader.color(colors[index % colors.size]),
                        backgroundShader = null
                    )
                }
            ),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
        ),
        modelProducer = modelProducer,
        marker = rememberMarker(),
        runInitialAnimation = true,
        zoomState = rememberVicoZoomState(zoomEnabled = true),
    )
}

@Composable
fun rememberModelProducer(state: GraphState): CartesianChartModelProducer {
    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(state) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    lineSeries {
                        state.series
                            .forEach { series ->
                                series.dataPoints
                                    .fold(
                                        initial = mutableListOf<Number>() to mutableListOf<Number>()
                                    ) { (xSeries, ySeries), point ->
                                        xSeries.add(point.x)
                                        ySeries.add(point.y)
                                        xSeries to ySeries
                                    }.also { (xSeries, ySeries) ->
                                        series(xSeries, ySeries)
                                    }
                            }
                    }
                }
            }
        }
    }

    return modelProducer
}
