package com.sky.conversion

import com.sky.conversion.mocks.MockLocalSource
import com.sky.conversion.mocks.MockRemoteSource
import com.sky.conversion.models.*
import com.sky.conversion.repos.RatesRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class RatesRepoTest {
    lateinit var mockRemote: MockRemoteSource
    lateinit var mockLocal: MockLocalSource
    lateinit var ratesRepo: RatesRepo


    @Before
    fun init() {
        mockRemote = MockRemoteSource()
        mockLocal = MockLocalSource()
        ratesRepo = RatesRepo(pref = mockLocal, remoteService = mockRemote)
    }


    @ExperimentalCoroutinesApi
    @Test
    fun test_getRates_success() = runBlockingTest {
        val list = listOf(
            RateAttributes(currencyCode = "MXN$", amount = 20f),
            RateAttributes(currencyCode = "USD$", amount = 400f),
        )
        mockRemote.ratesResponse = RatesSuccess(list)

        val latestRates = ratesRepo.getLatestRates("EUR")

        Assert.assertTrue(latestRates is RatesSuccess)
        if (latestRates is RatesSuccess)
            Assert.assertArrayEquals(list.toTypedArray(), latestRates.list.toTypedArray())

    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getRates_failure() = runBlockingTest {
        val type = "unknown call"
        val info = "sorry unknown call"
        mockRemote.ratesResponse = RatesError(404, type, info)

        val response = ratesRepo.getLatestRates("EUR")
        Assert.assertTrue(response is RatesError)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getRates_for_value() = runBlockingTest {
        val list = listOf(
            RateAttributes(currencyCode = "MXN$", amount = 20f),
            RateAttributes(currencyCode = "USD$", amount = 400f),
        )
        mockRemote.ratesResponse = RatesSuccess(list)

        ratesRepo.getLatestRates("EUR")
        val multiplier = 10f
        val convertedRates = ratesRepo.getConvertedRates(multiplier)
        convertedRates.list.forEachIndexed { i, convertedAttr ->
            val orgVal = list[i]
            Assert.assertEquals(orgVal.amount * multiplier, convertedAttr.amount)
        }
    }
}