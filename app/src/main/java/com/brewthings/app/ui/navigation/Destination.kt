package com.brewthings.app.ui.navigation

object Destination {
    const val HOME = "home"
    const val ONBOARDING = "onboarding"
    const val PILL_GRAPH = "pill_graph"
    const val BREWS_GRAPH = "brews_graph"
    const val BREW_COMPOSITION = "brew_composition"

    /* Deep links to specific tabs
    const val SCANNING = "$HOME/${Tab.SCAN}"
    const val BREWS = "$HOME/${Tab.BREWS}"*/

    object Tab {
        const val SCAN = "scan"
        const val BREWS = "brews"
    }
}
