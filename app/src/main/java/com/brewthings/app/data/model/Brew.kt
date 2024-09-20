package com.brewthings.app.data.model

data class Brew (
    val og: RaptPillData,
    val fgOrLast: RaptPillData,
    val isCompleted: Boolean,
)