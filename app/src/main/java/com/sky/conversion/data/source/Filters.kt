package com.sky.conversion.data.source

import com.sky.conversion.data.models.*
import com.sky.conversion.usecase.CurrencyUseCase


fun CurrencyUseCase.filterSymbols(keyword: String): SymbolsResponse {
    val response = symbolsResponse

    return if (response is SymbolsSuccess) {

        if (keyword.isBlank())
            return SymbolsSuccess(response.list)

        val filter = response.list.filter {
            it.currencyCode.contains(keyword, ignoreCase = true)
        }
        SymbolsSuccess(filter)
    } else {
        SymbolsError(404, "no symbols found", "the response is error")
    }
}

fun CurrencyUseCase.getConvertedRates(currencyAmount: Float): RatesResponse {
    val response = ratesResponse

    return if (response is RatesSuccess) {

        val list = response.list.map {
            it.copy(amount = it.amount * currencyAmount)
        }
        return RatesSuccess(list)
    } else {
        RatesError(404, "no symbols found", "the response is error")
    }
}