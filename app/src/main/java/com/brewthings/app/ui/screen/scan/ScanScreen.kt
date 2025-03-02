@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screen.scan

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.brewthings.app.ui.component.EditNameBottomSheet
import com.brewthings.app.ui.component.PrimaryButton
import com.brewthings.app.ui.component.ScannedDevicesDropdown
import com.brewthings.app.ui.component.ScrollableColumnWithFooter
import com.brewthings.app.ui.component.SectionTitle
import com.brewthings.app.ui.component.SensorMeasurementsGrid
import com.brewthings.app.ui.component.SettingsDropdown
import com.brewthings.app.ui.component.SettingsItem
import com.brewthings.app.ui.component.TimeSinceLastUpdate
import com.brewthings.app.ui.component.TroubleshootingInfo
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

    val onAddDevice = {
        // TODO(walt): routing
    }

    if (lockedDevices.isEmpty()) {
        OnboardingScreen() // TODO(walt): routing
    } else {
        val selectedDevice by viewModel.selectedDevice.collectAsState()
        val lockedSelectedDevice = selectedDevice
        if (lockedSelectedDevice != null) {
            val isBluetoothScanning by viewModel.isBluetoothScanning.collectAsState()
            val lastUpdate by viewModel.lastUpdate.collectAsState()
            val hasData by viewModel.hasData.collectAsState()
            val sensorMeasurements by viewModel.sensorMeasurements.collectAsState()
            val brewWithMeasurements by viewModel.brewWithMeasurements.collectAsState()
            val canSave by viewModel.canSave.collectAsState()
            val now by viewModel.now.collectAsState()
            ScanScreen(
                now = now,
                activityCallbacks = activityCallbacks,
                selectedDevice = lockedSelectedDevice,
                devices = lockedDevices,
                isBluetoothScanning = isBluetoothScanning,
                hasData = hasData,
                lastUpdate = lastUpdate,
                sensorMeasurements = sensorMeasurements,
                brewWithMeasurements = brewWithMeasurements,
                canSave = canSave,
                onSelectDevice = viewModel::selectDevice,
                onAddDevice = onAddDevice,
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
                },
                onRenameDevice = viewModel::renameDevice,
            )
        } else {
            NoDeviceSelected(
                devices = devices,
                onSelectDevice = viewModel::selectDevice,
                onAddDevice = onAddDevice,
            )
        }
    }
}

@Composable
private fun ScanScreen(
    now: Instant,
    activityCallbacks: ActivityCallbacks,
    selectedDevice: Device,
    devices: List<Device>,
    isBluetoothScanning: Boolean,
    hasData: Boolean,
    lastUpdate: Instant?,
    sensorMeasurements: SensorMeasurements,
    brewWithMeasurements: BrewWithMeasurements?,
    canSave: Boolean,
    onSelectDevice: (Device) -> Unit,
    onAddDevice: () -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onSave: (Boolean) -> Unit,
    onViewAllData: (Device) -> Unit,
    onViewBrewData: (Brew) -> Unit,
    onRenameDevice: (String) -> Unit,
) {
    var previousScanState by remember { mutableStateOf(BluetoothScanState.Unavailable) }
    var showStartBrewDialog by remember { mutableStateOf(false) }
    var showRenameBottomSheet by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val horizontalPadding = 16.dp

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
                    onAddDevice = onAddDevice,
                    onScanClick = onScanClick,
                    onRenameDeviceClick = {
                        showRenameBottomSheet = true
                    },
                )
            },
        ) { paddingValues ->
            if (hasData) {
                ScrollableColumnWithFooter(
                    modifier = Modifier.padding(paddingValues),
                    scrollableContent = {
                        if (lastUpdate != null) {
                            TimeSinceLastUpdate(now = now, lastUpdate = lastUpdate)
                        }

                        if (sensorMeasurements.isNotEmpty()) {
                            SectionTitle(
                                modifier = Modifier.padding(top = 8.dp),
                                title = stringResource(R.string.scan_section_title_last_measurements),
                                action = stringResource(R.string.scan_section_action_current_device_graph),
                                onActionClick = {
                                    onViewAllData(selectedDevice)
                                },
                            )

                            SensorMeasurementsGrid(
                                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 4.dp),
                                measurements = sensorMeasurements,
                            )
                        }

                        if (brewWithMeasurements != null) {
                            SectionTitle(
                                modifier = Modifier.padding(top = 8.dp),
                                title = stringResource(R.string.scan_section_title_current_brew),
                                action = stringResource(R.string.scan_section_action_current_brew_graph),
                                onActionClick = {
                                    onViewBrewData(brewWithMeasurements.brew)
                                },
                            )

                            BrewMeasurementsGrid(
                                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 4.dp),
                                data = brewWithMeasurements.measurements,
                            )
                        }
                    },
                    footer = {
                        PrimaryButton(
                            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 16.dp),
                            isEnabled = canSave,
                            text = stringResource(R.string.button_save),
                            onClick = {
                                if (brewWithMeasurements == null) {
                                    showStartBrewDialog = true
                                } else {
                                    onSave(false)
                                }
                            },
                        )
                    }
                )
            } else {
                TroubleshootingInfo(
                    modifier = Modifier.padding(paddingValues),
                    iconResId = R.drawable.ic_empty_glass,
                    title = stringResource(R.string.scan_troubleshooting_no_active_brew_title),
                    description = stringResource(R.string.scan_troubleshooting_no_active_brew_desc),
                    buttonText = stringResource(R.string.button_view_previous_data),
                    onButtonClick = { onViewAllData(selectedDevice) },
                )
            }

            previousScanState = scanState
        }

        if (showRenameBottomSheet) {
            EditNameBottomSheet(
                device = selectedDevice,
                onDismiss = { showRenameBottomSheet = false },
                onDeviceNameUpdate = onRenameDevice,
            )
        }

        if (showStartBrewDialog) {
            StartBrewDialog { isConfirmed ->
                showStartBrewDialog = false
                onSave(isConfirmed)
            }
        }
    }
}

@Composable
private fun NoDeviceSelected(
    devices: List<Device>,
    onSelectDevice: (Device) -> Unit,
    onAddDevice: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ScannedDevicesDropdown(
                        selectedDevice = null,
                        devices = devices,
                        onSelect = onSelectDevice,
                        onAddDevice = onAddDevice,
                    )
                }
            )
        },
        content = { paddingValues ->
            TroubleshootingInfo(
                modifier = Modifier.padding(paddingValues),
                iconResId = R.drawable.ic_device_unknown,
                title = stringResource(R.string.scan_troubleshooting_no_active_device_title),
                description = stringResource(R.string.scan_troubleshooting_no_active_device_desc),
            )
        }
    )
}

@Composable
private fun ScanTopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    selectedDevice: Device,
    scanState: BluetoothScanState,
    devices: List<Device>,
    onSelectDevice: (Device) -> Unit,
    onAddDevice: () -> Unit,
    onScanClick: () -> Unit,
    onRenameDeviceClick: () -> Unit,
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            ScannedDevicesDropdown(
                selectedDevice = selectedDevice,
                devices = devices,
                onSelect = onSelectDevice,
                onAddDevice = onAddDevice,
            )
        },
        actions = {
            BluetoothScanActionButton(scanState, onScanClick)
            SettingsDropdown(
                listOf(
                    SettingsItem(stringResource(R.string.settings_change_rssi)) {
                        // TODO(walt)
                    },
                    SettingsItem(stringResource(R.string.settings_rename_device), onRenameDeviceClick),
                )
            )
        }
    )
}

@Composable
private fun AutoScanBehavior(
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

@Composable
private fun StartBrewDialog(callback: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { callback(false) },
        text = { Text(text = stringResource(R.string.start_new_brew_dialog_text)) },
        confirmButton = {
            TextButton(onClick = { callback(true) }) {
                Text(stringResource(R.string.button_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = { callback(false) }) {
                Text(stringResource(R.string.button_no))
            }
        }
    )
}
