package com.brewthings.app.ui.screen.brews

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.model.Brew
import com.brewthings.app.ui.component.ExpandableCard
import com.brewthings.app.ui.component.TextWithIcon
import com.brewthings.app.ui.theme.Typography
import com.brewthings.app.util.datetime.format
import com.brewthings.app.util.datetime.formatDateTime
import kotlinx.datetime.Instant

@Composable
fun BrewCard(
    brew: Brew,
    isExpanded: Boolean,
    openGraph: (Brew) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(0.dp, Color.LightGray),
        shape = RoundedCornerShape(16.dp),
    ) {
        ExpandableCard(
            isExpanded = isExpanded,
            topContent = {
                BrewTopContent(
                    startDate = brew.og.timestamp,
                    endDate = brew.fgOrLast.timestamp,
                )
            },
            expandedContent = {
                Column {
                    BrewData(brew)
                    BrewFooter(brew, openGraph)
                }
            },
        )
    }
}

@Composable
private fun BrewTopContent(startDate: Instant, endDate: Instant) {
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

@SuppressLint("ResourceType")
@Composable
private fun BrewData(brew: Brew) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 0.dp, bottom = 16.dp, end = 22.dp),
    ) {
        Column {
            TextWithIcon(
                iconResId = R.drawable.ic_abv,
                text = stringResource(id = R.string.pill_abv, brew.abv),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextWithIcon(
                iconResId = R.drawable.ic_gravity,
                text = stringResource(id = R.string.brew_card_value_gravity, brew.og.gravity),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column {
            TextWithIcon(
                iconResId = R.drawable.ic_calendar,
                text = stringResource(id = R.string.brew_card_value_duration, brew.durationSinceOG.format()),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextWithIcon(
                iconResId = R.drawable.ic_final_gravity,
                text = stringResource(id = R.string.brew_card_value_gravity, brew.fgOrLast.gravity),
            )
        }
    }
}

@Composable
fun BrewFooter(
    brew: Brew,
    openGraph: (Brew) -> Unit,
) {
    TextButton(
        modifier = Modifier.padding(bottom = 8.dp, start = 10.dp, end = 10.dp),
        onClick = {
            openGraph(brew)
        },
    ) {
        Text(
            text = stringResource(id = R.string.pill_graph),
            style = Typography.bodyMedium,
        )
    }
}
