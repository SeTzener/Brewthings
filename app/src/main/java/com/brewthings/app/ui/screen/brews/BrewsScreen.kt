package com.brewthings.app.ui.screen.brews

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brewthings.app.data.model.Brew
import com.brewthings.app.ui.component.SectionTitle
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        lockedBrews.forEach { brews ->
            item {
                SectionTitle(title = brews.batchName)
            }

            val brewsList = brews.data.reversed()
            items(brewsList, key = { "Brew_" + it.og.timestamp }) { brew ->
                BrewCard(
                    brew = brew,
                    isExpanded = brew == brewsList.first(), // TODO(Tano): Add a remember
                    openGraph = openGraph,
                )
                VerticalSpace()
            }
        }
    }
}
