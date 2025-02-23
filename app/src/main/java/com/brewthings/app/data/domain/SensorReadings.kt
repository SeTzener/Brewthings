package com.brewthings.app.data.domain

interface SensorReadings : SensorData<Float> {
    companion object {
        fun compare(a: SensorReadings?, b: SensorReadings?): Int = compareValuesBy(a, b,
            { it?.timestamp },
            { it?.temperature },
            { it?.gravity },
            { it?.tilt },
            { it?.battery },
            { it?.gravityVelocity }
        )
    }
}
