package com.brewthings.app.ui.navigation

import androidx.navigation.NavController
import com.brewthings.app.data.model.Brew

class Router(private val navController: NavController) {
    fun back() {
        val startDestination = navController.graph.startDestinationRoute

        // This is to prevent popping the startDestination, e.g. on double taps
        if (navController.currentBackStackEntry?.destination?.route != startDestination) {
            navController.popBackStack()
        }
    }

    fun goToOnboarding() {
        navController.navigate(route = Destination.ONBOARDING)
    }

    fun goToPillGraph(name: String?, macAddress: String) {
        ParameterHolders.PillGraph.name = name
        ParameterHolders.PillGraph.macAddress = macAddress
        navController.navigate(route = Destination.PILL_GRAPH)
    }

    fun goToBrewGraph(brew: Brew) {
        ParameterHolders.BrewGraph.brew = brew
        navController.navigate(route = Destination.BREWS_GRAPH)
    }

    fun goToBrewComposition(brew: Brew) {
        ParameterHolders.BrewComposition.brew = brew
        navController.navigate(route = Destination.BREW_COMPOSITION)
    }
}
