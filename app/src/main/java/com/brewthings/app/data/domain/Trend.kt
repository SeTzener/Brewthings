package com.brewthings.app.data.domain

enum class Trend {
    Upwards,
    Downwards,
    Stationary;

    companion object {
        fun get(previous: Float?, current: Float): Trend {
            if (previous == null) return Stationary
            val diff = current - previous
            return when {
                diff > 0 -> Upwards
                diff < 0 -> Downwards
                else -> Stationary
            }
        }
    }
}
