package com.brewthings.app.utils

import com.karumi.kotlinsnapshot.core.SerializationModule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiSerializer : SerializationModule {
    private val adapter: JsonAdapter<Any> = defaultMoshiBuilder {}
        .build()
        .adapter(Any::class.java).indent("  ")

    override fun serialize(value: Any?): String = when (value) {
        is Pair<*, *> -> serialize(listOf(value.first, value.second))
        else -> adapter.toJson(value)
    }
}

fun defaultMoshiBuilder(scope: Moshi.Builder.() -> Unit = {}): Moshi.Builder {
    val builder = Moshi.Builder()
    scope(builder)
    return builder
        .add(KotlinJsonAdapterFactory()) // because of presedence this should be added last
        ?: throw RuntimeException("failed to create default moshi builder")
}