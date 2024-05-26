package com.brewthings.app.ui.screens.navigation

sealed class Screen(val route: String) {
    data object Scanning : Screen(route = "scanning_screen")
    data object Graph : Screen(route = "graph-screen")
}