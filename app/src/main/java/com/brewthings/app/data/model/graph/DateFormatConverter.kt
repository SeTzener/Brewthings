package com.brewthings.app.data.model.graph

import com.brewthings.app.data.model.graph.DateFormat.Absolute.Type.BETWEEN_TWO_AND_SIX_DAYS_AGO
import com.brewthings.app.data.model.graph.DateFormat.Absolute.Type.MORE_THAN_SIX_DAYS_AGO
import com.brewthings.app.data.model.graph.DateFormat.Absolute.Type.MORE_THAN_TWO_HOURS_LATER
import com.brewthings.app.data.model.graph.DateFormat.Absolute.Type.YESTERDAY
import com.brewthings.app.data.model.graph.DateFormat.Constant.Type.ERROR
import com.brewthings.app.data.model.graph.DateFormat.Constant.Type.JUST_NOW
import com.brewthings.app.data.model.graph.DateFormat.Relative.Type.HOURS_AGO
import com.brewthings.app.data.model.graph.DateFormat.Relative.Type.HOURS_LATER
import com.brewthings.app.data.model.graph.DateFormat.Relative.Type.MINUTES_AGO
import com.brewthings.app.data.model.graph.DateFormat.Relative.Type.MINUTES_LATER
import com.brewthings.app.util.getDaysHoursAndMinutes
import com.brewthings.app.util.isYesterday
import com.brewthings.app.util.startOfDay
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.TimeZone

/**
 * Get the date range that corresponds with the current device sensor screen state.
 *
 * Since the user could either be looking at the graph as a whole or in the context
 * of a selected value, we need to take both into account.
 *
 * The graph overview will always be a date interval, but the selected value can be
 * both a date interval or a specific date depending on the resolution of the samples
 * for the current time span. Still, a date interval where the start and end date is
 * the same is still valid, so we can model all date ranges as a date interval.
 */
fun Instant.toDateFormat(
    timeZone: TimeZone = TimeZone.getDefault(),
    clock: Clock = Clock.system(timeZone.toZoneId()),
): DateFormat {
    // Start and end is the same, to just take the first one.
    val now = Instant.now(clock)
    val startOfDay = startOfDay(now = now, timeZone = timeZone)
    val date = this
    // Diff will have negative values for past dates, positive values for future dates.
    val diff: Duration = Duration.between(date, now)
    val (days, hours, minutes) = diff.getDaysHoursAndMinutes()

    when {
        // Only minutes have passed since now.
        days == 0L && hours == 0L -> when {
            isYesterday(date = date, now = now, timeZone = timeZone) -> return DateFormat.Absolute(
                date = date,
                type = YESTERDAY
            )

            (0 downTo -2L).contains(minutes) -> return DateFormat.Constant(
                type = JUST_NOW
            )

            (-3 downTo -59L).contains(minutes) -> return DateFormat.Relative(
                date = date,
                relativeTo = now,
                type = MINUTES_AGO
            )

            (0 until 59).contains(minutes) -> return DateFormat.Relative(
                date = date,
                relativeTo = now,
                type = MINUTES_LATER
            )
        }
        // Only hours and minutes have passed since now.
        days == 0L -> when {
            isYesterday(date = date, now = now, timeZone = timeZone) -> return DateFormat.Absolute(
                date = date,
                type = YESTERDAY
            )

            hours in -1 downTo -23L -> return DateFormat.Relative(
                date = date,
                relativeTo = now,
                type = HOURS_AGO
            )

            hours in 1 until 2 -> return DateFormat.Relative(
                date = date,
                relativeTo = now,
                type = HOURS_LATER
            )

            hours > 2 -> return DateFormat.Absolute(
                date = date,
                type = MORE_THAN_TWO_HOURS_LATER
            )
        }
        // Days, hours and minutes have passed since now.
        days != 0L -> {
            val diffFromStartOfDay = Duration.between(date, startOfDay)
            val (daysFromStartOfDay, _, _) = diffFromStartOfDay.getDaysHoursAndMinutes()

            when {
                isYesterday(date = date, now = now, timeZone = timeZone) -> return DateFormat.Absolute(
                    date = date,
                    type = YESTERDAY
                )

                daysFromStartOfDay in -1L downTo -4L -> return DateFormat.Absolute(
                    date = date,
                    type = BETWEEN_TWO_AND_SIX_DAYS_AGO
                )

                daysFromStartOfDay <= -5L -> return DateFormat.Absolute(
                    date = date,
                    type = MORE_THAN_SIX_DAYS_AGO
                )

                daysFromStartOfDay in 2L until 6L -> return DateFormat.Absolute(
                    date = date,
                    type = MORE_THAN_TWO_HOURS_LATER
                )

                daysFromStartOfDay > 1 -> return DateFormat.Absolute(
                    date = date,
                    type = MORE_THAN_TWO_HOURS_LATER
                )
            }
        }
    }

    return DateFormat.Constant(type = ERROR)
}
