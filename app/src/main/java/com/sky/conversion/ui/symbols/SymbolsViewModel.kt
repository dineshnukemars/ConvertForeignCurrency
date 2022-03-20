package com.sky.conversion.ui.symbols

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.conversion.data.models.SymbolsIdleState
import com.sky.conversion.data.models.SymbolsResponse
import com.sky.conversion.data.source.filterSymbols
import com.sky.conversion.usecase.CurrencyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SymbolsViewModel(private val useCase: CurrencyUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<SymbolsResponse>(SymbolsIdleState())
    val uiState = _uiState.asStateFlow()

    var filterKeyword = ""

    init {
        requestCurrencyList()
    }

    /**
     * request's currency code list and emits to uiState flow
     */
    fun requestCurrencyList() {
        viewModelScope.launch {
            _uiState.emit(SymbolsIdleState())
            _uiState.emit(useCase.getSymbolsResponse())
        }
    }

    /**
     * this will emit Idle state to dismiss dialog
     */
    fun dialogDismissed() {
        _uiState.tryEmit(SymbolsIdleState(message = "No data.. come back later"))
    }

    /**
     * this filter the symbol and returns filtered list
     */
    fun filter(keyword: String) {
        filterKeyword = keyword
        _uiState.tryEmit(useCase.filterSymbols(keyword))
    }
}
