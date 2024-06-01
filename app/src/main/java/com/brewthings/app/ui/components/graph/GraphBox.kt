@file:Suppress("MagicNumber")

package com.brewthings.app.ui.components.graph

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.airthings.app.ui.res.AirthingsColors
import com.airthings.app.ui.res.AirthingsTheme
import com.airthings.app.ui.res.Size
import com.airthings.app.ui.res.asString
import com.airthings.app.ui.theme.LocalTheme
import com.airthings.app.ui.theme.Theme
import com.airthings.app.ui.util.preview.noOpFunction
import com.airthings.shared.domain.model.SensorType
import com.airthings.shared.ui.screen.deviceSensor.GraphOverlayState
import com.brewthings.app.R
import com.brewthings.app.data.model.graph.GraphBoxState
import com.brewthings.app.data.model.graph.GraphSelection
import com.brewthings.app.data.model.graph.GraphEvent
import com.brewthings.app.data.model.graph.GraphOverlayState
import com.brewthings.app.ui.components.graph.GraphConstants.Size
import java.time.Instant
import kotlinx.coroutines.flow.Flow

@Composable
fun GraphBox(
    lineColor: Color,
    state: GraphBoxState,
    events: Flow<GraphEvent>,
    onShowAvailableDataClicked: () -> Unit,
    onMarkerClick: () -> Unit,
    onVisibleRangeChanged: (ClosedRange<Instant>) -> Unit,
    onSelectedValueChanged: (GraphSelection?) -> Unit,
) {
    Box {
        SensorValuesGraph(
            onVisibleRangeChanged = onVisibleRangeChanged,
            onSelectedValueChanged = onSelectedValueChanged,
            graphState = state.graphState,
            graphTimeSpan = state.graphTimeSpan,
            events = events,
        )
        state.selectedValue?.also {
            GraphMarker(
                screenState = state,
                selectedValue = it,
                lineColor = lineColor,
                onClick = onMarkerClick
            )
        }
        when (val overlayState = state.graphOverlayState) {
            is GraphOverlayState.Error -> NoData(isError = true)
            is GraphOverlayState.Loading -> LoadingData()
            is GraphOverlayState.EmptySegment -> NoData()
            is GraphOverlayState.NoVisibleData -> MissingData(
                state = overlayState,
                onShowAvailableDataClicked = onShowAvailableDataClicked,
            )
            is GraphOverlayState.SensorCalibrating,
            null -> {
                // Do nothing.
            }
        }
    }
}

@Composable
private fun GraphMarker(
    screenState: GraphBoxState,
    lineColor: Color,
    selectedValue: GraphSelection,
    onClick: () -> Unit
) {
    val radius = Size.Marker.CIRCLE_RADIUS
    val indicatorSize = Size.Marker.INDICATOR_SIZE
    val (xDp, yDp) = with(LocalDensity.current) {
        selectedValue.xPos.toDp() to selectedValue.yPos.toDp()
    }

    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(key1 = selectedValue.sensorValue) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        GraphIndicator(isRotated = true, x = xDp - indicatorSize / 2, y = Size.Marker.INDICATOR_PADDING_TOP)
        GraphIndicator(x = xDp - indicatorSize / 2, y = maxHeight - Size.Marker.INDICATOR_PADDING_BOTTOM)
        Box(
            modifier = Modifier
                .size(radius)
                .absoluteOffset(x = xDp - radius / 2, y = yDp - radius / 2)
                .shadow(elevation = Size.Marker.CIRCLE_ELEVATION, shape = CircleShape, clip = true)
                .clip(CircleShape)
                .background(lineColor)
                .border(
                    border = BorderStroke(Size.Marker.CIRCLE_BORDER_WIDTH, Color.White),
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
        )
    }
}

@Composable
private fun GraphIndicator(
    isRotated: Boolean = false,
    size: Dp = Size.Marker.INDICATOR_SIZE,
    x: Dp,
    y: Dp,
) {
    Image(
        modifier = Modifier
            .size(size)
            .absoluteOffset(x, y)
            .rotate(if (isRotated) 180f else 0f),
        imageVector = ImageVector.vectorResource(R.drawable.graph_indicator),
        contentDescription = null,
        colorFilter = ColorFilter.tint(
            when (isSystemInDarkTheme()) {
                false -> GraphConstants.Color.indicatorLight
                true -> GraphConstants.Color.indicatorDark
            }
        )
    )
}

@Composable
private fun BoxScope.LoadingData() {
    Column(modifier = Modifier.align(Alignment.Center)) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = Size.DOUBLE_SPACING)
                .size(Size.ICON_ON_GRAPH_SIZE),
        )
        GraphOverlayText(text = stringResource(id = R.string.graph_fetching_data))
    }
}

@Composable
private fun BoxScope.NoData(isError: Boolean = false) {
    Column(modifier = Modifier.align(Alignment.Center)) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = Size.DOUBLE_SPACING)
                .size(Size.ICON_ON_GRAPH_SIZE),
            imageVector = ImageVector.vectorResource(id = R.drawable.outline_cloud_off_24),
            colorFilter = ColorFilter.tint(AirthingsColors.BLACK),
            contentDescription = null,
        )
        GraphOverlayText(
            text = if (isError) {
                R.string.deviceSensorFailedToLoadDataError
            } else {
                R.string.deviceSensorNoRecentData
            }.asString(),
        )
    }
}

@Composable
private fun BoxScope.MissingData(
    state: GraphOverlayState.NoVisibleData,
    onShowAvailableDataClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.align(Alignment.Center),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(shape = RoundedCornerShape(Size.BUTTON_CORNER_RADIUS)),
        ) {
            GraphOverlayText(
                modifier = Modifier
                    .background(AirthingsColors.BACKGROUND)
                    .padding(
                        horizontal = Size.DOUBLE_SPACING * 2,
                        vertical = Size.SPACING,
                    ),
                text = if (state.canShowAvailableData) {
                    R.string.deviceSensorNoRecentData
                } else {
                    R.string.noDataAvailable
                }.asString(),
                color = AirthingsColors.SHARK,
            )
        }
        if (state.canShowAvailableData) {
            Box(modifier = Modifier.height(Size.TRIPLE_SPACING))
            ShowAvailableDataButton(onClick = onShowAvailableDataClicked)
        }
    }
}

@Composable
private fun ColumnScope.ShowAvailableDataButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.align(Alignment.CenterHorizontally),
        shape = RoundedCornerShape(Size.BUTTON_CORNER_RADIUS),
        colors = ButtonDefaults.buttonColors(
            containerColor = AirthingsColors.SUNLIGHT,
            contentColor = AirthingsColors.SHARK,
        )
    ) {
        GraphOverlayText(
            modifier = Modifier.padding(horizontal = Size.SPACING),
            text = R.string.showAvailableData.asString(),
            color = AirthingsColors.SHARK,
        )
    }
}

@Composable
private fun GraphOverlayText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
    )
}

@Composable
@Preview(showBackground = true)
private fun LoadingDataPreview() {
    AirthingsTheme {
        Box {
            LoadingData()
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NoDataPreview() {
    AirthingsTheme {
        Box {
            NoData(isError = false)
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NoDataErrorPreview() {
    AirthingsTheme {
        Box {
            NoData(isError = true)
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MissingDataPreview() {
    AirthingsTheme {
        Box {
            MissingData(
                onShowAvailableDataClicked = noOpFunction(),
                state = GraphOverlayState.NoVisibleData(
                    canShowAvailableData = false
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MissingDataButtonPreview() {
    AirthingsTheme {
        Box {
            MissingData(
                onShowAvailableDataClicked = noOpFunction(),
                state = GraphOverlayState.NoVisibleData(
                    canShowAvailableData = true
                )
            )
        }
    }
}
