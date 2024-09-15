package com.brewthings.app.data.domain

import com.brewthings.app.util.datetime.TimeRange

interface BrewData {
    val durationSinceOG: TimeRange?
    val calculatedVelocity: Insight?
    val abv: Insight?
}
