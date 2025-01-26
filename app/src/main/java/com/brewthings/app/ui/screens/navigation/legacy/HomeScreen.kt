package com.brewthings.app.ui.screens.navigation.legacy

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.brewthings.app.R
import com.brewthings.app.ui.ActivityCallbacks
import com.brewthings.app.ui.components.BackgroundStatusBar
import com.brewthings.app.ui.components.ElevatedNavigationBar
import com.brewthings.app.ui.screens.brews.BrewsScreen
import com.brewthings.app.ui.screens.scanning.ScanningScreen
import kotlinx.coroutines.flow.map

private const val DEFAULT_TAB = Destination.Tab.SCANNING

@Composable
fun HomeScreen(
    router: Router,
    activityCallbacks: ActivityCallbacks,
    startDestination: String? = null,
) {
    val innerNavController = rememberNavController()

    BackgroundStatusBar()
    ElevatedNavigationBar()

    val defaultTab = startDestination ?: DEFAULT_TAB

    // Track the current destination
    val currentDestination by innerNavController.currentBackStackEntryFlow
        .map { it.destination.route }
        .collectAsState(initial = defaultTab)

    Scaffold(
        bottomBar = {
            val tabs = listOf(
                TabItem(Destination.Tab.SCANNING, R.string.tab_scanning, R.drawable.ic_bluetooth_scan),
                TabItem(Destination.Tab.BREWS, R.string.tab_brews, R.drawable.ic_list),
            )

            NavigationBar {
                tabs.forEach { tab ->
                    val label = stringResource(tab.labelResId)
                    NavigationBarItem(
                        selected = tab.route == currentDestination,
                        onClick = {
                            innerNavController.navigate(tab.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(innerNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = { Icon(ImageVector.vectorResource(tab.iconResId), contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = defaultTab,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destination.Tab.SCANNING) {
                ScanningScreen(
                    router = router,
                    activityCallbacks = activityCallbacks
                )
            }
            composable(Destination.Tab.BREWS) {
                BrewsScreen(router = router)
            }
        }
    }
}

private data class TabItem(
    val route: String,
    @StringRes val labelResId: Int,
    @DrawableRes val iconResId: Int,
)
