package com.brewthings.app.ui.screens.graph

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.brewthings.app.R
import com.brewthings.app.ui.android.chart.ChartData
import com.brewthings.app.ui.android.chart.MpAndroidLineChart
import com.brewthings.app.ui.theme.Size
import com.github.mikephil.charting.data.LineData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Graph(
    modifier: Modifier = Modifier,
    state: GraphState,
    selectSeries: (DataType) -> Unit,
    onSelect: (Int?) -> Unit,
) {
    val density: Density = LocalDensity.current
    val textSize = MaterialTheme.typography.labelMedium.fontSize

    val isDarkTheme = isSystemInDarkTheme()
    val textColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    val chartData = state.graphData.toChartData(state.selectedDataType)

    Column(modifier = modifier.wrapContentHeight()) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            DataTypeSelector(
                dataTypes = state.dataTypes,
                selectedDataType = state.selectedDataType,
                selectSeries = selectSeries,
            )
        }

        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .height(Size.Graph.HEIGHT)
                .padding(bottom = Size.Graph.PADDING_BOTTOM),
            factory = { context ->
                MpAndroidLineChart(
                    context = context,
                    chartData = chartData,
                    selectedIndex = state.selectedDataIndex,
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
                    selectedIndex = state.selectedDataIndex,
                    isDarkTheme = isDarkTheme,
                    textColor = textColor,
                    primaryColor = primaryColor
                )
            }
        )
    }
}

@Composable
fun DataTypeSelector(
    dataTypes: List<DataType>,
    selectedDataType: DataType,
    selectSeries: (DataType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            modifier = Modifier.wrapContentSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
            onClick = { expanded = !expanded },
        ) {
            Text(text = selectedDataType.toLabel())

            Icon(
                modifier = Modifier.padding(start = 4.dp),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Each DropdownMenuItem represents an option in the dropdown
            dataTypes.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectSeries(option)
                        expanded = false
                    },
                    text = {
                        Text(text = option.toLabel())
                    }
                )
            }
        }
    }
}

@Composable
private fun DataType.toLabel(): String = when (this) {
    DataType.TEMPERATURE -> stringResource(id = R.string.graph_data_label_temp_full)
    DataType.GRAVITY -> stringResource(id = R.string.graph_data_label_gravity)
    DataType.BATTERY -> stringResource(id = R.string.graph_data_label_battery)
}

@Composable
private fun GraphData.toChartData(dataType: DataType): ChartData = ChartData(
    data = LineData(
        series.find { it.type == dataType }
            ?.data
            ?.toSegments()
            ?.toDataSets(dataType)
    )
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
