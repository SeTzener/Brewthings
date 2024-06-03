package com.brewthings.app.ui.components.graph

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

private const val TAG = "Graph"

@Composable
fun Graph(
    modifier: Modifier,
    loadData: () -> Result<GraphData>
) {
    val modelProducer = rememberModelProducer(loadData)

    CartesianChartHost(
        modifier = modifier,
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(
                        shader = DynamicShader.color(MaterialTheme.colorScheme.primary),
                        backgroundShader = null
                    ),
                    rememberLineSpec(
                        shader = DynamicShader.color(MaterialTheme.colorScheme.secondary),
                        backgroundShader = null
                    ),
                )
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
fun rememberModelProducer(loadData: () -> Result<GraphData>): CartesianChartModelProducer {
    val modelProducer = remember { CartesianChartModelProducer.build() }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            while (isActive) {
                modelProducer.tryRunTransaction {
                    loadData()
                        .onFailure { Log.e(TAG, "Failed to load data.", it) }
                        .onSuccess { data ->
                            lineSeries {
                                data.series
                                    .forEach { series ->
                                        series.dataPoints
                                            .fold(
                                                initial = mutableListOf<Number>() to mutableListOf<Number>()
                                            ) { (xSeries, ySeries), point ->
                                                xSeries + point.x
                                                ySeries + point.y
                                                xSeries to ySeries
                                            }.also { (xSeries, ySeries) ->
                                                series(xSeries, ySeries)
                                            }
                                    }
                            }
                        }

                    lineSeries {
                        series(List(10) { it.toFloat() })
                    }
                }
            }
        }
    }
    return modelProducer
}
