package com.sky.conversion.ui.conversion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sky.conversion.R
import com.sky.conversion.core.BASE_CURRENCY_SYMBOL
import com.sky.conversion.core.utils.createVmFactory
import com.sky.conversion.core.utils.getCurrencyApp
import com.sky.conversion.core.utils.getPreferences
import com.sky.conversion.data.models.RatesError
import com.sky.conversion.data.models.RatesIdleState
import com.sky.conversion.data.models.RatesSuccess
import com.sky.conversion.data.source.local.PreferenceSource
import com.sky.conversion.data.source.remote.RemoteService
import com.sky.conversion.ui.base.components.ContentView
import com.sky.conversion.ui.base.components.LatestRatesList
import com.sky.conversion.ui.base.components.RatesErrorAlertDialog
import com.sky.conversion.usecase.CurrencyUseCase

class ConversionFragment : Fragment() {

    private val viewModel: ConversionViewModel by viewModels {

        val currencyApp = context.getCurrencyApp() //throws exception
        val pref = context.getPreferences() //throws exception

        createVmFactory(
            ConversionViewModel(
                CurrencyUseCase(
                    pref = PreferenceSource(pref),
                    remoteService = RemoteService(),
                    idlingRes = currencyApp.idlingRes
                )
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = container?.context ?: return null
        viewModel.baseCurrency = (arguments?.getString(BASE_CURRENCY_SYMBOL) ?: return null)
        viewModel.requestLatestRates()

        return ComposeView(context).apply {
            setContent {
                ContentView(getString(R.string.conversion)) {
                    ShowPage()
                }
            }
        }
    }

    @Composable
    private fun ShowPage() {

        val observeAsState = viewModel.uiState.collectAsState()
        val response = observeAsState.value

        when (response) {
            is RatesSuccess -> {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                ) {
                    ConversionField()
                    LatestRatesList(response)
                }
            }
            is RatesError -> RatesErrorAlertDialog(
                response = response,
                onRetryClick = viewModel::requestLatestRates,
                onDismissClick = findNavController()::popBackStack
            )
            is RatesIdleState -> Text(text = response.message, fontSize = 30.sp)
        }

    }

    @Composable
    private fun ConversionField() {
        Row {
            var amt by remember { mutableStateOf("1") }

            TextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = amt,
                onValueChange = {
                    amt = it
                    viewModel.requestConvertedList(amt)
                }, label = {
                    Text("Enter the Amount")
                }
            )
        }
    }
}