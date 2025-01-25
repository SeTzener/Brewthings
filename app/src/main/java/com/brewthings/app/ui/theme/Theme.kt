package com.brewthings.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Darkmode_Primary,
    secondary = Darkmode_Secondary,
    tertiary = Darkmode_PrimaryVariant,
)

private val LightColorScheme = lightColorScheme(
    primary = Brightmode_Primary,
    secondary = Brightmode_Secondary,
    tertiary = Brightmode_PrimaryVariant,
)

@Composable
fun BrewthingsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
