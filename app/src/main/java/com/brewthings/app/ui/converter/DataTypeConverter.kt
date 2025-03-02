package com.brewthings.app.ui.converter

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.brewthings.app.R
import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.domain.Trend
import com.brewthings.app.ui.theme.Coral
import com.brewthings.app.ui.theme.CoralDark
import com.brewthings.app.ui.theme.DarkTurquoise
import com.brewthings.app.ui.theme.DarkTurquoiseDark
import com.brewthings.app.ui.theme.Gold
import com.brewthings.app.ui.theme.GoldDark
import com.brewthings.app.ui.theme.LimeGreen
import com.brewthings.app.ui.theme.LimeGreenDark
import com.brewthings.app.ui.theme.MediumPurple
import com.brewthings.app.ui.theme.MediumPurpleDark
import com.brewthings.app.ui.theme.RedAlert
import com.brewthings.app.ui.theme.RedAlertDark
import com.brewthings.app.ui.theme.SteelBlue
import com.brewthings.app.ui.theme.SteelBlueDark
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Composable
fun DataType.toIconRes(optionalValue: Float? = null): Int = when (this) {
    DataType.GRAVITY -> R.drawable.ic_gravity
    DataType.TEMPERATURE -> R.drawable.ic_temperature
    DataType.TILT -> R.drawable.ic_tilt
    DataType.ABV -> R.drawable.ic_abv
    DataType.VELOCITY_MEASURED -> R.drawable.ic_velocity_measured
    DataType.VELOCITY_COMPUTED -> R.drawable.ic_velocity_computed
    DataType.BATTERY -> when {
        optionalValue == null || optionalValue <= 0f -> R.drawable.ic_battery_0
        optionalValue <= 0.125f -> R.drawable.ic_battery_1
        optionalValue <= 0.25f -> R.drawable.ic_battery_2
        optionalValue <= 0.375f -> R.drawable.ic_battery_3
        optionalValue <= 0.5f -> R.drawable.ic_battery_4
        optionalValue <= 0.75f -> R.drawable.ic_battery_5
        optionalValue < 1f -> R.drawable.ic_battery_6
        else -> R.drawable.ic_battery_7
    }
}

@Composable
fun Trend.toIconRes(): Int? = when (this) {
    Trend.Upwards -> R.drawable.ic_trending_up
    Trend.Downwards -> R.drawable.ic_trending_down
    Trend.Stationary -> null
}

@Composable
fun DataType.toColor(): Color = toColor(isDarkTheme = false)

@Composable
fun DataType.toColor(isDarkTheme: Boolean): Color = when (this) {
    DataType.GRAVITY -> if (isDarkTheme) SteelBlueDark else SteelBlue
    DataType.TEMPERATURE -> if (isDarkTheme) MediumPurpleDark else MediumPurple
    DataType.BATTERY -> if (isDarkTheme) RedAlertDark else RedAlert
    DataType.TILT -> if (isDarkTheme) DarkTurquoiseDark else DarkTurquoise
    DataType.ABV -> if (isDarkTheme) LimeGreenDark else LimeGreen
    DataType.VELOCITY_MEASURED -> if (isDarkTheme) CoralDark else Coral
    DataType.VELOCITY_COMPUTED -> if (isDarkTheme) GoldDark else Gold
}

@Composable
fun DataType.toFormatPattern(): String = when (this) {
    DataType.GRAVITY -> "0.000"
    DataType.BATTERY -> "0.##%"
    DataType.ABV -> "0.00"
    DataType.TEMPERATURE,
    DataType.TILT,
    DataType.VELOCITY_MEASURED,
    DataType.VELOCITY_COMPUTED,
    -> "0.##"
}

@Composable
fun DataType.toUnit(): String = stringResource(
    when (this) {
        DataType.GRAVITY -> R.string.unit_gravity
        DataType.TEMPERATURE -> R.string.unit_temperature_celsius
        DataType.BATTERY -> R.string.unit_battery
        DataType.TILT -> R.string.unit_tilt
        DataType.ABV -> R.string.unit_abv
        DataType.VELOCITY_MEASURED,
        DataType.VELOCITY_COMPUTED,
        -> R.string.unit_velocity
    },
)

@Composable
fun DataType.toLabel(isShortened: Boolean = false): String = stringResource(
    when (this) {
        DataType.GRAVITY ->
            R.string.graph_data_label_gravity

        DataType.TEMPERATURE -> if (isShortened) {
            R.string.graph_data_label_temp_short
        } else
            R.string.graph_data_label_temp_full

        DataType.BATTERY ->
            R.string.graph_data_label_battery

        DataType.TILT ->
            R.string.graph_data_label_tilt

        DataType.ABV ->
            R.string.graph_data_label_abv

        DataType.VELOCITY_MEASURED -> if (isShortened) {
            R.string.graph_data_label_velocity_measured_short
        } else
            R.string.graph_data_label_velocity_measured_full

        DataType.VELOCITY_COMPUTED -> if (isShortened) {
            R.string.graph_data_label_velocity_computed_short
        } else
            R.string.graph_data_label_velocity_computed_full
    },
)

@Composable
fun DataType.toValueFormatter(): DecimalFormat {
    val symbols = DecimalFormatSymbols().apply { percent = 0.toChar() } // Disable %
    return DecimalFormat(toFormatPattern(), symbols)
}
