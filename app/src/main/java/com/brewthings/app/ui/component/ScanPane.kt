package com.brewthings.app.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.juul.kable.Bluetooth
import com.juul.kable.Reason

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanPane(
    bluetooth: Bluetooth.Availability?,
    openAppDetails: () -> Unit,
    showLocationSettings: () -> Unit,
    enableBluetooth: () -> Unit,
    content: @Composable () -> Unit,
) {
    val permissionsState = rememberMultiplePermissionsState(Bluetooth.permissionsNeeded)

    var didAskForPermission by remember { mutableStateOf(false) }
    if (!didAskForPermission) {
        didAskForPermission = true
        SideEffect {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    if (permissionsState.allPermissionsGranted) {
        PermissionGranted(bluetooth, showLocationSettings, enableBluetooth, content)
    } else {
        if (permissionsState.shouldShowRationale) {
            BluetoothPermissionsNotGranted(permissionsState)
        } else {
            BluetoothPermissionsNotAvailable(openAppDetails)
        }
    }
}

@Composable
private fun PermissionGranted(
    bluetooth: Bluetooth.Availability?,
    showLocationSettings: () -> Unit,
    enableBluetooth: () -> Unit,
    content: @Composable () -> Unit,
) {
    when (bluetooth) {
        Bluetooth.Availability.Available -> {
            content()
        }

        is Bluetooth.Availability.Unavailable -> when (bluetooth.reason) {
            Reason.LocationServicesDisabled -> LocationServicesDisabled(showLocationSettings)
            Reason.Off,
            Reason.TurningOff,
            -> BluetoothDisabled(enableBluetooth)

            Reason.TurningOn -> BluetoothTemporarilyUnavailable()
            null -> BluetoothUnavailable()
        }

        null -> BluetoothTemporarilyUnavailable()
    }
}
