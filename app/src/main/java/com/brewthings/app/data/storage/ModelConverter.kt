package com.brewthings.app.data.storage


fun com.brewthings.app.data.model.RaptPill.toDataItem() = RaptPill(
    macAddress = macAddress,
    name = name,
)

fun RaptPillData.toModelItem() = com.brewthings.app.data.model.RaptPillData(
    timestamp = readings.timestamp,
    temperature = readings.temperature,
    gravity = readings.gravity,
    gravityVelocity = readings.gravityVelocity?.let { -1 * it }, // Invert the sign, to make it more intuitive.
    x = readings.x,
    y = readings.y,
    z = readings.z,
    battery = readings.battery,
    isOG = readings.isOG == true,
    isFG = readings.isFG == true,
)
