package com.sky.conversion.repos

import androidx.test.espresso.idling.CountingIdlingResource
import com.sky.conversion.models.RateAttributes
import com.sky.conversion.models.RatesSuccess
import com.sky.conversion.models.RatesResponse

class RatesRepo(
    private val pref: ILocalDataSource,
    private val remoteService: IRemoteDataSource,
    private val idlingRes: CountingIdlingResource? = null,
) {
    private var latestRates = listOf<RateAttributes>()

    suspend fun getLatestRates(baseCurrency: String): RatesResponse {
        idlingRes?.increment()

        var ratesList = pref.getRatesList(baseCurrency)

        if (ratesList == null) {
            val response = remoteService.getLatestRates(baseCurrency)
            if (response is RatesSuccess)
                pref.saveRateList(response.list, baseCurrency)
            ratesList = response
        }

        if (ratesList is RatesSuccess) {
            latestRates = ratesList.list
        }

        idlingRes?.decrement()
        return ratesList
    }

    fun getConvertedRates(currencyAmount: Float): RatesSuccess {
        val list = latestRates.map {
            it.copy(amount = it.amount * currencyAmount)
        }
        return RatesSuccess(list)
    }
}