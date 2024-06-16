package com.brewthings.app.util.datetime

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.brewthings.app.R
import kotlin.math.abs
import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

@Composable
fun Instant.toFormattedDate(
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val now = clock.now()
    val startOfDay = startOfDay(now = now, timeZone = timeZone)
    val date = this

    // Diff will have negative values for past dates, positive values for future dates.
    val diff: Duration = date - now
    val (days, hours, minutes) = diff.getDaysHoursAndMinutes()

    when {
        // Only minutes have passed since now.
        days == 0L && hours == 0L -> when {
            (0 downTo -1L).contains(minutes) ->
                return stringResource(R.string.formatted_date_less_than_a_minute)

            (-2 downTo -59L).contains(minutes) ->
                return stringResource(R.string.formatted_date_minutes_ago, abs(minutes))
        }

        // Only hours and minutes have passed since now.
        days == 0L -> when (hours) {
            -1L ->
                return stringResource(R.string.formatted_date_hour_ago)
            in -2 downTo -23L ->
                return stringResource(R.string.formatted_date_hours_ago, abs(hours))
        }

        // Days, hours and minutes have passed since now.
        days != 0L -> {
            val diffFromStartOfDay = date - startOfDay
            val (daysFromStartOfDay, _, _) = diffFromStartOfDay.getDaysHoursAndMinutes()

            when (daysFromStartOfDay) {
                in -1L downTo -6L -> {
                    val lastWeekday = stringResource(
                        id = R.string.formatted_date_last_weekday,
                        date.formatDateTime("EEEE", timeZone)
                    )
                    return "$lastWeekday, ${date.formatDateTime("MMM d, HH:mm", timeZone)}"
                }
            }
        }
    }

    // Covering both dates older than 1 week ago and dates in the future.
    return date.formatDateTime("MMM d, yyyy, HH:mm", timeZone)
}
