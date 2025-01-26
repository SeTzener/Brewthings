package com.brewthings.app.ui.screen.graph.insights

import com.brewthings.app.data.model.RaptPillData
import com.brewthings.app.ui.component.insights.toInsights
import com.brewthings.app.utils.shouldMatchSnapshot
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

class InsightsConverterTest {
    @Test
    fun `Conversion should return the correct ABV data`(testInfo: TestInfo) {
        val raptPillData = createRaptPillData(Feedings.NO_FEEDING)
        raptPillData.toInsights() shouldMatchSnapshot testInfo
    }

    @Test
    fun `Conversion should return the correct ABV data with just one feeding`(testInfo: TestInfo) {
        val raptPillData = createRaptPillData(Feedings.JUST_ONCE)
        raptPillData.toInsights() shouldMatchSnapshot testInfo
    }

    @Test
    fun `Conversion should return the correct ABV data with just more feeding`(testInfo: TestInfo) {
        val raptPillData = createRaptPillData(Feedings.TWICE)
        raptPillData.toInsights() shouldMatchSnapshot testInfo
    }

    private fun createRaptPillData(feedings: Feedings): List<RaptPillData> {
        var gravity = 1.110f
        val result = mutableListOf<RaptPillData>()
        for (i: Int in 1..9) {
            gravity -= 0.010f
            result.add(
                RaptPillData(
                    timestamp = Instant.parse("2024-09-0${i}T00:00:00Z"),
                    temperature = 20f,
                    gravity = when (feedings) {
                        Feedings.NO_FEEDING -> gravity
                        Feedings.JUST_ONCE -> if (i % 5 == 0) { gravity + 0.020f } else { gravity }
                        Feedings.TWICE -> when (i) {
                            4 -> gravity + 0.020f
                            8 -> gravity + 0.040f
                            else -> gravity
                        }
                    },
                    battery = 100.0f,
                    gravityVelocity = 1f,
                    x = 100f,
                    y = 200f,
                    z = 300f,
                    isOG = i == 1,
                    isFG = false,
                    isFeeding = when (feedings) {
                        Feedings.NO_FEEDING -> false
                        Feedings.JUST_ONCE -> i % 5 == 0
                        Feedings.TWICE -> i % 4 == 0
                    },
                ),
            )
        }
        return result
    }

    private enum class Feedings {
        NO_FEEDING,
        JUST_ONCE,
        TWICE,
    }
}
