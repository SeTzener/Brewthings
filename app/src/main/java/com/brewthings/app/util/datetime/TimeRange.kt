package com.brewthings.app.util.datetime

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.brewthings.app.R
import kotlin.math.abs
import kotlin.time.Duration
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TimeRange(val from: Instant, val to: Instant)

@Composable
fun TimeRange.format(): String {
    val diff: Duration = to - from
    val (days, hours, minutes) = diff.getDaysHoursAndMinutes()
    val daysIgnoringTime = daysBetweenIgnoringTime(from, to)

    when {
        // Only minutes have passed.
        days == 0L && hours == 0L -> when {
            minutes < 1L ->
                return stringResource(R.string.formatted_duration_less_than_a_minute)

            minutes == 1L ->
                return stringResource(R.string.formatted_duration_minute)

            (2L..59L).contains(minutes) ->
                return stringResource(R.string.formatted_duration_minutes, abs(minutes))
        }

        // Only hours and minutes have passed.
        days == 0L -> when (hours) {
            1L ->
                return stringResource(R.string.formatted_duration_hour)
            in 2L..23L ->
                return stringResource(R.string.formatted_duration_hours, abs(hours))
        }

        daysIgnoringTime == 1 -> return stringResource(R.string.formatted_duration_day)
    }

    // Covering dates older than 24 hours ago.
    return stringResource(R.string.formatted_duration_days, daysIgnoringTime)
}
