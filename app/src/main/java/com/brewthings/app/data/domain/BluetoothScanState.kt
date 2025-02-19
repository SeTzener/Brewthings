package com.brewthings.app.data.domain

sealed interface BluetoothScanState {
    data object Idle
    data object InProgress
    data class Unavailable(val reason: Reason) {
        enum class Reason {
            Off,
            TurningOff,
            TurningOn,

            /** Only on Android 11 (API 30) and lower. */
            LocationServicesDisabled,

            /** No status reported yet. **/
            Unknown,
        }
    }
}
