package com.sky.conversion.data.source.remote

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * retrofit interface for Currency conversion API's
 */
interface CurrencyService {
    //http://data.fixer.io/api/symbols?access_key=a2c37cc1a0f3bc78015e7191e45ecfcc&format=1
    @GET("symbols?format=1")
    fun getSymbolsJson(): Call<JsonObject>

    //http://data.fixer.io/api/latest?format=1&access_key=a2c37cc1a0f3bc78015e7191e45ecfcc&base=EUR
    @GET("latest?format=1")
    fun getLatestRatesJson(@Query("base") base: String): Call<JsonObject>
}