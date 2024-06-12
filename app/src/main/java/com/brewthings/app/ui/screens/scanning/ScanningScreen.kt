package com.brewthings.app.ui.screens.scanning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brewthings.app.R
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.ui.components.BatteryLevelIndicator
import com.brewthings.app.ui.components.ExpandableCard
import com.brewthings.app.ui.components.ScanPane
import com.brewthings.app.ui.components.TextWithIcon
import com.brewthings.app.ui.screens.navigation.legacy.Destination
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder
import com.brewthings.app.ui.theme.Typography
import kotlinx.datetime.Instant
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanningScreen(
    navController: NavController,
    viewModel: ScanningScreenViewModel = koinViewModel(),
    openAppDetails: () -> Unit,
    showLocationSettings: () -> Unit,
    enableBluetooth: () -> Unit,
) {
    ScanPane(
        bluetooth = viewModel.screenState.bluetooth,
        openAppDetails = openAppDetails,
        showLocationSettings = showLocationSettings,
        enableBluetooth = enableBluetooth,
    ) {
        ScanningScreen(
            state = viewModel.screenState,
            navGraph = navController,
            onRssiThresholdChanged = viewModel::onRssiThresholdChanged,
            toggleScan = viewModel::toggleScan,
            savePill = viewModel::savePill,
            onPillUpdate = viewModel::onPillUpdate,
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun ScanningScreen(
    state: ScanningScreenState,
    navGraph: NavController,
    onRssiThresholdChanged: (Int) -> Unit,
    toggleScan: () -> Unit,
    savePill: (ScannedRaptPill) -> Unit,
    onPillUpdate: (RaptPill) -> Unit
) {
    val scannedPills = newOrCached(state.scannedPills, emptyList())
    val savedPills = newOrCached(state.savedPills, emptyList())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.scanning_options).uppercase(),
                color = MaterialTheme.colorScheme.onBackground,
                style = Typography.bodyMedium
            )
            Card(
                border = BorderStroke(0.dp, Color.LightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExpandableCard(
                        topContent = { TopContent() },
                        expandedContent = {
                            RssiThreshold(
                                rssiThreshold = state.rssiThreshold,
                                onRssiThresholdChanged = onRssiThresholdChanged,
                            )
                        }
                    )

                    ScanningState(
                        scannedPillCount = state.scannedPillsCount,
                        filteredPillsCount = state.scannedPills.size,
                        scanning = state.scanning,
                        onScanButtonClicked = toggleScan,
                    )
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.scanning_results).uppercase(),
                color = MaterialTheme.colorScheme.onBackground,
                style = Typography.bodyMedium
            )
        }

        items(scannedPills, key = { "scanned_" + it.macAddress }) { pill ->
            ScannedPill(
                pill = pill,
                isExpanded = scannedPills.size == 1,
                isInScannedPills = state.scannedPills.contains(pill),
                savePill = savePill,
                navGraph = navGraph
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.scanning_saved).uppercase(),
                color = MaterialTheme.colorScheme.onBackground,
                style = Typography.bodyMedium
            )
        }

        items(savedPills, key = { "saved_" + it.macAddress }) { pill ->
            Pill(
                pill = pill,
                navGraph = navGraph,
                onPillUpdate = onPillUpdate
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    LaunchedEffect(key1 = state.bluetooth) {
        toggleScan()
    }
}

@Composable
private fun TopContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        Text(stringResource(R.string.scanning_advanced_options), style = Typography.bodyMedium)
    }
}

@Composable
private fun RssiThreshold(
    rssiThreshold: Int,
    onRssiThresholdChanged: (Int) -> Unit,
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = stringResource(R.string.scanning_rssi),
                style = Typography.bodyMedium,
            )
            Text(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = rssiThreshold.toString(),
                style = Typography.bodyMedium,
            )
        }
        @Suppress("UnnecessaryParentheses") Slider(
            value = (rssiThreshold * -1).toFloat(),
            onValueChange = { onRssiThresholdChanged(it.toInt() * -1) },
            valueRange = RSSI_THRESHOLD_RANGE_START..(RSSI_THRESHOLD_RANGE_END * -1),
        )
    }
}

@Composable
private fun ScanningState(
    scannedPillCount: Int,
    filteredPillsCount: Int,
    scanning: Boolean,
    onScanButtonClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 16.dp, bottom = 4.dp)
    ) {
        TextButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onScanButtonClicked,
        ) {
            Text(
                text = if (scanning) stringResource(R.string.scanning_stop) else stringResource(R.string.scanning_start),
                style = Typography.bodyMedium,
            )
        }
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (scanning) {
                Box(Modifier.padding(horizontal = 16.dp)) {
                    CircularProgressIndicator(Modifier.size(16.dp))
                }
            }
            Text(
                text = "$filteredPillsCount ($scannedPillCount)",
                style = Typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ScannedPill(
    pill: ScannedRaptPill,
    isExpanded: Boolean,
    isInScannedPills: Boolean,
    navGraph: NavController,
    savePill: (ScannedRaptPill) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        ExpandableCard(
            isExpanded = isExpanded,
            topContent = { ScannedPillTopContent(pill, isInScannedPills, savePill) },
            expandedContent = {
                PillData(
                    name = pill.name,
                    macAddress = pill.macAddress,
                    pill.data,
                    navGraph = navGraph
                )
            }
        )
    }
}

@Composable
private fun ScannedPillTopContent(
    pill: ScannedRaptPill,
    isInScannedPills: Boolean,
    savePill: (ScannedRaptPill) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = pill.name ?: stringResource(R.string.scanning_result),
                overflow = TextOverflow.Ellipsis,
                style = Typography.bodyMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = pill.macAddress,
                style = Typography.bodySmall,
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (isInScannedPills) {
                Text(
                    text = stringResource(id = R.string.pill_rssi, pill.rssi),
                    style = Typography.bodySmall,
                )
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_bluetooth_disabled),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.padding(12.dp))

        IconButton(onClick = { savePill(pill) }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_save),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun Pill(
    pill: RaptPill,
    navGraph: NavController,
    onPillUpdate: (RaptPill) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row {
            Column {
                PillTopContent(pill, onPillUpdate)
            }
        }
        Column {
            val maxTimestamp = pill.data.maxOfOrNull { it.timestamp } ?: Instant.DISTANT_PAST
            pill.data.find { it.timestamp == maxTimestamp }?.let { data ->
                PillData(name = pill.name, macAddress = pill.macAddress, pillData = data, navGraph = navGraph)
            }
        }
    }
}

@Composable
private fun PillTopContent(
    pill: RaptPill,
    onPillUpdate: (RaptPill) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 20.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = pill.name ?: stringResource(R.string.scanning_result),
                overflow = TextOverflow.Ellipsis,
                style = Typography.bodyMedium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = pill.macAddress,
                style = Typography.bodySmall,
            )
        }
        Column {
            DropDownMenu(pill, onPillUpdate)
        }
    }
}

@Composable
private fun PillData(name: String?, macAddress: String, pillData: RaptPillData?, navGraph: NavController) {
    newOrCached(pillData, null)?.let { data ->
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 62.dp),
            ) {
                Column {
                    TextWithIcon(
                        iconResId = R.drawable.ic_gravity,
                        text = stringResource(id = R.string.pill_gravity, data.gravity)
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextWithIcon(
                        iconResId = R.drawable.ic_temperature,
                        text = stringResource(id = R.string.pill_temperature, data.temperature)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column {
                    TextWithIcon(
                        iconResId = R.drawable.ic_tilt,
                        text = stringResource(id = R.string.pill_tilt, data.floatingAngle)
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextWithIcon(
                        icon = { BatteryLevelIndicator(data.battery) },
                        text = stringResource(id = R.string.pill_battery, data.battery)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 62.dp),
            ) {
                Column {
                    Spacer(modifier = Modifier.padding(40.dp))
                    Column {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 50.dp),
                            color = Color.LightGray
                        )
                        IconButton(
                            onClick = {
                                ParameterHolder.Graph.name = name
                                ParameterHolder.Graph.macAddress = macAddress
                                navGraph.navigate(route = Destination.Graph)
                            },
                        ) {
                            TextWithIcon(
                                iconResId = R.drawable.ic_pill,
                                text = stringResource(id = R.string.pill_graph),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownMenu(
    raptPill: RaptPill,
    onPillUpdate: (RaptPill) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) } // State to control bottom sheet visibility

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit Name") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }

        if (showBottomSheet) {
            expanded = false
            EditNameBottomSheet(
                isBottomSheetVisible = showBottomSheet,
                sheetState = SheetState(skipPartiallyExpanded = true, density = Density(1f)),
                pill = raptPill,
                onDismiss = { showBottomSheet = false },
                onPillUpdate = onPillUpdate
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNameBottomSheet(
    isBottomSheetVisible: Boolean,
    sheetState: SheetState,
    pill: RaptPill,
    onDismiss: () -> Unit,
    onPillUpdate: (newPill: RaptPill) -> Unit
) {
    var name by remember { mutableStateOf(pill.name) }

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = null,
            scrimColor = Color.Black.copy(alpha = .5f),
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .imePadding()
                    .padding(vertical = 32.dp, horizontal = 24.dp), // Inner padding,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    value = name ?: "",
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(id = R.string.edit_name_tooltip)) },
                    readOnly = false,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                OutlinedButton(
                    onClick = {
                        onPillUpdate(pill.copy(name = name))
                        onDismiss()
                    },
                    content = { Text(text = stringResource(id = R.string.edit_name_btn)) },
                    enabled = isValidName(oldName = pill.name, newName = name)
                )
            }
        }
    }
}

@Composable
fun <T : Any?> newOrCached(
    data: T,
    initialValue: T
): T {
    var previousData: T by remember { mutableStateOf(initialValue) }
    return if (data != null) {
        previousData = data
        data
    } else {
        previousData
    }
}

private fun isValidName(oldName: String?, newName: String?): Boolean =
    newName?.trim()?.let { it.isNotEmpty() && it != oldName } ?: false
