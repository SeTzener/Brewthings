@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screen.scan

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.domain.BluetoothScanState
import com.brewthings.app.data.domain.BrewWithMeasurements
import com.brewthings.app.data.domain.Device
import com.brewthings.app.data.domain.SensorMeasurements
import com.brewthings.app.data.model.Brew
import com.brewthings.app.ui.ActivityCallbacks
import com.brewthings.app.ui.component.BluetoothScanActionButton
import com.brewthings.app.ui.component.BluetoothScanRequirements
import com.brewthings.app.ui.component.BrewMeasurementsGrid
import com.brewthings.app.ui.component.PrimaryButton
import com.brewthings.app.ui.component.ScannedDevicesDropdown
import com.brewthings.app.ui.component.ScrollableColumnWithFooter
import com.brewthings.app.ui.component.SectionTitle
import com.brewthings.app.ui.component.SensorMeasurementsGrid
import com.brewthings.app.ui.component.SettingsDropdown
import com.brewthings.app.ui.component.SettingsItem
import com.brewthings.app.ui.component.TimeSinceLastUpdate
import com.brewthings.app.ui.navigation.legacy.Router
import com.brewthings.app.ui.screen.onboarding.OnboardingScreen
import kotlinx.datetime.Instant
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanScreen(
    router: Router,
    activityCallbacks: ActivityCallbacks,
    viewModel: ScanViewModel = koinViewModel(),
) {
    val devices by viewModel.devices.collectAsState()
    val lockedDevices = devices
    if (lockedDevices.isEmpty()) {
        OnboardingScreen() // TODO(walt): routing
    } else {
        val selectedDevice by viewModel.selectedDevice.collectAsState()
        val lockedSelectedDevice = selectedDevice
        if (lockedSelectedDevice != null) {
            val isBluetoothScanning by viewModel.isBluetoothScanning.collectAsState()
            val lastUpdate by viewModel.lastUpdate.collectAsState()
            val sensorMeasurements by viewModel.sensorMeasurements.collectAsState()
            val brewWithMeasurements by viewModel.brewWithMeasurements.collectAsState()
            val canSave by viewModel.canSave.collectAsState()
            ScanScreen(
                activityCallbacks = activityCallbacks,
                selectedDevice = lockedSelectedDevice,
                devices = lockedDevices,
                isBluetoothScanning = isBluetoothScanning,
                lastUpdate = lastUpdate,
                sensorMeasurements = sensorMeasurements,
                brewWithMeasurements = brewWithMeasurements,
                canSave = canSave,
                onSelectDevice = viewModel::selectDevice,
                onStartScan = viewModel::startScan,
                onStopScan = viewModel::stopScan,
                onSave = viewModel::save,
                onViewAllData = { device ->
                    router.goToPillGraph(
                        name = device.displayName,
                        macAddress = device.macAddress
                    )
                },
                onViewBrewData = { brew ->
                    router.goToBrewGraph(brew)
                }
            )
        }
    }
}

@Composable
fun ScanScreen(
    activityCallbacks: ActivityCallbacks,
    selectedDevice: Device,
    devices: List<Device>,
    isBluetoothScanning: Boolean,
    lastUpdate: Instant?,
    sensorMeasurements: SensorMeasurements,
    brewWithMeasurements: BrewWithMeasurements?,
    canSave: Boolean,
    onSelectDevice: (Device) -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onSave: () -> Unit,
    onViewAllData: (Device) -> Unit,
    onViewBrewData: (Brew) -> Unit,
) {
    var previousScanState by remember { mutableStateOf(BluetoothScanState.Unavailable) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BluetoothScanRequirements(
        isScanning = isBluetoothScanning,
        onToggleScan = { if (isBluetoothScanning) onStopScan() else onStartScan() },
        activityCallbacks = activityCallbacks,
    ) { scanState, onScanClick ->
        AutoScanBehavior(
            previousScanState = previousScanState,
            scanState = scanState,
            startScan = onStartScan,
            stopScan = onStopScan,
        )

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                ScanTopBar(
                    scrollBehavior = scrollBehavior,
                    selectedDevice = selectedDevice,
                    scanState = scanState,
                    devices = devices,
                    onSelectDevice = onSelectDevice,
                    onScanClick = onScanClick
                )
            },
        ) { paddingValues ->
            ScrollableColumnWithFooter(
                modifier = Modifier.padding(paddingValues),
                scrollableContent = {
                    if (lastUpdate != null) {
                        TimeSinceLastUpdate(lastUpdate = lastUpdate)
                    }

                    if (sensorMeasurements.isNotEmpty()) {
                        SectionTitle(
                            title = stringResource(R.string.scan_section_title_last_measurements),
                            action = stringResource(R.string.scan_section_action_current_device_graph),
                            onActionClick = {
                                onViewAllData(selectedDevice)
                            },
                        )

                        SensorMeasurementsGrid(
                            modifier = Modifier.padding(16.dp),
                            measurements = sensorMeasurements,
                        )
                    }

                    if (brewWithMeasurements != null) {
                        SectionTitle(
                            title = stringResource(R.string.scan_section_title_current_brew),
                            action = stringResource(R.string.scan_section_action_current_brew_graph),
                            onActionClick = {
                                onViewBrewData(brewWithMeasurements.brew)
                            },
                        )

                        BrewMeasurementsGrid(
                            modifier = Modifier.padding(16.dp),
                            data = brewWithMeasurements.measurements,
                        )
                    }
                },
                footer = {
                    PrimaryButton(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
                        isEnabled = canSave,
                        text = stringResource(R.string.button_save),
                        onClick = onSave,
                    )
                }
            )

            previousScanState = scanState
        }
    }
}

@Composable
fun ScanTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    selectedDevice: Device,
    scanState: BluetoothScanState,
    devices: List<Device>,
    onSelectDevice: (Device) -> Unit,
    onScanClick: () -> Unit,
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            ScannedDevicesDropdown(
                selectedDevice = selectedDevice,
                devices = devices,
                onSelect = onSelectDevice,
                onAddDevice = {
                    // TODO(walt)
                },
            )
        },
        actions = {
            BluetoothScanActionButton(scanState, onScanClick)
            SettingsDropdown(
                listOf(
                    SettingsItem(stringResource(R.string.settings_change_rssi)) {
                        // TODO(walt)
                    },
                    SettingsItem(stringResource(R.string.settings_rename_device)) {
                        // TODO(walt)
                    },
                )
            )
        }
    )
}

@Composable
fun AutoScanBehavior(
    previousScanState: BluetoothScanState,
    scanState: BluetoothScanState,
    startScan: () -> Unit,
    stopScan: () -> Unit,
) {
    LaunchedEffect(scanState) {
        if (scanState == BluetoothScanState.Idle && previousScanState == BluetoothScanState.Unavailable) {
            startScan()
        }

        if (scanState == BluetoothScanState.Unavailable && previousScanState == BluetoothScanState.InProgress) {
            stopScan()
        }
    }
}
