package com.sky.conversion.datasources.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sky.conversion.models.*
import com.sky.conversion.repos.ILocalDataSource
import com.sky.conversion.utils.CACHE_TIME_STAMP
import com.sky.conversion.utils.RATES_LIST_JSON
import com.sky.conversion.utils.SYMBOL_LIST_JSON
import com.sky.conversion.utils.appLog

/**
 * creates local cache using Shared Preference
 * implements functions for save and retrieve currency symbols and rates
 */
class PreferenceSource(private val pref: SharedPreferences) : ILocalDataSource {

    override fun getSymbolList(): SymbolsResponse? {
        val cache = symbolsResponseFromCache()
        return if (isWithin30Mins() && cache != null) cache else null
    }

    override fun getRatesList(baseCurrency: String): RatesResponse? {
        val cache = ratesResponseFromCache(baseCurrency)
        return if (isWithin30Mins() && cache != null) cache else null
    }

    override fun saveSymbolList(symbols: List<SymbolsAttributes>) {
        val currentTime = System.currentTimeMillis()
        val list = Gson().toJson(symbols)
        val edit = pref.edit()
        edit.putLong(CACHE_TIME_STAMP, currentTime)
        edit.putString(SYMBOL_LIST_JSON, list)
        edit.apply()
        appLog(list)
    }

    override fun saveRateList(rates: List<RateAttributes>, baseCurrency: String) {
        val currentTime = System.currentTimeMillis()
        val list = Gson().toJson(rates)
        val edit = pref.edit()
        edit.putLong(CACHE_TIME_STAMP, currentTime)
        edit.putString(getRatesListPrefKey(baseCurrency), list)
        edit.apply()
        appLog(list)
    }

    private fun clear() {
        pref.edit().clear().apply()
        appLog("cache cleared")
    }

    private fun symbolsResponseFromCache(): SymbolsResponse? {
        val symbolList = try {
            getSymbolsOrThrow()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return if (symbolList != null) {
            SymbolsSuccess(symbolList)
        } else {
            clear()
            null
        }
    }

    private fun getSymbolsOrThrow(): List<SymbolsAttributes>? {
        val responseJson =
            pref.getString(SYMBOL_LIST_JSON, null) ?: throw Exception("invalid state of pref")

        val userListType = object : TypeToken<List<SymbolsAttributes>>() {}.type
        val list = Gson().fromJson<List<SymbolsAttributes>>(responseJson, userListType)

        if (list.isEmpty())
            throw Exception("Empty $SYMBOL_LIST_JSON List")
        return list
    }

    private fun ratesResponseFromCache(baseCurrency: String): RatesResponse? {
        val key = getRatesListPrefKey(baseCurrency)

        val symbolList = try {
            getRateListOrThrow(key)
        } catch (e: Exception) {
            pref.edit().remove(key).apply()
            e.printStackTrace()
            null
        }
        if (symbolList != null)
            return RatesSuccess(symbolList)

        return null
    }

    private fun getRateListOrThrow(key: String): List<RateAttributes>? {
        val responseJson = pref.getString(key, null) ?: throw Exception("invalid state of pref")

        val userListType = object : TypeToken<List<RateAttributes>>() {}.type
        val list = Gson().fromJson<List<RateAttributes>>(responseJson, userListType)

        if (list.isEmpty())
            throw Exception("Empty $key List")
        return list
    }

    private fun isWithin30Mins(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastCacheTime = pref.getLong(CACHE_TIME_STAMP, currentTime)
        val timeGapSecs = (currentTime - lastCacheTime) / 1000
        val timeGapMins = (timeGapSecs / 60).toInt()
        return timeGapMins in 1..30
    }

    private fun getRatesListPrefKey(baseCurrency: String) = "$RATES_LIST_JSON$baseCurrency"
}