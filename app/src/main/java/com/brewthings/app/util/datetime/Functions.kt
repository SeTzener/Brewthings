package com.brewthings.app.util.datetime

import java.time.format.DateTimeFormatter
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime

fun daysBetweenIgnoringTime(instant1: Instant, instant2: Instant): Int {
    val timeZone = TimeZone.UTC
    // Convert Instants to LocalDate
    val date1 = instant1.toLocalDateTime(timeZone).date
    val date2 = instant2.toLocalDateTime(timeZone).date

    // Calculate the difference in days
    return date1.daysUntil(date2)
}

fun Instant.formatDateTime(
    dateTimePattern: String,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val localDate = toLocalDateTime(timeZone)
    val formatter = DateTimeFormatter.ofPattern(dateTimePattern)
    return formatter.format(localDate.toJavaLocalDateTime())
}

fun Duration.getDaysHoursAndMinutes(): Triple<Long, Long, Long> =
    toComponents { days, hours, minutes, _, _ ->
        Triple(days, hours.toLong(), minutes.toLong())
    }

fun isYesterday(date: Instant, now: Instant, timeZone: TimeZone): Boolean {
    // Adding 24 hours is problematic with daylight saving.
    val offsetDate = date.plus(24.hours)
    val localOffsetDate = offsetDate.toLocalDateTime(timeZone)
    val nowLocalDate = now.toLocalDateTime(timeZone)
    return localOffsetDate.dayOfYear == nowLocalDate.dayOfYear
}

/**
 * Rounds the given milliseconds since epoch [Long] to the closest hour.
 */
fun Long.roundMsToHour(): Long =
    HOUR_IN_MS * msToHours().roundToLong()

private fun Long.msToHours(): Double =
    this / HOUR_IN_MS.toDouble()

internal const val SEC_IN_MS: Long = 1000L
private const val HOUR_IN_SEC = 60 * 60
private const val HOUR_IN_MS = HOUR_IN_SEC * SEC_IN_MS
