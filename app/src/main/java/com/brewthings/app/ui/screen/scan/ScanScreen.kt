package com.brewthings.app.ui.screen.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.brewthings.app.data.domain.BluetoothScanState
import com.brewthings.app.data.domain.BrewMeasurements
import com.brewthings.app.data.domain.Device
import com.brewthings.app.data.domain.SensorMeasurements
import com.brewthings.app.ui.ActivityCallbacks
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
            val bluetoothScanState by viewModel.bluetoothScanState.collectAsState()
            val sensorMeasurements by viewModel.sensorMeasurements.collectAsState()
            val brewMeasurements by viewModel.brewMeasurements.collectAsState()
            val canSave by viewModel.canSave.collectAsState()
            ScanScreen(
                selectedDevice = lockedSelectedDevice,
                devices = lockedDevices,
                bluetoothScanState = bluetoothScanState,
                sensorMeasurements = sensorMeasurements,
                brewMeasurements = brewMeasurements,
                canSave = canSave,
                onScanClick = viewModel::toggleScan,
                onSave = viewModel::save,
            )
        } else {
            ErrorNoPillSelectedScreen()
        }
    }
}

@Composable
fun ScanScreen(
    selectedDevice: Device,
    devices: List<Device>,
    bluetoothScanState: BluetoothScanState,
    sensorMeasurements: SensorMeasurements,
    brewMeasurements: BrewMeasurements?,
    canSave: Boolean,
    onScanClick: (BluetoothScanState) -> Unit,
    onSave: () -> Unit,
) {

}

@Composable
fun ErrorNoPillSelectedScreen() {

}
