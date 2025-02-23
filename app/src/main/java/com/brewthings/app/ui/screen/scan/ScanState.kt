package com.brewthings.app.ui.screen.scan

import com.brewthings.app.data.domain.BluetoothScanState
import com.brewthings.app.data.domain.Device
import com.brewthings.app.data.domain.Measurement
import com.brewthings.app.util.datetime.TimeRange

/**
 * Screen state for ScanScreen.
 */
sealed interface ScanState {
    data object Loading : ScanState

    data object NoDevices : ScanState

    data class NoDeviceSelected(
        val devices: List<Device>,
    ) : ScanState

    data class DeviceState(
        val selectedDevice: Device,
        val devices: List<Device>,
        val bluetoothScanState: BluetoothScanState,
        val sensorMeasurements: List<Measurement>,
        val currentBrewState: BrewState?,
        val canSave: Boolean,
    ) : ScanState
}

data class BrewState(
    val timeRange: TimeRange,
    val measurements: List<Measurement>,
)
