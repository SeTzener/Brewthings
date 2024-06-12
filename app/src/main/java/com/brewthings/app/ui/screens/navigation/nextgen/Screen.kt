package com.brewthings.app.ui.screens.navigation.nextgen

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Scanning
    @Serializable
    data class Graph(val name: String?, val macAddress: String)
}
