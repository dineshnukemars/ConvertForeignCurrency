package com.sky.conversion.models


sealed class SymbolsResponse

data class SymbolsSuccess(
    val list: List<SymbolsAttributes>,
) : SymbolsResponse()

data class SymbolsAttributes(
    val currencyCode: String,
    val countryName: String,
)

data class SymbolsError(
    val code: Int,
    val type: String,
    val info: String,
) : SymbolsResponse()

data class SymbolsIdleState(
    val message: String = "Loading....",
) : SymbolsResponse()
