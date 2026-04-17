package com.brewthings.app.ui.navigation

import com.brewthings.app.data.domain.DataType
import com.brewthings.app.data.model.Brew

/**
 * A holder for parameters that are passed between screens.
 * Jetpack Compose navigation sucks and I hate Google.
 */
object ParameterHolders {
    object PillGraph {
        var name: String? = null
        var macAddress: String? = null
        var dataType: DataType? = null
    }

    object BrewGraph {
        var brew: Brew? = null
        var dataType: DataType? = null
    }

    object BrewComposition {
        var brew: Brew? = null
    }
}
