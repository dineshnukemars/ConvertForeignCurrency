package com.sky.conversion.mocks

import com.sky.conversion.models.*
import com.sky.conversion.repos.ILocalDataSource

class MockLocalSource : ILocalDataSource {
    var symbolList: List<SymbolsAttributes>? = null
    var rateList: List<RateAttributes>? = null
    var timeExpired = false

    override fun saveSymbolList(symbols: List<SymbolsAttributes>) {
        symbolList = symbols
    }

    override fun getSymbolList(): SymbolsResponse? {
        if (timeExpired) return null
        return SymbolsSuccess(symbolList ?: return null)
    }

    override fun saveRateList(rates: List<RateAttributes>, baseCurrency: String) {
        rateList = rates
    }

    override fun getRatesList(baseCurrency: String): RatesResponse? {
        if (timeExpired) return null
        return RatesSuccess(rateList ?: return null)
    }
}