package com.brewthings.app.ui.navigation.legacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brewthings.app.ui.ActivityCallbacks
import com.brewthings.app.ui.screen.graph.GraphScreen

@Composable
fun MainNavGraph(activityCallbacks: ActivityCallbacks) {
    val outerNavController = rememberNavController()

    // This is the single entry point for the entire app where the router is created.
    val router = remember(outerNavController) { Router(outerNavController) }

    NavHost(
        navController = outerNavController,
        startDestination = Destination.HOME,
    ) {
        composable(Destination.HOME) {
            HomeScreen(router, activityCallbacks)
        }

        /* Entry point for supporting deep linking to a specific tab
        composable("${Destination.HOME}/{tab}") { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab")
            HomeScreen(router, activityCallbacks, tab)
        }
         */

        composable(Destination.PILL_GRAPH) {
            GraphScreen(router, Destination.PILL_GRAPH)
        }
    }
}
