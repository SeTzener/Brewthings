package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.ui.theme.BrewthingsTheme

@Composable
fun TroubleshootingInfo(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    title: String,
    description: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
) {
    DelayedContent {
        Box {
            Column(modifier = modifier.fillMaxSize()) {
                Spacer(
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f),
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier
                            .size(96.dp),
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                        .weight(2f)
                        .padding(horizontal = 32.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        text = description,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (buttonText != null) {
                PrimaryButton(
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
                        .align(Alignment.BottomCenter),
                    text = buttonText,
                ) {
                    onButtonClick?.invoke()
                }
            }
        }
    }
}

@Preview
@Composable
fun TroubleshootingInfoPreview() {
    BrewthingsTheme {
        Surface {
            TroubleshootingInfo(
                iconResId = R.drawable.ic_empty_glass,
                title = stringResource(R.string.scan_troubleshooting_no_active_brew_title),
                description = stringResource(R.string.scan_troubleshooting_no_active_brew_desc),
                buttonText = stringResource(R.string.button_view_previous_data),
            )
        }
    }
}
