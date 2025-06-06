@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.domain.BluetoothScanState
import com.brewthings.app.data.domain.Device
import com.brewthings.app.data.domain.MockDevice
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.ui.theme.GreyLight

@Composable
fun TopAppBarTitle(title: String) {
    Box(
        modifier = Modifier.height(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun ScannedDevicesDropdown(
    selectedDevice: Device?,
    devices: List<Device>,
    onSelect: (Device) -> Unit,
    onAddDevice: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .height(56.dp)
            .clickable { expanded = true },
        contentAlignment = Alignment.Center,
    ) {
        TextWithIcon(
            text = selectedDevice?.displayName ?: stringResource(R.string.scan_device_select),
            iconResId = R.drawable.ic_chevron_down,
            iconAlign = IconAlign.End,
            iconPadding = 4.dp,
            textStyle = MaterialTheme.typography.titleMedium,
        )

        DropdownMenu(
            modifier = Modifier.widthIn(min = 200.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
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
                    },
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = GreyLight,
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
                },
            )
        }
    }
}

@Composable
fun SettingsDropdown(items: List<SettingsItem>) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable { expanded = true },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_settings),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.padding(end = 16.dp),
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    onClick = {
                        item.onSelect()
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun TopAppBarBackButton(
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null,
        )
    }
}

@Composable
fun BrewCompositionAction(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_percent),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@PreviewLightDark
@Composable
private fun OnboardingTopAppBarPreview() {
    BrewthingsTheme {
        TopAppBar(
            navigationIcon = { TopAppBarBackButton { } },
            title = { TopAppBarTitle(stringResource(R.string.onboarding_add_device)) },
            actions = { BrewCompositionAction { } },
        )
    }
}

@PreviewLightDark
@Composable
private fun FullTopAppBarPreview() {
    val device = MockDevice(name = "Rapt Pill", macAddress = "AB:CD:EF:AA")
    BrewthingsTheme {
        TopAppBar(
            title = {
                ScannedDevicesDropdown(
                    selectedDevice = device,
                    devices = listOf(device),
                    onSelect = {},
                    onAddDevice = {},
                )
            },
            actions = {
                BluetoothScanActionButton(BluetoothScanState.InProgress) {}
                SettingsDropdown(listOf())
            },
        )
    }
}

data class SettingsItem(val name: String, val onSelect: () -> Unit)
