@file:OptIn(ExperimentalMaterial3Api::class)

package com.brewthings.app.ui.screen.onboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.domain.BluetoothScanState
import com.brewthings.app.ui.ActivityCallbacks
import com.brewthings.app.ui.component.BluetoothScanBox
import com.brewthings.app.ui.component.BluetoothScanRequirements
import com.brewthings.app.ui.component.TertiaryButton
import com.brewthings.app.ui.component.TopAppBarBackButton
import com.brewthings.app.ui.component.TopAppBarTitle
import com.brewthings.app.ui.navigation.Router
import com.brewthings.app.ui.theme.BrewthingsTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardScreen(
    router: Router,
    activityCallbacks: ActivityCallbacks,
    viewModel: OnboardViewModel = koinViewModel(),
) {
    val isBluetoothScanning by viewModel.isBluetoothScanning.collectAsState()
    val isDone by viewModel.isDone.collectAsState()

    if (isDone) {
        router.back()
    }

    OnboardScreen(
        activityCallbacks = activityCallbacks,
        isBluetoothScanning = isBluetoothScanning,
        onStartScan = { viewModel.startScan() },
        onStopScan = { viewModel.stopScan() },
        onBack = router::back,
    )
}

@Composable
fun OnboardScreen(
    activityCallbacks: ActivityCallbacks,
    isBluetoothScanning: Boolean,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onBack: () -> Unit,
) {
    BluetoothScanRequirements(
        isScanning = isBluetoothScanning,
        onToggleScan = { if (isBluetoothScanning) onStopScan() else onStartScan() },
        activityCallbacks = activityCallbacks,
    ) { scanState, onScan ->
        LaunchedEffect(scanState) {
            if (scanState != BluetoothScanState.InProgress) {
                onScan()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { TopAppBarBackButton(onBack) },
                    title = {
                        TopAppBarTitle(
                            title = stringResource(R.string.onboarding_add_device),
                        )
                    }
                )
            }
        ) { paddingValues ->
            OnboardScreenContent(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                isBluetoothScanning = isBluetoothScanning,
                onStartScan = onStartScan,
            )
        }
    }
}

@Composable
fun OnboardScreenContent(
    modifier: Modifier = Modifier,
    isBluetoothScanning: Boolean,
    onStartScan: () -> Unit,
) {
    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            if (isBluetoothScanning) {
                BluetoothScanBox()

                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    text = stringResource(R.string.onboarding_scanning),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                TertiaryButton(
                    modifier = Modifier
                        .padding(16.dp),
                    text = stringResource(R.string.onboarding_scanning_resume),
                    onClick = onStartScan,
                )
            }
        }
        Spacer(
            modifier = Modifier
                .weight(0.2f),
        )
    }
}

@Preview
@Composable
fun ScanningEnabledPreview() {
    BrewthingsTheme {
        OnboardScreenContent(
            isBluetoothScanning = true,
            onStartScan = {},
        )
    }
}

@Preview
@Composable
fun ScanningDisabledPreview() {
    BrewthingsTheme {
        OnboardScreenContent(
            isBluetoothScanning = false,
            onStartScan = {},
        )
    }
}