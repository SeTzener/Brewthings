package com.brewthings.app.ui.screens.navigation.legacy

object Destination {
    const val HOME = "home"
    const val SCANNING = "$HOME/${Tab.SCANNING}"
    const val BREWS = "$HOME/${Tab.BREWS}"
    const val PILL_GRAPH = "pill_graph"

    object Tab {
        const val SCANNING = "scanning"
        const val BREWS = "brews"
    }
}
