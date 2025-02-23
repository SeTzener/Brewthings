package com.brewthings.app.data.domain

import com.juul.kable.Bluetooth
import com.juul.kable.Reason

sealed interface BluetoothScanState {
    data object Idle : BluetoothScanState
    data object InProgress : BluetoothScanState
    data class Unavailable(val reason: Reason) : BluetoothScanState {
        enum class Reason {
            Off,
            TurningOff,
            TurningOn,

            /** Only on Android 11 (API 30) and lower. */
            LocationServicesDisabled,

            /** No status reported yet. **/
            Unknown,
        }
    }
}

fun Bluetooth.Availability.toBluetoothScanState(isScanning: Boolean) = when (this) {
    Bluetooth.Availability.Available ->
        if (isScanning)
            BluetoothScanState.InProgress
        else
            BluetoothScanState.Idle

    is Bluetooth.Availability.Unavailable -> BluetoothScanState.Unavailable(
        reason = when (reason) {
            Reason.Off -> BluetoothScanState.Unavailable.Reason.Off
            Reason.TurningOff -> BluetoothScanState.Unavailable.Reason.TurningOff
            Reason.TurningOn -> BluetoothScanState.Unavailable.Reason.TurningOn
            Reason.LocationServicesDisabled -> BluetoothScanState.Unavailable.Reason.LocationServicesDisabled
            null -> BluetoothScanState.Unavailable.Reason.Unknown
        }
    )
}
