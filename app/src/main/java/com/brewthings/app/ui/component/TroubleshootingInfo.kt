package com.brewthings.app.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
        TroubleshootingInfoContent(
            modifier = modifier,
            iconResId = iconResId,
            title = title,
            description = description,
            buttonText = buttonText,
            onButtonClick = onButtonClick,
        )
    }
}

@Composable
private fun TroubleshootingInfoContent(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    title: String,
    description: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
) {
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (columnRef, iconRef, buttonRef) = createRefs()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .constrainAs(columnRef) {
                    centerTo(parent)
                },
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
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

        Icon(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .size(96.dp)
                .constrainAs(iconRef) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(columnRef.top)
                },
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        if (buttonText != null) {
            TertiaryButton(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .constrainAs(buttonRef) {
                        top.linkTo(columnRef.bottom)
                    },
                text = buttonText,
            ) {
                onButtonClick?.invoke()
            }
        }
    }
}

@Preview
@Composable
fun TroubleshootingInfoContentPreview() {
    BrewthingsTheme {
        Surface {
            TroubleshootingInfoContent(
                iconResId = R.drawable.ic_empty_glass,
                title = stringResource(R.string.scan_troubleshooting_no_active_brew_title),
                description = stringResource(R.string.scan_troubleshooting_no_active_brew_desc),
                buttonText = stringResource(R.string.button_view_previous_data),
            )
        }
    }
}
