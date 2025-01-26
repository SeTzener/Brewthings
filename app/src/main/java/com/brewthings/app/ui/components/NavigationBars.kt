package com.brewthings.app.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ElevatedNavigationBar() {
    UpdateSystemNavigationBarColor(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(NavigationBarDefaults.Elevation),
        isDarkTheme = isSystemInDarkTheme(),
    )
}

@Composable
fun BackgroundNavigationBar() {
    UpdateSystemNavigationBarColor(
        color = MaterialTheme.colorScheme.background,
        isDarkTheme = isSystemInDarkTheme(),
    )
}

@Composable
fun BackgroundStatusBar() {
    UpdateSystemStatusBarColor(
        color = MaterialTheme.colorScheme.background,
        isDarkTheme = isSystemInDarkTheme(),
    )
}

@Composable
private fun UpdateSystemNavigationBarColor(color: Color, isDarkTheme: Boolean) {
    // Suggested to use Android's edgeToEdge. I would, if it worked! ヽ(ಠ_ಠ)ノ
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(color, isDarkTheme) {
        systemUiController.setNavigationBarColor(
            color = color,
            darkIcons = !isDarkTheme,
        )
    }
}

@Composable
private fun UpdateSystemStatusBarColor(color: Color, isDarkTheme: Boolean) {
    // Suggested to use Android's edgeToEdge. I would, if it worked! ヽ(ಠ_ಠ)ノ
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(color, isDarkTheme) {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = !isDarkTheme,
        )
    }
}
