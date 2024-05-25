package com.brewthings.app.ui.screens.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.brewthings.app.ui.screens.graph.GraphScreen
import com.brewthings.app.ui.screens.scanning.ScanningScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun SetupNavGraph(
    openAppDetails: () -> Unit,
    showLocationSettings: () -> Unit,
    enableBluetooth: () -> Unit,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Scanning.route
    ) {
        composable(
            route = Screen.Scanning.route
        ) {
            ScanningScreen(
                navController = navController,
                viewModel = koinViewModel(),
                openAppDetails = openAppDetails,
                showLocationSettings = showLocationSettings,
                enableBluetooth = enableBluetooth,
            )
        }
        composable(
            route = Screen.Graph.route
        ) {
            GraphScreen(navController)
        }
    }
}
