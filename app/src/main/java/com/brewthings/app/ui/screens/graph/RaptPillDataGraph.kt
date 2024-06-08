package com.brewthings.app.ui.screens.graph

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.brewthings.app.ui.android.chart.RaptPillDataChart

@Composable
fun RaptPillDataGraph(
    modifier: Modifier = Modifier,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .background(color = surfaceColor),
        factory = { context ->
            RaptPillDataChart(context = context)
        },
        update = {
            it.showData() // TODO: Pass data to chart.
        }
    )
}
