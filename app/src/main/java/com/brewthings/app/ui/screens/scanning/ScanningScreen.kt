package com.brewthings.app.ui.screens.scanning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brewthings.app.R
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.ui.components.BatteryLevelIndicator
import com.brewthings.app.ui.components.ExpandableCard
import com.brewthings.app.ui.components.ScanPane
import com.brewthings.app.ui.components.TextWithIcon
import com.brewthings.app.ui.screens.navigation.Screen
import com.brewthings.app.ui.screens.navigation.SetupNavGraph
import com.brewthings.app.ui.theme.Typography
import java.time.Instant
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanningScreen(
    navController: NavController,
    viewModel: ScanningScreenViewModel = koinViewModel(),
    openAppDetails: () -> Unit,
    showLocationSettings: () -> Unit,
    enableBluetooth: () -> Unit,
) {
    ScanPane(
        bluetooth = viewModel.screenState.bluetooth,
        openAppDetails = openAppDetails,
        showLocationSettings = showLocationSettings,
        enableBluetooth = enableBluetooth,
    ) {
        ScanningScreen(
            state = viewModel.screenState,
            onRssiThresholdChanged = viewModel::onRssiThresholdChanged,
            toggleScan = viewModel::toggleScan,
            savePill = viewModel::savePill,
            navGraph = navController
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun ScanningScreen(
    state: ScanningScreenState,
    navGraph: NavController,
    onRssiThresholdChanged: (Int) -> Unit,
    toggleScan: () -> Unit,
    savePill: (ScannedRaptPill) -> Unit
) {
    val scannedPills = newOrCached(state.scannedPills, emptyList())
    val savedPills = newOrCached(state.savedPills, emptyList())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            // TODO(): Change this Text object to a clickable icon
            Text(
                modifier = Modifier.clickable {
                    navGraph.navigate(route = Screen.Scanning.route)
                },
                text = stringResource(R.string.scanning_options).uppercase(),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                style = Typography.bodyMedium
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.scanning_options).uppercase(),
                color = MaterialTheme.colorScheme.onBackground,
                style = Typography.bodyMedium
            )
            Card(
                border = BorderStroke(0.dp, Color.LightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExpandableCard(
                        topContent = { TopContent() },
                        expandedContent = {
                            RssiThreshold(
                                rssiThreshold = state.rssiThreshold,
                                onRssiThresholdChanged = onRssiThresholdChanged,
                            )
                        }
                    )
                    Text(
                        modifier = Modifier.clickable {
                            navGraph.navigate(route = Screen.Graph.route)
                        },
                        text = "Click here to go to details",
                        color = Color.DarkGray,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )

                    ScanningState(
                        scannedPillCount = state.scannedPillsCount,
                        filteredPillsCount = state.scannedPills.size,
                        scanning = state.scanning,
                        onScanButtonClicked = toggleScan,
                    )
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.scanning_results).uppercase(),
                color = MaterialTheme.colorScheme.onBackground,
                style = Typography.bodyMedium
            )
        }

        items(scannedPills, key = { "scanned_" + it.macAddress }) { pill ->
            ScannedPill(
                pill = pill,
                isExpanded = scannedPills.size == 1,
                isInScannedPills = state.scannedPills.contains(pill),
                savePill = savePill
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.scanning_saved).uppercase(),
                color = MaterialTheme.colorScheme.onBackground,
                style = Typography.bodyMedium
            )
        }

        items(savedPills, key = { "saved_" + it.macAddress }) { pill ->
            Pill(
                pill = pill,
                isExpanded = savedPills.size == 1,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    LaunchedEffect(key1 = state.bluetooth) {
        toggleScan()
    }
}

@Composable
private fun TopContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        Text(stringResource(R.string.scanning_advanced_options), style = Typography.bodyMedium)
    }
}

@Composable
private fun RssiThreshold(
    rssiThreshold: Int,
    onRssiThresholdChanged: (Int) -> Unit,
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = stringResource(R.string.scanning_rssi),
                style = Typography.bodyMedium,
            )
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = rssiThreshold.toString(),
                style = Typography.bodyMedium,
            )
        }
        @Suppress("UnnecessaryParentheses") Slider(
            value = (rssiThreshold * -1).toFloat(),
            onValueChange = { onRssiThresholdChanged(it.toInt() * -1) },
            valueRange = RSSI_THRESHOLD_RANGE_START..(RSSI_THRESHOLD_RANGE_END * -1),
        )
    }
}

@Composable
private fun ScanningState(
    scannedPillCount: Int,
    filteredPillsCount: Int,
    scanning: Boolean,
    onScanButtonClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 16.dp, bottom = 4.dp)
    ) {
        TextButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onScanButtonClicked,
        ) {
            Text(
                text = if (scanning) stringResource(R.string.scanning_stop) else stringResource(R.string.scanning_start),
                style = Typography.bodyMedium,
            )
        }
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (scanning) {
                Box(Modifier.padding(horizontal = 16.dp)) {
                    CircularProgressIndicator(Modifier.size(16.dp))
                }
            }
            Text(
                text = "$filteredPillsCount ($scannedPillCount)",
                style = Typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ScannedPill(
    pill: ScannedRaptPill,
    isExpanded: Boolean,
    isInScannedPills: Boolean,
    savePill: (ScannedRaptPill) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        ExpandableCard(
            isExpanded = isExpanded,
            topContent = { ScannedPillTopContent(pill, isInScannedPills, savePill) },
            expandedContent = { PillData(pill.data) }
        )
    }
}

@Composable
private fun ScannedPillTopContent(
    pill: ScannedRaptPill,
    isInScannedPills: Boolean,
    savePill: (ScannedRaptPill) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = pill.name ?: stringResource(R.string.scanning_result),
                overflow = TextOverflow.Ellipsis,
                style = Typography.bodyMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = pill.macAddress,
                style = Typography.bodySmall,
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (isInScannedPills) {
                Text(
                    text = stringResource(id = R.string.pill_rssi, pill.rssi),
                    style = Typography.bodySmall,
                )
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_bluetooth_disabled),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.padding(12.dp))

        IconButton(onClick = { savePill(pill) }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_save),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun Pill(
    pill: RaptPill,
    isExpanded: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        ExpandableCard(
            isExpanded = isExpanded,
            topContent = { PillTopContent(pill) },
            expandedContent = {
                val maxTimestamp = pill.data.maxOfOrNull { it.timestamp } ?: Instant.EPOCH
                pill.data.find { it.timestamp == maxTimestamp }?.let { data ->
                    PillData(data)
                }
            }
        )
    }
}

@Composable
private fun PillTopContent(pill: RaptPill) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = pill.name ?: stringResource(R.string.scanning_result),
                overflow = TextOverflow.Ellipsis,
                style = Typography.bodyMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = pill.macAddress,
                style = Typography.bodySmall,
            )
        }
    }
}

@Composable
private fun PillData(pillData: RaptPillData?) {
    newOrCached(pillData, null)?.let { data ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 62.dp),
        ) {
            Column {
                TextWithIcon(
                    iconResId = R.drawable.ic_gravity,
                    text = stringResource(id = R.string.pill_gravity, data.gravity)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextWithIcon(
                    iconResId = R.drawable.ic_temperature,
                    text = stringResource(id = R.string.pill_temperature, data.temperature)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column {
                TextWithIcon(
                    iconResId = R.drawable.ic_tilt,
                    text = stringResource(id = R.string.pill_tilt, data.floatingAngle)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                TextWithIcon(
                    icon = { BatteryLevelIndicator(data.battery) },
                    text = stringResource(id = R.string.pill_battery, data.battery)
                )
            }
        }
    }
}

@Composable
fun <T: Any?> newOrCached(
    data: T,
    initialValue: T
): T {
    var previousData: T by remember { mutableStateOf(initialValue) }
    return if (data != null) {
        previousData = data
        data
    } else {
        previousData
    }
}