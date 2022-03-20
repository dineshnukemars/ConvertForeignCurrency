package com.sky.conversion.data.models


sealed class RatesResponse

data class RatesSuccess(
    val list: List<RateAttributes>,
) : RatesResponse()

data class RatesError(
    val code: Int,
    val type: String,
    val info: String?,
) : RatesResponse()

data class RateAttributes(
    val currencyCode: String,
    val amount: Float,
){
    fun getAmountRounded(): String {
        return String.format("%.2f", amount);
    }
}

data class RatesIdleState(
    val message: String = "Loading....",
) : RatesResponse()
