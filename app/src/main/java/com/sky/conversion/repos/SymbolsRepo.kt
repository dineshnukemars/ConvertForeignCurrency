package com.sky.conversion.repos

import androidx.test.espresso.idling.CountingIdlingResource
import com.sky.conversion.models.SymbolsAttributes
import com.sky.conversion.models.SymbolsSuccess
import com.sky.conversion.models.SymbolsResponse


class SymbolsRepo(
    private val pref: ILocalDataSource,
    private val remoteService: IRemoteDataSource,
    private val idlingRes: CountingIdlingResource? = null,
) {
    private var list = listOf<SymbolsAttributes>()

    suspend fun getSymbolsResponse(): SymbolsResponse {
        idlingRes?.increment()

        var symbolList = pref.getSymbolList()

        if (symbolList == null) {
            val symbolsResponse = remoteService.getSymbolsResponse()
            if (symbolsResponse is SymbolsSuccess)
                pref.saveSymbolList(symbolsResponse.list)
            symbolList = symbolsResponse
        }

        if (symbolList is SymbolsSuccess) {
            list = symbolList.list
        }

        idlingRes?.decrement()
        return symbolList
    }

    fun filter(keyword: String): SymbolsSuccess {
        if (keyword.isBlank()) return SymbolsSuccess(list)
        val filter = list.filter { it.currencyCode.contains(keyword, ignoreCase = true) }
        return SymbolsSuccess(filter)
    }
}