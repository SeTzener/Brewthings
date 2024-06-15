package com.brewthings.app.ui.components

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.brewthings.app.ui.theme.Typography

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    text: String,
    icon: @Composable () -> Unit,
    iconPadding: Dp = 8.dp,
    textStyle: TextStyle = Typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        Spacer(modifier = Modifier.padding(iconPadding))

        Text(
            text = text,
            style = textStyle,
            color = textColor
        )
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
    textStyle: TextStyle = Typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconResId?.also {
            Icon(
                modifier = Modifier.size(size = iconSize),
                imageVector = ImageVector.vectorResource(id = it),
                contentDescription = null,
                tint = iconColor,
            )

            Spacer(modifier = Modifier.padding(iconPadding))
        }

        Text(
            text = text,
            style = textStyle,
            color = textColor
        )
    }
}
