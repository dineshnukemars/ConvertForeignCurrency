package com.sky.conversion.mocks

import com.sky.conversion.models.*
import com.sky.conversion.repos.IRemoteDataSource

class MockRemoteSource : IRemoteDataSource {
    var symbolsResponse: SymbolsResponse = SymbolsIdleState()
    var ratesResponse: RatesResponse = RatesIdleState()

    override suspend fun getSymbolsResponse(): SymbolsResponse {
        return symbolsResponse
    }

    override suspend fun getLatestRates(base: String): RatesResponse {
        return ratesResponse
    }
}