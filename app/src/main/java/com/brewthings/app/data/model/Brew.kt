package com.brewthings.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Brew (
    val og: RaptPillData,
    val fgOrLast: RaptPillData,
    val isCompleted: Boolean,
)