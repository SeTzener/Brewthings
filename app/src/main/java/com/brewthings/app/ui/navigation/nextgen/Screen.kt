package com.brewthings.app.ui.navigation.nextgen

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Scanning

    @Serializable
    data class Graph(val name: String?, val macAddress: String)
}
