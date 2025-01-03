package com.brewthings.app.ui.screens.scanning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.brewthings.app.R
import com.brewthings.app.data.model.RaptPill
import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.model.ScannedRaptPillData
import com.brewthings.app.ui.components.BatteryLevelIndicator
import com.brewthings.app.ui.components.ExpandableCard
import com.brewthings.app.ui.components.ScanPane
import com.brewthings.app.ui.components.TextWithIcon
import com.brewthings.app.ui.screens.navigation.legacy.Destination
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolder
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.ui.theme.Typography
import com.brewthings.app.util.datetime.formatDateTime
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
    Surface(color = MaterialTheme.colorScheme.background) {
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
                onFirstLoad = viewModel::onFirstLoad,
                toggleScan = viewModel::toggleScan,
                stopScan = viewModel::stopScan,
                savePill = viewModel::savePill,
                onPillUpdate = viewModel::onPillUpdate,
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun ScanningScreen(
    state: ScanningScreenState,
    navGraph: NavController,
    onFirstLoad: () -> Unit,
    toggleScan: () -> Unit,
    stopScan: () -> Unit,
    onRssiThresholdChanged: (Int) -> Unit,
    savePill: (ScannedRaptPill) -> Unit,
    onPillUpdate: (RaptPill) -> Unit,
) {
    LaunchedEffect(Unit) {
        onFirstLoad()
    }
    val scannedPills = newOrCached(state.scannedPills, emptyList())
    val savedPills = newOrCached(state.savedPills, emptyList())
    val brews = newOrCached(data = state.brews, initialValue = emptyList())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            SectionTitle(title = stringResource(R.string.scanning_advanced_options))
        }

        item {
            ScanningOptions(
                state = state,
                onRssiThresholdChanged = onRssiThresholdChanged,
                toggleScan = toggleScan,
            )
            VerticalSpace()
        }

        if (scannedPills.isNotEmpty()) {
            item {
                SectionTitle(title = stringResource(R.string.scanning_results))
            }
        }

        items(scannedPills, key = { "scanned_" + it.macAddress }) { pill ->
            ScannedPill(
                pill = pill,
                isExpanded = scannedPills.size == 1,
                isInScannedPills = state.scannedPills.contains(pill),
                navGraph = navGraph,
                savePill = savePill,
                stopScan = stopScan,
            )
            VerticalSpace()
        }

        item {
            SectionTitle(title = stringResource(R.string.scanning_saved))
        }

        items(savedPills, key = { "saved_" + it.macAddress }) { pill ->
            Pill(
                pill = pill,
                navGraph = navGraph,
                onPillUpdate = onPillUpdate,
                stopScan = stopScan,
            )
            VerticalSpace()
        }

        item {
            SectionTitle(title = stringResource(R.string.brew_list))
        }

        items(brews, key = { "Brew_" + it.og.timestamp }) { brew ->
            BrewCard(brew = brew, isExpanded = brew == brews.first()) // TODO(Tano): Add a remember
            VerticalSpace()
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        modifier = Modifier.padding(16.dp),
        text = title.uppercase(),
        color = MaterialTheme.colorScheme.onBackground,
        style = Typography.bodyMedium,
    )
}

@Composable
private fun ScanningOptions(
    state: ScanningScreenState,
    onRssiThresholdChanged: (Int) -> Unit,
    toggleScan: () -> Unit,
) {
    Card(
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ExpandableCard(
                topContent = { TopContent() },
                expandedContent = {
                    RssiThreshold(
                        rssiThreshold = state.rssiThreshold,
                        onRssiThresholdChanged = onRssiThresholdChanged,
                    )
                },
            )

            ScanningState(
                scannedPillCount = state.scannedPillsCount,
                filteredPillsCount = state.scannedPills.size,
                scanning = state.scanning,
                onScanButtonClicked = toggleScan,
            )
        }
    }
}

@Composable
private fun VerticalSpace() {
    Spacer(modifier = Modifier.size(16.dp))
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
        @Suppress("UnnecessaryParentheses")
        Slider(
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
            .padding(start = 4.dp, end = 16.dp, bottom = 4.dp),
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
    savePill: (ScannedRaptPill) -> Unit,
    stopScan: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp),
    ) {
        ExpandableCard(
            isExpanded = isExpanded,
            topContent = { ScannedPillTopContent(pill, isInScannedPills, savePill) },
            expandedContent = {
                Column {
                    PillData(
                        temperature = pill.data.temperature,
                        gravity = pill.data.gravity,
                        floatingAngle = pill.data.tilt,
                        battery = pill.data.battery,
                    )
                    PillFooter(
                        name = pill.name,
                        macAddress = pill.macAddress,
                        navGraph = navGraph,
                        stopScan = stopScan,
                    )
                }
            },
        )
    }
}

@Composable
private fun ScannedPillTopContent(
    pill: ScannedRaptPill,
    isInScannedPills: Boolean,
    savePill: (ScannedRaptPill) -> Unit,
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
                maxLines = 1,
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = pill.macAddress,
                style = Typography.bodySmall,
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
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

        Spacer(modifier = Modifier.padding(24.dp))

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
fun BrewTopContent(startDate: Instant, endDate: Instant) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp),
        ) {
            TextWithIcon(
                iconResId = R.drawable.ic_calendar,
                text = stringResource(
                    id = R.string.brew_start_to_end,
                    startDate.formatDateTime("MMM d, yyyy"),
                    endDate.formatDateTime("MMM d, yyyy"),
                ),
            )
        }
    }
}

@Composable
private fun Pill(
    pill: RaptPill,
    navGraph: NavController,
    onPillUpdate: (RaptPill) -> Unit,
    stopScan: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row {
            Column {
                PillTopContent(pill, onPillUpdate)
            }
        }
        val maxTimestamp = pill.data.maxOfOrNull { it.timestamp } ?: Instant.DISTANT_PAST
        pill.data.find { it.timestamp == maxTimestamp }?.let { data ->
            PillData(
                temperature = data.temperature,
                gravity = data.gravity,
                floatingAngle = data.tilt,
                battery = data.battery,
            )
            PillFooter(
                name = pill.name,
                macAddress = pill.macAddress,
                navGraph = navGraph,
                stopScan = stopScan,
            )
        }
    }
}

@Composable
private fun PillTopContent(
    pill: RaptPill,
    onPillUpdate: (RaptPill) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, end = 4.dp),
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
                maxLines = 1,
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = pill.macAddress,
                style = Typography.bodySmall,
            )
        }

        DropDownMenu(pill, onPillUpdate)
    }
}

@Composable
private fun PillData(
    gravity: Float,
    temperature: Float,
    floatingAngle: Float,
    battery: Float,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 62.dp),
    ) {
        Column {
            TextWithIcon(
                iconResId = R.drawable.ic_gravity,
                text = stringResource(id = R.string.pill_gravity, gravity),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextWithIcon(
                iconResId = R.drawable.ic_temperature,
                text = stringResource(id = R.string.pill_temperature, temperature),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column {
            TextWithIcon(
                iconResId = R.drawable.ic_tilt,
                text = stringResource(id = R.string.pill_tilt, floatingAngle),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextWithIcon(
                icon = { BatteryLevelIndicator(battery) },
                text = stringResource(id = R.string.pill_battery, battery),
            )
        }
    }
}

@Composable
private fun PillFooter(
    modifier: Modifier = Modifier,
    name: String?,
    macAddress: String,
    navGraph: NavController,
    stopScan: () -> Unit,
) {
    TextButton(
        modifier = modifier.padding(bottom = 8.dp, start = 10.dp, end = 10.dp),
        onClick = {
            stopScan()
            ParameterHolder.Graph.name = name
            ParameterHolder.Graph.macAddress = macAddress
            navGraph.navigate(route = Destination.Graph)
        },
    ) {
        Text(
            text = stringResource(id = R.string.pill_graph),
            style = Typography.bodyMedium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownMenu(
    raptPill: RaptPill,
    onPillUpdate: (RaptPill) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) } // State to control bottom sheet visibility

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd),
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
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Edit Name") },
                onClick = {
                    showBottomSheet = true
                },
            )
        }

        if (showBottomSheet) {
            expanded = false
            EditNameBottomSheet(
                isBottomSheetVisible = true,
                sheetState = SheetState(skipPartiallyExpanded = true, density = Density(1f)),
                pill = raptPill,
                onDismiss = { showBottomSheet = false },
                onPillUpdate = onPillUpdate,
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
    onPillUpdate: (newPill: RaptPill) -> Unit,
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
                horizontalAlignment = Alignment.CenterHorizontally,
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
                        imeAction = ImeAction.Done,
                    ),
                )

                OutlinedButton(
                    onClick = {
                        onPillUpdate(pill.copy(name = name))
                        onDismiss()
                    },
                    content = { Text(text = stringResource(id = R.string.edit_name_btn)) },
                    enabled = isValidName(oldName = pill.name, newName = name),
                )
            }
        }
    }
}

@Composable
fun <T : Any?> newOrCached(
    data: T,
    initialValue: T,
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

@Preview
@Composable
fun ScannedPillPreview() {
    BrewthingsTheme {
        ScannedPill(
            pill = ScannedRaptPill(
                name = "Pill Name",
                macAddress = "00:00:00:00:00:00",
                rssi = -50,
                data = ScannedRaptPillData(
                    timestamp = Instant.DISTANT_PAST,
                    gravity = 1.0f,
                    gravityVelocity = -2.4f,
                    temperature = 20.0f,
                    x = 236.0625f,
                    y = 4049.375f,
                    z = 1008.9375f,
                    battery = 100f,
                ),
            ),
            isExpanded = true,
            isInScannedPills = true,
            navGraph = rememberNavController(),
            savePill = {},
            stopScan = {},
        )
    }
}

@Preview
@Composable
fun PillPreview() {
    BrewthingsTheme {
        Pill(
            pill = RaptPill(
                name = "Pill Name",
                macAddress = "00:00:00:00:00:00",
                data = listOf(
                    RaptPillData(
                        timestamp = Instant.DISTANT_PAST,
                        gravity = 1.0f,
                        gravityVelocity = -2.4f,
                        temperature = 20.0f,
                        x = 236.0625f,
                        y = 4049.375f,
                        z = 1008.9375f,
                        battery = 100f,
                        isOG = false,
                        isFG = false,
                        isFeeding = true,
                    ),
                ),
            ),
            navGraph = rememberNavController(),
            onPillUpdate = {},
            stopScan = {},
        )
    }
}
