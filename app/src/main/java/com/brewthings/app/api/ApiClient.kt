package com.brewthings.app.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ApiClient() {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    suspend fun fetchCatFact(): CatFact? {
        return try {
            val response: HttpResponse = client.get("https://catfact.ninja/fact")
            if (response.status == HttpStatusCode.OK) {
                val bodyText = response.bodyAsText()
                Json.decodeFromString<CatFact>(bodyText)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error fetching cat fact: ${e.message}")
            null
        }
    }

}

@Serializable
data class CatFact(val fact: String, val length: Int)