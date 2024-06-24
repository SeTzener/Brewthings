package com.brewthings.app.api

import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Test


class ApiClientE2ETest {

    @Test
    fun `test fetchCatFact E2E`() = runBlocking {
        // Perform the test
        val result = ApiClient().fetchCatFact()

        // Assertions
        assertNotNull(result?.fact)
    }
}
