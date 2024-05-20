package com.brewthings.app.data.storage

import java.time.Instant

data class RaptPillReadings(
    val timestamp: Instant,
    val temperature: Float,
    val gravity: Float,
    val x: Float,
    val y: Float,
    val z: Float,
    val battery: Float
)
