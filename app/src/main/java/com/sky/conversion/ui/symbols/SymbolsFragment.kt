package com.sky.conversion.ui.symbols

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sky.conversion.R
import com.sky.conversion.datasources.local.PreferenceSource
import com.sky.conversion.datasources.remote.RemoteService
import com.sky.conversion.models.SymbolsSuccess
import com.sky.conversion.models.SymbolsError
import com.sky.conversion.models.SymbolsIdleState
import com.sky.conversion.repos.SymbolsRepo
import com.sky.conversion.ui.components.ContentView
import com.sky.conversion.ui.components.SymbolErrorAlertDialog
import com.sky.conversion.ui.components.SymbolList
import com.sky.conversion.utils.BASE_CURRENCY_SYMBOL
import com.sky.conversion.utils.createVmFactory
import com.sky.conversion.utils.getCurrencyApp
import com.sky.conversion.utils.getPreferences

class SymbolsFragment : Fragment() {

    private val viewModel: SymbolsViewModel by viewModels {
        val currencyApp = context.getCurrencyApp() //throws exception
        val pref = context.getPreferences() //throws exception
        val repo = SymbolsRepo(PreferenceSource(pref), RemoteService(), currencyApp.idlingRes)
        createVmFactory(SymbolsViewModel(repo))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val con = container?.context ?: return null
        val composeView = ComposeView(con)
        composeView.setContent {
            ContentView(getString(R.string.currency_code_Name)) {
                ShowPage()
            }
        }
        return composeView
    }


    @Composable
    private fun ShowPage() {
        val collectAsState by viewModel.uiState.collectAsState()
        val response = collectAsState

        when (response) {
            is SymbolsSuccess -> {

                Column(modifier = Modifier.fillMaxSize()) {
                    SearchField()
                    SymbolList(response, onSymbolSelectedClick = ::navigateToConversion)
                }
            }
            is SymbolsError -> SymbolErrorAlertDialog(
                response,
                viewModel::requestCurrencyList,
                viewModel::dialogDismissed
            )
            is SymbolsIdleState -> Text(text = response.message, fontSize = 30.sp)
        }
    }

    private fun navigateToConversion(currencyCode: String) {
        findNavController().navigate(
            R.id.action_selectBaseCurrencyFragment_to_convertCurrencyFragment2,
            bundleOf(BASE_CURRENCY_SYMBOL to currencyCode)
        )
    }

    @Composable
    private fun SearchField() {
        var keyword by remember { mutableStateOf("") }
        if (keyword.isEmpty()) keyword = viewModel.filterKeyword

        TextField(
            value = keyword,
            onValueChange = {
                keyword = it
                viewModel.filter(it)
            }, label = {
                Text(getString(R.string.search))
            }
        )
    }
}