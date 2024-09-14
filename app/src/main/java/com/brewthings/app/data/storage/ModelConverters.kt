package com.brewthings.app.data.storage


fun com.brewthings.app.data.model.RaptPill.toDataItem() = RaptPill(
    macAddress = macAddress,
    name = name,
)

fun RaptPillData.toModelItem() = com.brewthings.app.data.model.RaptPillData(
    timestamp = readings.timestamp,
    temperature = readings.temperature,
    gravity = readings.gravity,
    gravityVelocity = readings.gravityVelocity,
    x = readings.x,
    y = readings.y,
    z = readings.z,
    battery = readings.battery,
    isOG = readings.isOG,
    isFG = readings.isFG
)
