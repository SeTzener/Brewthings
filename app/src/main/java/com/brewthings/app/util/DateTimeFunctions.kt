package com.brewthings.app.util

import java.time.Duration
import java.time.Instant
import java.util.TimeZone
import kotlin.math.absoluteValue
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

fun startOfDay(now: Instant, timeZone: TimeZone): Instant = now
    .atZone(timeZone.toZoneId())
    .toLocalDate()
    .atStartOfDay(timeZone.toZoneId())
    .toInstant()

fun Duration.getDaysHoursAndMinutes(): Triple<Long, Long, Long> =
    Triple(toDays(), toHours() % 24, toMinutes() % 60)

fun isYesterday(date: Instant, now: Instant, timeZone: TimeZone): Boolean {
    // Adding 24 hours is problematic with daylight saving.
    val offsetDate = date.plus(24.hours.toJavaDuration())
    val localOffsetDate = offsetDate.atZone(timeZone.toZoneId()).toLocalDateTime()
    val nowLocalDate = now.atZone(timeZone.toZoneId()).toLocalDateTime()
    return localOffsetDate.dayOfYear == nowLocalDate.dayOfYear
}

fun Instant.isWithin(instant: Instant, duration: Duration): Boolean {
    val diff = (instant.epochSecond - epochSecond).absoluteValue
    return diff <= duration.seconds
}

/**
 * Rounds the given milliseconds since epoch [Long] to the closest hour.
 */
fun Long.roundMsToHour(): Long =
    HOUR_IN_MS * msToHours().roundToLong()

/**
 * Rounds the given seconds since epoch [Long] to the closest hour.
 */
fun Long.roundSecToHour(): Long =
    HOUR_IN_SEC * secondsToHours().roundToLong()

private fun Long.msToHours(): Double =
    this / HOUR_IN_MS.toDouble()

private fun Long.secondsToHours(): Double =
    this / HOUR_IN_SEC.toDouble()

internal const val SEC_IN_MS: Long = 1000L
private const val HOUR_IN_SEC = 60 * 60
private const val HOUR_IN_MS = HOUR_IN_SEC * SEC_IN_MS
