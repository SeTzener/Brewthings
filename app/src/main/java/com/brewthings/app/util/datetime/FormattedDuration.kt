package com.brewthings.app.util.datetime

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.brewthings.app.R
import kotlin.math.abs
import kotlin.time.Duration

@Composable
fun Duration.toFormattedDuration(): String {
    if (this.isPositive()) { return "" }

    val (days, hours, minutes) = getDaysHoursAndMinutes()

    when {
        // Only minutes have passed.
        days == 0L && hours == 0L -> when {
            (0 downTo -1L).contains(minutes) ->
                return stringResource(R.string.formatted_duration_less_than_a_minute)

            (-2 downTo -59L).contains(minutes) ->
                return stringResource(R.string.formatted_duration_minutes, abs(minutes))
        }

        // Only hours and minutes have passed.
        days == 0L -> when (hours) {
            -1L ->
                return stringResource(R.string.formatted_duration_hour)
            in -2 downTo -23L ->
                return stringResource(R.string.formatted_duration_hours, abs(hours))
        }

        days == -1L -> return stringResource(R.string.formatted_duration_day)
    }

    // Covering dates older than 24 hours ago.
    return  stringResource(R.string.formatted_duration_days, abs(days))
}
