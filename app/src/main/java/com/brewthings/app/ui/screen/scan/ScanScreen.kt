package com.brewthings.app.ui.screen.scan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.ui.ActivityCallbacks
import com.brewthings.app.ui.component.IconAlign
import com.brewthings.app.ui.component.TextWithIcon
import com.brewthings.app.ui.navigation.legacy.Router
import com.brewthings.app.ui.theme.BrewthingsTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanScreen(
    router: Router,
    activityCallbacks: ActivityCallbacks,
    viewModel: ScanViewModel = koinViewModel(),
) {

}

@Composable
fun SectionTitle(
    title: String,
    action: String,
    onActionClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = title,
            style = MaterialTheme.typography.titleSmall,
        )

        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
                .clickable(onClick = onActionClick)
                .padding(top = 16.dp, bottom = 16.dp, start = 8.dp)
                ,
        ) {
            TextWithIcon(
                text = action,
                iconResId = R.drawable.ic_chevron_right,
                iconAlign = IconAlign.End,
                iconPadding = 2.dp,
                iconSize = 16.dp,
                textStyle = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
fun ScanScreenPreview() {
    BrewthingsTheme {
        Surface {
            Column {
                SectionTitle(
                    title = stringResource(R.string.scan_section_title_last_measurements),
                    action = stringResource(R.string.scan_section_action_current_device_graph),
                    onActionClick = {},
                )
            }
        }
    }
}
