@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screen.scan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.domain.Device
import com.brewthings.app.data.domain.MockDevice
import com.brewthings.app.ui.component.BluetoothScanActionButton
import com.brewthings.app.ui.component.BluetoothScanState
import com.brewthings.app.ui.component.IconAlign
import com.brewthings.app.ui.component.TextWithIcon
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.ui.theme.Grey_Light

@Composable
fun ScannedDevicesDropdown(
    modifier: Modifier = Modifier,
    selectedDevice: Device,
    devices: List<Device>,
    onSelect: (Device) -> Unit,
    onAddDevice: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .height(56.dp)
            .clickable { expanded = true },
        contentAlignment = Alignment.Center,
    ) {
        TextWithIcon(
            text = selectedDevice.displayName,
            iconResId = R.drawable.ic_chevron_down,
            iconAlign = IconAlign.End,
            iconPadding = 4.dp,
            textStyle = MaterialTheme.typography.titleMedium,
        )

        DropdownMenu(
            modifier = Modifier.widthIn(min = 200.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            devices.forEach { device ->
                DropdownMenuItem(
                    text = {
                        TextWithIcon(
                            modifier = Modifier.padding(end = 16.dp),
                            text = device.displayName,
                            icon = {
                                Checkbox(
                                    checked = device == selectedDevice,
                                    onCheckedChange = null,
                                )
                            },
                            iconAlign = IconAlign.Start,
                            iconPadding = 8.dp,
                            textStyle = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    onClick = {
                        onSelect(device)
                        expanded = false
                    }
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Grey_Light
            )
            DropdownMenuItem(
                text = {
                    TextWithIcon(
                        modifier = Modifier.padding(end = 16.dp),
                        text = stringResource(R.string.dropdown_add_device),
                        iconResId = R.drawable.ic_add,
                        iconAlign = IconAlign.Start,
                        iconPadding = 8.dp,
                        textStyle = MaterialTheme.typography.bodyMedium,
                    )
                },
                onClick = {
                    onAddDevice()
                    expanded = false
                }
            )
        }
    }
}

@Composable
@Preview
private fun FullTopAppBarPreview() {
    val devices = listOf(
        MockDevice(name = "Rapt Pill", macAddress = "AB:CD:EF:AA"),
        MockDevice(name = "Sample Pill", macAddress = "AB:CD:EF:AB"),
        MockDevice(name = "Pillolone", macAddress = "AB:CD:EF:BA"),
    )
    var selectedDevice: Device by remember { mutableStateOf(devices[1]) }

    var scanState by remember { mutableStateOf(BluetoothScanState.Error) }
    val onScanClick = {
        scanState = when (scanState) {
            BluetoothScanState.Error -> BluetoothScanState.Idle
            BluetoothScanState.Idle -> BluetoothScanState.Scanning
            BluetoothScanState.Scanning -> BluetoothScanState.Error
        }
    }

    BrewthingsTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        ScannedDevicesDropdown(
                            selectedDevice = selectedDevice,
                            devices = devices,
                            onSelect = { selectedDevice = it },
                            onAddDevice = {},
                        )
                    },
                    actions = {
                        BluetoothScanActionButton(scanState, onScanClick)
                    }
                )
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues))
            }
        )
    }
}
