package com.sky.conversion

import com.sky.conversion.mocks.MockLocalSource
import com.sky.conversion.mocks.MockRemoteSource
import com.sky.conversion.models.SymbolsAttributes
import com.sky.conversion.models.SymbolsSuccess
import com.sky.conversion.models.SymbolsError
import com.sky.conversion.repos.SymbolsRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SymbolsRepoTest {
    lateinit var mockRemote: MockRemoteSource
    lateinit var mockLocal: MockLocalSource
    lateinit var symbolsRepo: SymbolsRepo


    @Before
    fun init() {
        mockRemote = MockRemoteSource()
        mockLocal = MockLocalSource()
        symbolsRepo = SymbolsRepo(pref = mockLocal, remoteService = mockRemote)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getSymbols_success() = runBlockingTest {
        val list = listOf(
            SymbolsAttributes("MXN$", "MEXICO"),
            SymbolsAttributes("USD$", "USA"),
        )
        mockRemote.symbolsResponse = SymbolsSuccess(list)

        val symbolsResponse = symbolsRepo.getSymbolsResponse()

        Assert.assertTrue(symbolsResponse is SymbolsSuccess)
        if (symbolsResponse is SymbolsSuccess)
            Assert.assertArrayEquals(list.toTypedArray(), symbolsResponse.list.toTypedArray())

    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getSymbols_filter() = runBlockingTest {
        val list = listOf(
            SymbolsAttributes("MXN$", "MEXICO"),
            SymbolsAttributes("USD$", "USA"),
        )
        mockRemote.symbolsResponse = SymbolsSuccess(list)
        symbolsRepo.getSymbolsResponse()

        val data = symbolsRepo.filter("MXN")

        Assert.assertTrue(data.list.count() == 1)
        Assert.assertTrue(data.list[0].countryName == "MEXICO")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_getSymbols_Failure() = runBlockingTest {
        val type = "unknown call"
        val info = "sorry unknown call"
        mockRemote.symbolsResponse = SymbolsError(404, type, info)

        val symbolsResponse = symbolsRepo.getSymbolsResponse()

        Assert.assertTrue(symbolsResponse is SymbolsError)
    }

    @After
    fun destroy() {

    }
}