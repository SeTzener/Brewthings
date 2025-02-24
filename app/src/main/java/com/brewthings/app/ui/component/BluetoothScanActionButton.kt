package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.domain.BluetoothScanState
import com.brewthings.app.ui.theme.BrewthingsTheme

@Composable
fun BluetoothScanActionButton(
    scanState: BluetoothScanState,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp) // Standard action button size
    ) {
        BluetoothScanIcon(scanState, onClick)
    }
}

@Composable
fun BluetoothScanBox() {
    val color = MaterialTheme.colorScheme.primary

    ScanningEffect(color = color)

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color)
            .fillMaxWidth(fraction = 0.4f)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        BluetoothScanProgressIcon()
    }
}

@Composable
fun ScanningEffect(color: Color) {
    val transition = rememberInfiniteTransition()
    val scale by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas {
            scale(scale) {
                drawCircle(color.copy(alpha = alpha))
            }
        }
    }
}

@Composable
fun ErrorShakeEffect(content: @Composable (Modifier) -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "ShakeTransition")

    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShakeAnimation",
    )

    Box(modifier = Modifier.offset(x = offsetX.dp)) {
        content(Modifier)
    }
}

@Composable
fun BluetoothUnavailableButton(onClick: () -> Unit) {
    val buttonColor = MaterialTheme.colorScheme.onSurface
    ErrorShakeEffect {
        OutlinedIconButton(
            onClick = onClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            border = BorderStroke(2.dp, buttonColor),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = Color.Transparent,
                contentColor = buttonColor,
            )
        ) {
            BluetoothScanDisabledIcon(
                imageResId = R.drawable.ic_bluetooth_disabled,
                descriptionResId = R.string.a11y_bluetooth_scan_error,
                buttonColor = buttonColor,
            )
        }
    }
}

@Composable
fun BluetoothScanIdleButton(onClick: () -> Unit) {
    val buttonColor = MaterialTheme.colorScheme.primary
    OutlinedIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        border = BorderStroke(2.dp, buttonColor),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = Color.Transparent,
            contentColor = buttonColor,
        )
    ) {
        BluetoothScanDisabledIcon(
            imageResId = R.drawable.ic_bluetooth,
            descriptionResId = R.string.a11y_bluetooth_scan_idle,
            buttonColor = buttonColor,
        )
    }
}

@Composable
fun BluetoothScanProgressButton(onClick: () -> Unit) {
    val buttonColor = MaterialTheme.colorScheme.primary

    ScanningEffect(color = buttonColor)

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        colors = IconButtonDefaults.iconButtonColors(containerColor = buttonColor)
    ) {
        BluetoothScanProgressIcon()
    }
}

@Composable
fun BluetoothScanDisabledIcon(
    @DrawableRes imageResId: Int,
    @StringRes descriptionResId: Int,
    buttonColor: Color,
) {
    Icon(
        modifier = Modifier.fillMaxSize(fraction = 0.5f),
        imageVector = ImageVector.vectorResource(imageResId),
        contentDescription = stringResource(descriptionResId),
        tint = buttonColor,
    )
}

@Composable
fun BluetoothScanProgressIcon() {
    Icon(
        modifier = Modifier.fillMaxSize(fraction = 0.5f),
        imageVector = ImageVector.vectorResource(R.drawable.ic_bluetooth_scan),
        contentDescription = stringResource(R.string.a11y_bluetooth_scan_scanning),
        tint = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun BluetoothScanIcon(
    scanState: BluetoothScanState,
    onClick: () -> Unit,
) {
    when (scanState) {
        BluetoothScanState.InProgress -> BluetoothScanProgressButton(onClick)
        BluetoothScanState.Idle -> BluetoothScanIdleButton(onClick)
        BluetoothScanState.Unavailable -> BluetoothUnavailableButton(onClick)
    }
}

@Preview
@Composable
fun BluetoothScanBoxPreview() {
    BrewthingsTheme {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                BluetoothScanBox()
            }
        }
    }
}
