package com.brewthings.app.data.repository

import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.RaptPillData
import com.brewthings.app.data.storage.RaptPillReadings
import com.brewthings.app.utils.shouldMatchSnapshot
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

class RaptPillRepositoryTest {

    private val dao = mockk<RaptPillDao>()
    private val repository = RaptPillRepository(scanner = mockk(), dao = dao)

    @Test
    fun `Returns a list with a complete brew`(testInfo: TestInfo) {
        // happy path
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            flow {
                emit(listOf(createOG("01"), createFG("02")))
            }
        }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

    @Test
    fun `Returns a list with 3 complete brews`(testInfo: TestInfo) {
        // happy path
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            flow {
                emit(
                    listOf(
                        createOG("01"), createFG("02"),
                        createOG("03"), createFG("04"),
                        createOG("05"), createFG("06")
                    )
                )
            }
        }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

}

// happy path senza FG finale

// 2 OG accollati

// 2 FG accollati

// no og or fg

// un solo punto

private fun createOG(day: String): RaptPillData {
    return RaptPillData(
        pillId = 1234567890,
        readings = RaptPillReadings(
            timestamp = Instant.parse("2024-09-${day}T00:00:00Z"),
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

private fun createFG(day: String): RaptPillData {
    return return RaptPillData(
        pillId = 1234567890,
        readings = RaptPillReadings(
            timestamp = Instant.parse("2024-09-${day}T00:00:00Z"),
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