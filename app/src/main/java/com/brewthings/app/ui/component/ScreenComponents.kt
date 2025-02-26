package com.brewthings.app.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.ui.theme.BrewthingsTheme
import com.brewthings.app.util.datetime.toFormattedDate
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun TimeSinceLastUpdate(
    modifier: Modifier = Modifier,
    now: Instant = Clock.System.now(),
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    lastUpdate: Instant,
) {
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    TextWithIcon(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        text = lastUpdate.toFormattedDate(now, timeZone),
        iconResId = R.drawable.ic_update,
        iconPadding = 4.dp,
        iconColor = textColor,
        textStyle = MaterialTheme.typography.titleSmall,
        textColor = textColor,
    )
}

@Composable
fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String,
    action: String,
    onActionClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 14.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = title,
            style = MaterialTheme.typography.titleSmall,
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable(onClick = onActionClick)
                .padding(top = 8.dp, bottom = 8.dp, start = 8.dp),
        ) {
            val textColor = MaterialTheme.colorScheme.onSurfaceVariant
            TextWithIcon(
                text = action,
                iconResId = R.drawable.ic_chevron_right,
                iconAlign = IconAlign.End,
                iconPadding = 2.dp,
                iconSize = 16.dp,
                iconColor = textColor,
                textStyle = MaterialTheme.typography.bodySmall,
                textColor = textColor,
            )
        }
    }
}

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        enabled = isEnabled,
        onClick = onClick,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun TimeSinceLastUpdatePreview() {
    val timeZone = TimeZone.UTC
    BrewthingsTheme {
        Surface {
            TimeSinceLastUpdate(
                now = LocalDateTime(2025, 2, 16, 14, 30, 0).toInstant(timeZone),
                lastUpdate = LocalDateTime(2025, 2, 16, 14, 15, 0).toInstant(timeZone),
                timeZone = timeZone,
            )
        }
    }
}

@Preview
@Composable
fun SectionTitlePreview() {
    BrewthingsTheme {
        Surface {
            SectionTitle(
                title = stringResource(R.string.scan_section_title_last_measurements),
                action = stringResource(R.string.scan_section_action_current_device_graph),
                onActionClick = {},
            )
        }
    }
}

@Preview
@Composable
fun PrimaryButtonPreview() {
    BrewthingsTheme {
        PrimaryButton(isEnabled = false, text = stringResource(R.string.button_save)) { }
    }
}
