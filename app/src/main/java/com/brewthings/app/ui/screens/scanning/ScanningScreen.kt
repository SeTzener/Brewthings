package com.brewthings.app.ui.screens.scanning

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.ui.components.BatteryLevelIndicator
import com.brewthings.app.ui.components.ExpandableCard
import com.brewthings.app.ui.components.ScanPane
import com.brewthings.app.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanningScreen(
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
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun ScanningScreen(
    state: ScanningScreenState,
    onRssiThresholdChanged: (Int) -> Unit,
    toggleScan: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
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

                    ScanningState(
                        scannedInstrumentCount = state.scannedInstrumentCount,
                        filteredInstrumentsCount = state.scannedInstruments.size,
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

        items(state.scannedInstruments, key = { it.macAddress }) { instrument ->
            Instrument(
                instrument = instrument,
                isExpanded = state.scannedInstruments.size == 1
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
    scannedInstrumentCount: Int,
    filteredInstrumentsCount: Int,
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
                text = "$filteredInstrumentsCount ($scannedInstrumentCount)",
                style = Typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun Instrument(
    instrument: RaptPill,
    isExpanded: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        ExpandableCard(
            isExpanded = isExpanded,
            topContent = { InstrumentTopContent(instrument) },
            expandedContent = { InstrumentExpandedContent(instrument) }
        )
    }
}

@Composable
private fun InstrumentTopContent(
    instrument: RaptPill,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = instrument.name ?: stringResource(R.string.scanning_result),
                overflow = TextOverflow.Ellipsis,
                style = Typography.bodyMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = instrument.macAddress,
                style = Typography.bodySmall,
            )
        }

        Text(
            text = stringResource(id = R.string.instrument_rssi, instrument.rssi),
            style = Typography.bodySmall,
        )
    }
}

@Composable
private fun InstrumentExpandedContent(
    instrument: RaptPill,
) {
    instrument.data?.let { data ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 62.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                        .align(Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_gravity),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = stringResource(id = R.string.instrument_gravity, data.gravity),
                        style = Typography.bodyMedium,
                    )
                }
                Row(
                    modifier = Modifier.weight(1f)
                        .align(Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_temperature),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = stringResource(id = R.string.instrument_temperature, data.temperature),
                        style = Typography.bodyMedium,
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                        .align(Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.Start
                ) {
                    BatteryLevelIndicator(batteryPercentage = data.battery)

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = stringResource(id = R.string.instrument_battery, data.battery * 100f),
                        style = Typography.bodyMedium,
                    )
                }
                Row(
                    modifier = Modifier.weight(1f)
                        .align(Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_tilt),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = stringResource(id = R.string.instrument_tilt, data.floatingAngle),
                        style = Typography.bodyMedium,
                    )
                }
            }
        }
    }
}
