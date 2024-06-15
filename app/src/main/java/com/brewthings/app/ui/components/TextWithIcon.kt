package com.brewthings.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.theme.Typography

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    text: String,
    icon: @Composable () -> Unit,
    iconPadding: Dp = 8.dp,
    textAlign: TextAlign = TextAlign.Start,
    textStyle: TextStyle = Typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (textAlign == TextAlign.Start) {
            Arrangement.Start
        } else {
            Arrangement.End
        }
    ) {
        if (textAlign == TextAlign.Start) {
            icon()
            Spacer(modifier = Modifier.padding(iconPadding))
        }

        Text(
            text = text,
            textAlign = textAlign,
            style = textStyle,
            color = textColor
        )

        if (textAlign == TextAlign.End) {
            Spacer(modifier = Modifier.padding(iconPadding))
            icon()
        }
    }
}

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes iconResId: Int?,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    iconSize: Dp = 24.dp,
    iconPadding: Dp = 8.dp,
    textAlign: TextAlign = TextAlign.Start,
    textStyle: TextStyle = Typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    TextWithIcon(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        textStyle = textStyle,
        textColor = textColor,
        iconPadding = iconPadding,
        icon = {
            if (iconResId != null) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    tint = iconColor
                )
            } else {
                Spacer(modifier = Modifier.size(iconSize))
            }
        }
    )
}
