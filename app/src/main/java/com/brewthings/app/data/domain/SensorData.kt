package com.brewthings.app.data.domain

import kotlinx.datetime.Instant

interface SensorData<T> {
    val timestamp: Instant
    val temperature: T
    val gravity: T
    val gravityVelocity: T?
    val tilt: T
    val battery: T
}
