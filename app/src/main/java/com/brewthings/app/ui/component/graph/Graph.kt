package com.brewthings.app.ui.component.graph

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
import com.brewthings.app.ui.component.graph.mpandroid.MpAndroidChart
import com.brewthings.app.ui.theme.Size

@Composable
fun Graph(
    series: List<GraphSeries>,
    selectedIndex: Int?,
    onSelect: (Int?) -> Unit,
) {
    val density: Density = LocalDensity.current
    val textSize = MaterialTheme.typography.labelMedium.fontSize
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    val chartData = series.toChartData()

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(Size.Graph.HEIGHT)
            .padding(bottom = Size.Graph.PADDING_BOTTOM),
        factory = { context ->
            MpAndroidChart(
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
