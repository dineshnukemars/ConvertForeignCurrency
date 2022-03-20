package com.sky.conversion.ui.base.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sky.conversion.data.models.RatesSuccess
import com.sky.conversion.data.models.RatesError
import com.sky.conversion.data.models.SymbolsSuccess
import com.sky.conversion.data.models.SymbolsError
import com.sky.conversion.ui.base.theme.MyApplicationTheme
import com.sky.conversion.core.LIST_VIEW
import java.util.*

/**
 * gives structure and theme for the content view
 */
@Composable
fun ContentView(title: String, content: @Composable () -> Unit) {
    MyApplicationTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(text = title)
                })
            },
            content = { content() },
        )
    }
}

/**
 * creates lazy list for the symbol list
 */
@Composable
fun SymbolList(response: SymbolsSuccess, onSymbolSelectedClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .semantics { contentDescription = LIST_VIEW }) {

        val symbolList = response.list
        items(symbolList.size) { index ->
            val attributes = symbolList[index]
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable { onSymbolSelectedClick(attributes.currencyCode) }
            ) {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = attributes.currencyCode
                )
                Text(modifier = Modifier.padding(5.dp), text = attributes.countryName)
            }
        }
    }
}

/**
 * creates lazy list for the Latest rates
 */
@Composable
fun LatestRatesList(response: RatesSuccess) {

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .semantics { contentDescription = LIST_VIEW }) {
        val symbolList = response.list
        items(symbolList.size) { index ->
            val attributes = symbolList[index]
            Row(modifier = Modifier.padding(10.dp)) {

                val currency = Currency.getInstance(attributes.currencyCode)
                Text(
                    modifier = Modifier
                        .padding(5.dp)
                        .width(40.dp),
                    text = currency.symbol
                )

                Text(
                    modifier = Modifier.padding(5.dp),
                    text = attributes.getAmountRounded()
                )
            }
        }
    }
}

/**
 * creates alert dialog for SymbolError
 */
@Composable
fun SymbolErrorAlertDialog(
    response: SymbolsError,
    onRetryClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        title = { Text(text = response.type) },
        text = { Text(text = response.info) },
        onDismissRequest = { onDismissClick() },
        confirmButton = {
            TextButton(onClick = onRetryClick) {
                Text(text = "Retry")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissClick() }) {
                Text(text = "Cancel")
            }
        },
    )
}

/**
 * creates alert dialog for RatesError
 */
@Composable
fun RatesErrorAlertDialog(
    response: RatesError,
    onRetryClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        title = { Text(text = response.type) },
        text = { Text(text = response.info ?: "") },
        onDismissRequest = { onDismissClick() },
        confirmButton = {
            TextButton(onClick = onRetryClick) {
                Text(text = "Retry")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissClick() }) {
                Text(text = "Cancel")
            }
        },
    )
}