package com.brewthings.app.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class Insight(
    val value: Float,
    val deltaFromPrevious: Float? = null,
    val deltaFromOG: Float? = null,
)
