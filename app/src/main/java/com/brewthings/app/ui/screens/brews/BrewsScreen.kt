package com.brewthings.app.ui.screens.brews

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brewthings.app.data.model.Brew
import com.brewthings.app.ui.components.SectionTitle
import com.brewthings.app.ui.components.VerticalSpace
import com.brewthings.app.ui.screens.navigation.legacy.Destination
import com.brewthings.app.ui.screens.navigation.legacy.ParameterHolders
import com.brewthings.app.util.newOrCached
import org.koin.androidx.compose.koinViewModel

@Composable
fun BrewsScreen(
    navController: NavController,
    viewModel: BrewsScreenViewModel = koinViewModel(),
) {
    BrewsScreen(
        state = viewModel.screenState,
        onBrewClick = { brew -> navController.openBrewGraph(brew) }
    )
}

@Composable
fun BrewsScreen(
    state: BrewsScreenState,
    onBrewClick: (Brew) -> Unit, // TODO(walt): wire it in
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
               BrewCard(brew = brew, isExpanded = brew == brewsList.first()) // TODO(Tano): Add a remember
               VerticalSpace()
           }
       }
    }
}

private fun NavController.openBrewGraph(brew: Brew) {
    ParameterHolders.BrewGraph.brew = brew
    navigate(route = Destination.BREW_GRAPH)
}
