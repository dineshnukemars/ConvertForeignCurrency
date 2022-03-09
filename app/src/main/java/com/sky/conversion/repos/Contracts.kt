package com.sky.conversion.repos

import com.sky.conversion.models.RateAttributes
import com.sky.conversion.models.RatesResponse
import com.sky.conversion.models.SymbolsAttributes
import com.sky.conversion.models.SymbolsResponse

interface IRemoteDataSource {
    /**
     * this will connect to the remote source and get currency symbol list
     */
    suspend fun getSymbolsResponse(): SymbolsResponse
    /**
     * this will connect to the remote source and get latest conversion rate list
     */
    suspend fun getLatestRates(base: String): RatesResponse
}

interface ILocalDataSource {

    /**
     * save's the list to local cache
     */
    fun saveSymbolList(symbols: List<SymbolsAttributes>)
    /**
     * get the list from local cache or null if any exception/empty
     */
    fun getSymbolList(): SymbolsResponse?

    /**
     * save's the list to local cache with base currency as part of key
     */
    fun saveRateList(rates: List<RateAttributes>, baseCurrency: String)
    /**
     * get the list from local cache for base currency or null if any exception/empty
     */
    fun getRatesList(baseCurrency: String): RatesResponse?
}