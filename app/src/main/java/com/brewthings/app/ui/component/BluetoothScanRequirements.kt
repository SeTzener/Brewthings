@file:OptIn(ExperimentalPermissionsApi::class)

package com.brewthings.app.ui.component

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewthings.app.R
import com.brewthings.app.data.domain.BluetoothScanState
import com.brewthings.app.ui.ActivityCallbacks
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.juul.kable.Bluetooth
import com.juul.kable.Reason

private val permissionsNeeded: List<String> = when {
    // If your app targets Android 9 (API level 28) or lower, you can declare the ACCESS_COARSE_LOCATION permission
    // instead of the ACCESS_FINE_LOCATION permission.
    // https://developer.android.com/guide/topics/connectivity/bluetooth/permissions#declare-android11-or-lower
    SDK_INT <= VERSION_CODES.P -> listOf(ACCESS_COARSE_LOCATION)

    // ACCESS_FINE_LOCATION is necessary because, on Android 11 (API level 30) and lower, a Bluetooth scan could
    // potentially be used to gather information about the location of the user.
    // https://developer.android.com/guide/topics/connectivity/bluetooth/permissions#declare-android11-or-lower
    SDK_INT <= VERSION_CODES.R -> listOf(ACCESS_FINE_LOCATION)

    // If your app targets Android 12 (API level 31) or higher, declare the following permissions in your app's
    // manifest file:
    //
    // 1. If your app looks for Bluetooth devices, such as BLE peripherals, declare the `BLUETOOTH_SCAN` permission.
    // 2. If your app makes the current device discoverable to other Bluetooth devices, declare the
    //    `BLUETOOTH_ADVERTISE` permission.
    // 3. If your app communicates with already-paired Bluetooth devices, declare the BLUETOOTH_CONNECT permission.
    // https://developer.android.com/guide/topics/connectivity/bluetooth/permissions#declare-android12-or-higher
    else /* SDK_INT >= S */ -> listOf(BLUETOOTH_SCAN, BLUETOOTH_CONNECT)
}

@Composable
fun BluetoothScanRequirements(
    isScanning: Boolean,
    onToggleScan: () -> Unit,
    activityCallbacks: ActivityCallbacks,
    content: @Composable (scanState: BluetoothScanState, onScanClick: () -> Unit) -> Unit
) {
    val permissionsState = rememberMultiplePermissionsState(permissionsNeeded)
    val bluetoothAvailability by Bluetooth.availability.collectAsStateWithLifecycle(initialValue = null)

    var showDialog by remember { mutableStateOf(false) }
    var dialogContent by remember { mutableStateOf<@Composable (() -> Unit)?>(null) }

    if (permissionsState.allPermissionsGranted) {
        when (bluetoothAvailability) {
            Bluetooth.Availability.Available -> {
                val scanState = if (isScanning) BluetoothScanState.InProgress else BluetoothScanState.Idle
                content(scanState, onToggleScan)
            }

            is Bluetooth.Availability.Unavailable, null -> {
                content(BluetoothScanState.Unavailable) {
                    showDialog = true
                    dialogContent = {
                        val unavailableOrNull = bluetoothAvailability as? Bluetooth.Availability.Unavailable
                        when (unavailableOrNull?.reason) {
                            Reason.LocationServicesDisabled ->
                                LocationServicesDisabled {
                                    showDialog = false
                                    activityCallbacks.showLocationSettings()
                                }

                            Reason.Off, Reason.TurningOff ->
                                BluetoothDisabled {
                                    showDialog = false
                                    activityCallbacks.enableBluetooth()
                                }

                            Reason.TurningOn ->
                                BluetoothTemporarilyUnavailable()

                            null ->
                                BluetoothUnavailable()
                        }
                    }
                }
            }
        }
    } else {
        content(BluetoothScanState.Unavailable) {
            showDialog = true
            dialogContent = {
                if (permissionsState.shouldShowRationale) {
                    BluetoothPermissionsNotAvailable {
                        showDialog = false
                        activityCallbacks.openAppDetails()
                    }
                } else {
                    BluetoothPermissionsNotGranted {
                        showDialog = false
                        permissionsState.launchMultiplePermissionRequest()
                    }
                }
            }
        }
    }

    val lockedDialogContent = dialogContent
    if (showDialog && lockedDialogContent != null) {
        BluetoothDialog(
            onDismissRequest = { showDialog = false },
            content = lockedDialogContent,
        )
    }
}

@Composable
fun BluetoothDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                content()
            }
        }
    }
}

@Composable
fun BluetoothUnavailable() {
    ActionRequired(
        icon = ImageVector.vectorResource(R.drawable.ic_bluetooth_disabled),
        title = stringResource(id = R.string.bluetooth_unavailable_title),
        description = stringResource(id = R.string.bluetooth_unavailable_desc),
    )
}

@Composable
fun BluetoothDisabled(enableAction: () -> Unit) {
    ActionRequired(
        icon = ImageVector.vectorResource(R.drawable.ic_bluetooth_disabled),
        title = stringResource(id = R.string.bluetooth_disabled_title),
        description = stringResource(id = R.string.bluetooth_disabled_desc),
        buttonText = stringResource(id = R.string.bluetooth_disabled_btn),
        onClick = enableAction,
    )
}

@Composable
fun LocationServicesDisabled(enableAction: () -> Unit) {
    ActionRequired(
        icon = ImageVector.vectorResource(R.drawable.ic_location_disabled),
        title = stringResource(id = R.string.location_services_disabled_title),
        description = stringResource(id = R.string.location_services_disabled_desc),
        buttonText = stringResource(id = R.string.location_services_disabled_btn),
        onClick = enableAction,
    )
}

@Composable
fun BluetoothPermissionsNotGranted(launchPermissionsRequest: () -> Unit) {
    ActionRequired(
        icon = ImageVector.vectorResource(R.drawable.ic_bluetooth_disabled),
        title = stringResource(id = R.string.bluetooth_permissions_not_granted_title),
        description = stringResource(id = R.string.bluetooth_permissions_not_granted_desc),
        buttonText = stringResource(id = R.string.bluetooth_permissions_not_granted_btn),
        onClick = launchPermissionsRequest,
    )
}

@Composable
fun BluetoothPermissionsNotAvailable(openSettingsAction: () -> Unit) {
    ActionRequired(
        icon = ImageVector.vectorResource(R.drawable.ic_warning),
        title = stringResource(id = R.string.bluetooth_permissions_not_available_title),
        description = stringResource(id = R.string.bluetooth_permissions_not_available_desc),
        buttonText = stringResource(id = R.string.bluetooth_permissions_not_available_btn),
        onClick = openSettingsAction,
    )
}

@Composable
fun BluetoothTemporarilyUnavailable() {
    ActionRequired(
        icon = ImageVector.vectorResource(R.drawable.ic_waiting_for_bluetooth),
        title = stringResource(id = R.string.bluetooth_temporarily_unavailable_title),
        description = stringResource(id = R.string.bluetooth_temporarily_unavailable_desc),
    )
}

@Composable
private fun ActionRequired(
    icon: ImageVector,
    title: String,
    description: String? = null,
    buttonText: String? = null,
    onClick: () -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.size(8.dp))
        Icon(
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
            imageVector = icon,
            contentDescription = null,
        )
        Spacer(Modifier.size(24.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally),
            textAlign = TextAlign.Center,
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        description?.let { description ->
            Spacer(Modifier.size(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally),
                textAlign = TextAlign.Center,
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        buttonText?.let {
            Spacer(Modifier.size(24.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick,
            ) {
                Text(buttonText)
            }
        }
    }
}

@Preview
@Composable
private fun BluetoothUnavailableDialogPreview() {
    BrewthingsTheme {
        BluetoothDialog(onDismissRequest = {}) {
            BluetoothUnavailable()
        }
    }
}

@Preview
@Composable
private fun BluetoothTemporarilyUnavailableDialogPreview() {
    BrewthingsTheme {
        BluetoothDialog(onDismissRequest = {}) {
            BluetoothTemporarilyUnavailable()
        }
    }
}

@Preview
@Composable
private fun BluetoothDisabledDialogPreview() {
    BrewthingsTheme {
        BluetoothDialog(onDismissRequest = {}) {
            BluetoothDisabled {}
        }
    }
}

@Preview
@Composable
private fun LocationServicesDisabledDialogPreview() {
    BrewthingsTheme {
        BluetoothDialog(onDismissRequest = {}) {
            LocationServicesDisabled {}
        }
    }
}

@Preview
@Composable
private fun BluetoothPermissionsNotGrantedDialogPreview() {
    BrewthingsTheme {
        BluetoothDialog(onDismissRequest = {}) {
            BluetoothPermissionsNotGranted {}
        }
    }
}

@Preview
@Composable
private fun BluetoothPermissionsNotAvailableDialogPreview() {
    BrewthingsTheme {
        BluetoothDialog(onDismissRequest = {}) {
            BluetoothPermissionsNotAvailable {}
        }
    }
}
