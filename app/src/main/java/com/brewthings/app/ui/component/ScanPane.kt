package com.brewthings.app.ui.component

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.juul.kable.Bluetooth
import com.juul.kable.Reason

private val Bluetooth.permissionsNeeded: List<String> by lazy {
    when {
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
}

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

            Reason.TurningOn -> Loading()
            null -> BluetoothUnavailable()
        }

        null -> Loading()
    }
}

@Composable
fun BluetoothUnavailable() {
    ActionRequired(
        icon = ImageVector.vectorResource(R.drawable.ic_bluetooth_disabled),
        title = stringResource(id = R.string.bluetooth_unavailable),
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothPermissionsNotGranted(permissions: MultiplePermissionsState) {
    ActionRequired(
        icon = ImageVector.vectorResource(R.drawable.ic_bluetooth_disabled),
        title = stringResource(id = R.string.bluetooth_permissions_not_granted_title),
        description = stringResource(id = R.string.bluetooth_permissions_not_granted_desc),
        buttonText = stringResource(id = R.string.bluetooth_permissions_not_granted_btn),
        onClick = permissions::launchMultiplePermissionRequest,
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
fun Loading() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Center,
    ) {
        CircularProgressIndicator()
    }
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
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Center,
    ) {
        Icon(
            modifier = Modifier.size(150.dp),
            tint = MaterialTheme.colorScheme.primary,
            imageVector = icon,
            contentDescription = null,
        )
        Spacer(Modifier.size(16.dp))
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
            Spacer(Modifier.size(16.dp))
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
            Spacer(Modifier.size(48.dp))
            Button(onClick) {
                Text(buttonText)
            }
        }
    }
}
