@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screen.scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.brewthings.app.R
import com.brewthings.app.data.domain.BluetoothScanState
import com.brewthings.app.data.domain.BrewMeasurements
import com.brewthings.app.data.domain.Device
import com.brewthings.app.data.domain.SensorMeasurements
import com.brewthings.app.ui.ActivityCallbacks
import com.brewthings.app.ui.component.BluetoothScanActionButton
import com.brewthings.app.ui.component.BluetoothScanRequirements
import com.brewthings.app.ui.component.ScannedDevicesDropdown
import com.brewthings.app.ui.component.SettingsDropdown
import com.brewthings.app.ui.component.SettingsItem
import com.brewthings.app.ui.navigation.legacy.Router
import com.brewthings.app.ui.screen.onboarding.OnboardingScreen
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
            val sensorMeasurements by viewModel.sensorMeasurements.collectAsState()
            val brewMeasurements by viewModel.brewMeasurements.collectAsState()
            val canSave by viewModel.canSave.collectAsState()
            ScanScreen(
                activityCallbacks = activityCallbacks,
                selectedDevice = lockedSelectedDevice,
                devices = lockedDevices,
                isBluetoothScanning = isBluetoothScanning,
                sensorMeasurements = sensorMeasurements,
                brewMeasurements = brewMeasurements,
                canSave = canSave,
                onSelectDevice = viewModel::selectDevice,
                onStartScan = viewModel::startScan,
                onStopScan = viewModel::stopScan,
                onSave = viewModel::save,
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
    sensorMeasurements: SensorMeasurements,
    brewMeasurements: BrewMeasurements?,
    canSave: Boolean,
    onSelectDevice: (Device) -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onSave: () -> Unit,
) {
    BluetoothScanRequirements(
        isScanning = isBluetoothScanning,
        onToggleScan = { if (isBluetoothScanning) onStopScan() else onStartScan() },
        activityCallbacks = activityCallbacks,
    ) { scanState, onScanClick ->
        var previousScanState by remember { mutableStateOf(BluetoothScanState.Unavailable) }

        LaunchedEffect(scanState) {
            if (scanState == BluetoothScanState.Idle && previousScanState == BluetoothScanState.Unavailable) {
                onStartScan()
            }

            if (scanState == BluetoothScanState.Unavailable && previousScanState == BluetoothScanState.InProgress) {
                onStopScan()
            }

            previousScanState = scanState
        }

        Scaffold(
            topBar = {
                TopAppBar(
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
            },
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues))
        }
    }
}
