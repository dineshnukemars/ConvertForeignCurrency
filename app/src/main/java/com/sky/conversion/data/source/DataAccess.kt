package com.sky.conversion.data.source

import androidx.test.espresso.idling.CountingIdlingResource
import com.sky.conversion.data.models.RatesResponse
import com.sky.conversion.data.models.RatesSuccess
import com.sky.conversion.data.models.SymbolsResponse
import com.sky.conversion.data.models.SymbolsSuccess
import com.sky.conversion.data.source.local.PreferenceSource
import com.sky.conversion.data.source.remote.RemoteService


suspend fun callLatestRates(
    pref: PreferenceSource,
    remoteService: RemoteService,
    idlingRes: CountingIdlingResource? = null,
    baseCurrency: String
): RatesResponse {
    idlingRes?.increment()

    var ratesList = pref.getRatesList(baseCurrency)

    if (ratesList == null) {
        val response = remoteService.getLatestRates(baseCurrency)

        if (response is RatesSuccess)
            pref.saveRateList(response.list, baseCurrency)

        ratesList = response
    }

    idlingRes?.decrement()
    return ratesList
}

suspend fun callSymbols(
    pref: PreferenceSource,
    remoteService: RemoteService,
    idlingRes: CountingIdlingResource? = null
): SymbolsResponse {
    idlingRes?.increment()

    var symbolList = pref.getSymbolList()

    if (symbolList == null) {
        val symbolsResponse = remoteService.getSymbolsResponse()

        if (symbolsResponse is SymbolsSuccess)
            pref.saveSymbolList(symbolsResponse.list)

        symbolList = symbolsResponse
    }

    idlingRes?.decrement()
    return symbolList
}