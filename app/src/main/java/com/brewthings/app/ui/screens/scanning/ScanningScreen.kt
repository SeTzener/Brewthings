package com.brewthings.app.ui.screens.scanning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.ble.RaptPill
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
            navigateToInstrument = {
                viewModel.stopScan()
                // TODO: navigate
            },
            onRssiThresholdChanged = viewModel::onRssiThresholdChanged,
            onScanButtonClicked = viewModel::onScanButtonClicked,
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun ScanningScreen(
    state: ScanningScreenState,
    navigateToInstrument: (RaptPill) -> Unit,
    onRssiThresholdChanged: (Int) -> Unit,
    onScanButtonClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(modifier = Modifier.padding(start = 24.dp), text = "OPTIONS", style = Typography.bodyMedium)
        Card(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            border = BorderStroke(0.dp, Color.LightGray),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                ExpandableCard(
                    topContent = { TopContent() },
                    expandedContent = {
                        RssiThreshold(
                            rssiThreshold = state.rssiThreshold,
                            onRssiThresholdChanged = onRssiThresholdChanged,
                        )
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

                ScanningState(
                    scannedInstrumentCount = state.scannedInstrumentCount,
                    filteredInstrumentsCount = state.scannedInstruments.size,
                    scanning = state.scanning,
                    onScanButtonClicked = onScanButtonClicked,
                )
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        Text(modifier = Modifier.padding(start = 24.dp), text = "RESULTS", style = Typography.bodyMedium)
        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        ScannedInstruments(
            instruments = state.scannedInstruments,
            navigateToInstrument = navigateToInstrument,
        )

        Spacer(modifier = Modifier.weight(1f))

        val context = LocalContext.current
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        Text(
            text = "Version: ${packageInfo?.versionName} (${packageInfo?.longVersionCode})",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TopContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text("Advanced Options", style = Typography.bodyMedium)
    }
}

@Composable
private fun RssiThreshold(
    rssiThreshold: Int,
    onRssiThresholdChanged: (Int) -> Unit,
) {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "RSSI",
            )
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = rssiThreshold.toString(),
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
                .padding(all = 8.dp)
        ) {
            TextButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = onScanButtonClicked,
            ) {
                Text(
                    text = if (scanning) "Stop Scanning" else "Start Scanning",
                    style = Typography.bodyLarge,
                )
            }
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (scanning) {
                    Box(Modifier.padding(horizontal = 16.dp)) {
                        CircularProgressIndicator(Modifier.size(24.dp))
                    }
                }
                Text("$filteredInstrumentsCount ($scannedInstrumentCount)")
            }
        }
}

@Composable
private fun ScannedInstruments(
    modifier: Modifier = Modifier,
    instruments: List<RaptPill>,
    navigateToInstrument: (RaptPill) -> Unit,
) {
    Column {
        if (instruments.isNotEmpty()) {
            Card(
                modifier = modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                border = BorderStroke(0.dp, Color.LightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    instruments.forEach { instrument ->
                        Instrument(
                            instrument = instrument,
                            navigateToInstrument = navigateToInstrument,
                        )
                        if (instruments.last() != instrument) {
                            HorizontalDivider(
                                modifier = Modifier.background(
                                    MaterialTheme.colorScheme.secondary.copy(
                                        alpha = 0.4f
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Instrument(
    instrument: RaptPill,
    navigateToInstrument: (RaptPill) -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { navigateToInstrument(instrument) })
            .fillMaxWidth()
            .heightIn(52.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = instrument.name,
                overflow = TextOverflow.Ellipsis,
                style = Typography.bodyMedium,
                maxLines = 1
            )
            Text(
                text = instrument.macAddress,
                style = Typography.bodySmall,
            )
        }
        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.padding(end = 8.dp)) {
                    Text(text = "${instrument.rssi}")
                }
                Image(imageVector = ImageVector.vectorResource(R.drawable.ic_chevron_right), contentDescription = null)
            }
        }
    }
}
