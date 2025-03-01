package com.brewthings.app.data.storage

import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.model.ScannedRaptPillData

typealias ModelRaptPill = com.brewthings.app.data.model.RaptPill
typealias ModelRaptPillData = com.brewthings.app.data.model.RaptPillData

typealias DaoRaptPill = RaptPill
typealias DaoRaptPillData = RaptPillData
typealias DaoRaptPillReadings = RaptPillReadings

fun ModelRaptPill.toDaoItem(): DaoRaptPill = DaoRaptPill(
    macAddress = macAddress,
    name = name,
)

fun ScannedRaptPill.toDaoItem(): DaoRaptPill = DaoRaptPill(
    macAddress = macAddress,
    name = name,
)

fun ScannedRaptPillData.toDaoItem(
    isOg: Boolean? = null,
    isFg: Boolean? = null,
    isFeeding: Boolean? = null,
): DaoRaptPillReadings = DaoRaptPillReadings(
    timestamp = timestamp,
    temperature = temperature,
    gravity = gravity,
    gravityVelocity = gravityVelocity,
    x = x,
    y = y,
    z = z,
    battery = battery,
    isOG = isOg?.takeIf { it },
    isFG = isFg?.takeIf { it },
    isFeeding = isFeeding?.takeIf { it },
)

fun DaoRaptPillReadings.toModelItem(): ModelRaptPillData = ModelRaptPillData(
    timestamp = timestamp,
    temperature = temperature,
    gravity = gravity,
    rawVelocity = gravityVelocity,
    x = x,
    y = y,
    z = z,
    battery = battery,
    isOG = isOG == true,
    isFG = isFG == true,
    isFeeding = isFeeding == true,
)

fun DaoRaptPillData.toModelItem() = readings.toModelItem()
