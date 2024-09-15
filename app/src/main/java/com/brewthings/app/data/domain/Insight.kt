package com.brewthings.app.data.domain

data class Insight(
    val value: Float,
    val deltaFromPrevious: Float? = null,
    val deltaFromOG: Float? = null,
)
