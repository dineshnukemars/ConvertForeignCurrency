package com.sky.conversion.ui.conversion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.conversion.data.models.RatesIdleState
import com.sky.conversion.data.models.RatesResponse
import com.sky.conversion.data.source.getConvertedRates
import com.sky.conversion.usecase.CurrencyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ConversionViewModel(private val useCase: CurrencyUseCase) : ViewModel() {

    var baseCurrency: String = ""

    private val _uiState = MutableStateFlow<RatesResponse>(RatesIdleState())
    val uiState = _uiState.asStateFlow()

    /**
     * this will request latest rates for base currency
     * emits RatesError or RatesData
     */
    fun requestLatestRates() {
        viewModelScope.launch {
            _uiState.emit(RatesIdleState())
            _uiState.emit(useCase.getLatestRates(baseCurrency))
        }
    }

    /**
     * this will convert the amount entered to all the available currencies
     * emits RatesData
     */
    fun requestConvertedList(currencyAmount: String) {
        try {
            val amt = currencyAmount.toFloat()
            _uiState.tryEmit(useCase.getConvertedRates(amt))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
