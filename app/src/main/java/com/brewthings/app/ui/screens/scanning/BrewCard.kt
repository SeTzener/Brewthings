package com.brewthings.app.ui.screens.scanning

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.model.Brew
import com.brewthings.app.ui.components.ExpandableCard
import com.brewthings.app.util.datetime.format

@Composable
fun BrewCard(
    brew: Brew,
    isExpanded: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        ExpandableCard(
            isExpanded = isExpanded,
            topContent = {
                BrewTopContent(
                    startDate = brew.og.timestamp,
                    endDate = brew.fgOrLast.timestamp
                )
            },
            expandedContent = {
                Column {
                    BrewData(brew)
                }
            }
        )
    }
}

@SuppressLint("ResourceType")
@Composable
private fun BrewData(
    brew: Brew,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 0.dp, bottom = 16.dp, end = 22.dp),
    ) {
        Column {
            BrewRow(
                icon = { BrewIcon(it, iconResId = R.drawable.ic_abv) },
                label = { BrewLabel(it, labelResId = R.string.brew_card_label_abv) },
                value = { BrewValue(it, textResId = R.string.brew_card_value_abv, value = brew.abv) },
            )
            BrewRow(
                icon = { BrewIcon(it, iconResId = R.drawable.ic_calendar) },
                label = { BrewLabel(it, labelResId = R.string.brew_card_label_duration) },
                value = { BrewValue(it, textResId = R.string.brew_card_value_duration, value = brew.durationSinceOG.format())}
            )
            BrewRow(
                icon = { BrewIcon(it, iconResId = R.drawable.ic_gravity) },
                label = { BrewLabel(it, labelResId = R.string.brew_card_label_original_gravity) },
                value = { BrewValue(it, textResId = R.string.brew_card_value_gravity, value = brew.og.gravity) },
            )
            BrewRow(
                icon = { BrewIcon(it, iconResId = R.drawable.ic_gravity) },
                label = { BrewLabel(it, labelResId = R.string.brew_card_label_final_gravity) },
                value = { BrewValue(it, textResId = R.string.brew_card_value_gravity, value = brew.fgOrLast.gravity) },
            )
        }
    }
}

@Composable
private fun BrewRow(
    modifier: Modifier = Modifier,
    icon: @Composable (Modifier) -> Unit,
    label: @Composable (Modifier) -> Unit,
    value: @Composable (Modifier) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon(modifier)
        Spacer(modifier = modifier.size(20.dp))
        label(modifier.weight(1f))
        Spacer(modifier = modifier.size(36.dp))
        value(modifier.weight(1f))
    }
}

@Composable
private fun BrewIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
) {
    Icon(
        modifier = modifier.size(24.dp),
        painter = painterResource(id = iconResId),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun BrewLabel(
    modifier: Modifier = Modifier,
    @StringRes labelResId: Int,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = labelResId),
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun BrewValue(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    value: Any?,
) {
    Text(
        modifier = modifier,
        text = if (value != null) stringResource(id = textResId, value) else "",
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.bodyMedium,
    )
}