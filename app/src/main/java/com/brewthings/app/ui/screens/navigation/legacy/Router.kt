package com.brewthings.app.ui.screens.navigation.legacy

import androidx.navigation.NavController

class Router(private val navController: NavController) {
    fun back() {
        val startDestination = navController.graph.startDestinationRoute

        // This is to prevent popping the startDestination, e.g. on double taps
        if (navController.currentBackStackEntry?.destination?.route != startDestination) {
            navController.popBackStack()
        }
    }
}
