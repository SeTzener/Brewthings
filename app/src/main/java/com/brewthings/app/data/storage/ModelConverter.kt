package com.brewthings.app.data.storage

import com.brewthings.app.data.model.ScannedRaptPill
import com.brewthings.app.data.model.ScannedRaptPillData
import com.brewthings.app.util.sanitizeVelocity

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

fun ScannedRaptPillData.toDaoItem(): DaoRaptPillReadings = DaoRaptPillReadings(
    timestamp = timestamp,
    temperature = temperature,
    gravity = gravity,
    gravityVelocity = gravityVelocity,
    x = x,
    y = y,
    z = z,
    battery = battery,
    isOG = null,
    isFG = null,
    isFeeding = null,
)

fun DaoRaptPillReadings.toModelItem(): ModelRaptPillData = ModelRaptPillData(
    timestamp = timestamp,
    temperature = temperature,
    gravity = gravity,
    gravityVelocity = gravityVelocity?.sanitizeVelocity(),
    x = x,
    y = y,
    z = z,
    battery = battery,
    isOG = isOG == true,
    isFG = isFG == true,
    isFeeding = isFeeding == true,
)

fun DaoRaptPillData.toModelItem() = readings.toModelItem()
