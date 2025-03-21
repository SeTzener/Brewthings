package com.brewthings.app.data.repository

import com.brewthings.app.data.storage.RaptPillDao
import com.brewthings.app.data.storage.RaptPillData
import com.brewthings.app.data.storage.RaptPillReadings
import com.brewthings.app.utils.shouldMatchSnapshot
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

class BrewsRepositoryTest {
    private val dao = mockk<RaptPillDao>()
    private val repository = BrewsRepository(dao = dao)

    @Test
    fun `The user has a single complete brew`(testInfo: TestInfo) {
        // happy path
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            listOf(createOG("01"), createFG("02"))
        }
        coEvery { dao.getBrewData(any(), any(), any()) }.coAnswers { emptyList() }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

    @Test
    fun `The user has 3 complete brews`(testInfo: TestInfo) {
        // happy path
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            listOf(
                createOG("01"),
                createFG("02"),
                createOG("03"),
                createFG("04"),
                createOG("05"),
                createFG("06"),
            )
        }
        coEvery { dao.getBrewData(any(), any(), any()) }.coAnswers { emptyList() }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

    @Test
    fun `The user has no brews`(testInfo: TestInfo) {
        // Common for a new user
        coEvery { dao.getBrewEdges(any()) }.coAnswers { emptyList() }
        coEvery { dao.getBrewData(any(), any(), any()) }.coAnswers { emptyList() }

        runBlocking {
            repository.getBrews("macAddressTest") shouldBe emptyList()
        }
    }

    @Test
    fun `The user has an unfinished brew`(testInfo: TestInfo) {
        // Happy path
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            listOf(
                createOG("01"),
            )
        }
        coEvery { dao.getLastMeasurement(any()) }.coAnswers {
            flow {
                emit(
                    createMeasurement("02"),
                )
            }
        }
        coEvery { dao.getBrewData(any(), any(), any()) }.coAnswers { emptyList() }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

    @Test
    fun `The user set two OGs and one FG`(testInfo: TestInfo) {
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            listOf(
                createOG("01"),
                createOG("02"),
                createFG("03"),
            )
        }
        coEvery { dao.getBrewData(any(), any(), any()) }.coAnswers { emptyList() }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

    @Test
    fun `The user set many OGs and one FG`(testInfo: TestInfo) {
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            listOf(
                createOG("01"),
                createOG("02"),
                createOG("03"),
                createOG("04"),
                createFG("05"),
            )
        }
        coEvery { dao.getBrewData(any(), any(), any()) }.coAnswers { emptyList() }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

    @Test
    fun `The user set one OG and two FG`(testInfo: TestInfo) {
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            listOf(
                createOG("01"),
                createFG("02"),
                createFG("03"),
            )
        }
        coEvery { dao.getBrewData(any(), any(), any()) }.coAnswers { emptyList() }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

    @Test
    fun `The user set one OG and many FG`(testInfo: TestInfo) {
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            listOf(
                createOG("01"),
                createFG("02"),
                createFG("03"),
                createFG("04"),
                createFG("05"),
            )
        }
        coEvery { dao.getBrewData(any(), any(), any()) }.coAnswers { emptyList() }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

    @Test
    fun `The user set the brew's end but not the beginning`(testInfo: TestInfo) {
        coEvery { dao.getBrewEdges(any()) }.coAnswers {
            listOf(
                createFG("02"),
            )
        }
        coEvery { dao.getFirstMeasurement(any()) }.coAnswers {
            flow {
                emit(
                    createMeasurement("01"),
                )
            }
        }
        coEvery { dao.getBrewData(any(), any(), any()) }.coAnswers { emptyList() }

        runBlocking {
            repository.getBrews("macAddressTest") shouldMatchSnapshot testInfo
        }
    }

    private fun createOG(day: String): RaptPillData =
        RaptPillData(
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
                isFG = null,
                isFeeding = null,
            ),
        )

    private fun createFG(day: String): RaptPillData =
        RaptPillData(
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
                isFG = true,
                isFeeding = null,
            ),
        )

    private fun createMeasurement(day: String): RaptPillData =
        RaptPillData(
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
                isFG = null,
                isFeeding = null,
            ),
        )
}
