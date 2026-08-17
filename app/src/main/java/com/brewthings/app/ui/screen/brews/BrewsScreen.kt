package com.brewthings.app.ui.screen.brews

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brewthings.app.R
import com.brewthings.app.data.model.Brew
import com.brewthings.app.ui.component.TroubleshootingInfo
import com.brewthings.app.ui.component.VerticalSpace
import com.brewthings.app.ui.navigation.Router
import com.brewthings.app.util.newOrCached
import org.koin.androidx.compose.koinViewModel

@Composable
fun BrewsScreen(
    router: Router,
    viewModel: BrewsViewModel = koinViewModel(),
) {
    BrewsScreen(
        state = viewModel.screenState,
        openGraph = { brew -> router.goToBrewGraph(brew) },
    )
}

@Composable
fun BrewsScreen(
    state: BrewsState,
    openGraph: (Brew) -> Unit,
) {
    val lockedBrews = newOrCached(state.brews, emptyList())

    if (lockedBrews.isEmpty()) {
        TroubleshootingInfo(
            modifier = Modifier.fillMaxSize(),
            iconResId = R.drawable.ic_empty_glass,
            title = stringResource(R.string.brews_empty_title),
            description = stringResource(R.string.brews_empty_desc),
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        val brews = lockedBrews.reversed()
        items(brews, key = { "Brew_" + it.og.timestamp }) { brew ->
            BrewCard(
                brew = brew,
                isExpanded = brew == brews.first(), // TODO(Tano): Add a remember
                openGraph = openGraph,
            )
            VerticalSpace()
        }
    }
}
