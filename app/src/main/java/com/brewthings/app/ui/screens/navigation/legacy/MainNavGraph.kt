package com.brewthings.app.ui.screens.navigation.legacy

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brewthings.app.ui.ActivityCallbacks
import com.brewthings.app.ui.screens.pill.GraphScreen

@Composable
fun MainNavGraph(activityCallbacks: ActivityCallbacks) {
    val outerNavController = rememberNavController()

    NavHost(
        navController = outerNavController,
        startDestination = Destination.HOME,
    ) {
        composable(Destination.HOME) {
            HomeScreen(
                outerNavController = outerNavController,
                activityCallbacks = activityCallbacks,
            )
        }

        /* Entry point for supporting deep linking to a specific tab
        composable("${Destination.HOME}/{tab}") { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab")
            HomeScreen(
                outerNavController = outerNavController,
                activityCallbacks = activityCallbacks,
                startDestination = tab,
            )
        }
        */

        composable(Destination.PILL_GRAPH) {
            GraphScreen(Router(outerNavController))
        }
    }
}
