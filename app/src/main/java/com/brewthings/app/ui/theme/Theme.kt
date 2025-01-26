package com.brewthings.app.ui.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColoringEnabled: Boolean = true, //TODO(walt): wire it with a setting from the app
    content: @Composable () -> Unit,
) {
    val colorScheme = getColorScheme(isDynamicColoringEnabled, isDarkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

@Composable
private fun getColorScheme(isDynamicColoringEnabled: Boolean, isDarkTheme: Boolean): ColorScheme =
    if (isDynamicColoringEnabled && isDynamicColoringSupported())
        getDynamicColorScheme(isDarkTheme)
    else
        getAppColorScheme(isDarkTheme)

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
private fun isDynamicColoringSupported() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun getDynamicColorScheme(isDarkTheme: Boolean): ColorScheme {
    val context = LocalContext.current
    return if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
}

@Composable
private fun getAppColorScheme(isDarkTheme: Boolean): ColorScheme =
    if (isDarkTheme) DarkColorScheme else LightColorScheme
