package com.brewthings.app.ui.screens.navigation.legacy

/**
 * A holder for parameters that are passed between screens.
 * Jetpack Compose navigation sucks and I hate Google.
 */
object ParameterHolder {
    object Graph {
        var name: String? = null
        var macAddress: String? = null
    }
}
