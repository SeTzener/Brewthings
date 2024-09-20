package com.brewthings.app.data.repository

import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.RaptPillData
import com.brewthings.app.data.storage.RaptPillReadings
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.Test

class RaptPillRepositoryTest {

    private val repository = RaptPillRepository(mockk(), mockk())
    @Test
    fun `Returns a list with a completed brew`() {
        // happy path
        mockk<RaptPillDao> {
            coEvery { getBrewEdges(any()) }.coAnswers {
                flow {
                    emit(listOf(createOG(1), createFG(2)))
                }
            }
        }

        runBlocking {
            repository.getBrews("macAddressTest")
        }
    }

}

    // happy path senza FG finale

    // 2 OG accollati

    // 2 FG accollati

    // no og or fg

    // un solo punto

private fun createOG(day: Int): RaptPillData {
    return RaptPillData(
        pillId = 1234567890,
        readings = RaptPillReadings(
            timestamp = Instant.parse("2024-09-${day}T00:00:00"),
            temperature = 0.0f,
            gravity = 0.0f,
            gravityVelocity = null,
            x = 0.0f,
            y = 0.0f,
            z = 0.0f,
            battery = 0.0f,
            isOG = true,
            isFG = null
        )
    )
}

private fun createFG(day: Int): RaptPillData {
    return return RaptPillData(
        pillId = 1234567890,
        readings = RaptPillReadings(
            timestamp = Instant.parse("2024-09-${day}T00:00:00"),
            temperature = 0.0f,
            gravity = 0.0f,
            gravityVelocity = null,
            x = 0.0f,
            y = 0.0f,
            z = 0.0f,
            battery = 0.0f,
            isOG = null,
            isFG = true
        )
    )
}