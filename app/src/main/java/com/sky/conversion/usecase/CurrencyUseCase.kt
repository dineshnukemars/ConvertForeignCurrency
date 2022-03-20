package com.sky.conversion.usecase

import androidx.test.espresso.idling.CountingIdlingResource
import com.sky.conversion.data.source.callLatestRates
import com.sky.conversion.data.source.callSymbols
import com.sky.conversion.data.models.*
import com.sky.conversion.data.source.local.PreferenceSource
import com.sky.conversion.data.source.remote.RemoteService

class CurrencyUseCase(
    private val pref: PreferenceSource,
    private val remoteService: RemoteService,
    private val idlingRes: CountingIdlingResource? = null,
) {
    var symbolsResponse: SymbolsResponse = SymbolsIdleState()
        private set
    var ratesResponse: RatesResponse = RatesIdleState()
        private set

    suspend fun getSymbolsResponse(): SymbolsResponse {
        symbolsResponse = callSymbols(pref, remoteService, idlingRes)
        return symbolsResponse
    }

    suspend fun getLatestRates(baseCurrency: String): RatesResponse {
        ratesResponse = callLatestRates(pref, remoteService, idlingRes, baseCurrency)
        return ratesResponse
    }
}