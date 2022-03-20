package com.sky.conversion.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sky.conversion.CurrencyApp
import com.sky.conversion.core.APP_CACHE_PREFERENCE

fun appLog(text: String) {
    Log.d("AppLog", text)
}

fun <VM : ViewModel> createVmFactory(vmClass: VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return vmClass as T
        }
    }
}

fun Context?.getPreferences(): SharedPreferences =
    this?.getSharedPreferences(APP_CACHE_PREFERENCE, Context.MODE_PRIVATE)
        ?: throw Exception("cant get Preference")

fun Context?.getCurrencyApp(): CurrencyApp =
    this?.applicationContext as? CurrencyApp ?: throw Exception("could not init App")
