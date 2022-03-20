package com.sky.conversion.data.source.remote

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sky.conversion.core.ACCESS_KEY
import com.sky.conversion.core.BASE_URL
import com.sky.conversion.core.utils.appLog
import com.sky.conversion.data.models.*
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume

/**
 * creates retrofit instance and suspend functions for api calls
 */
class RemoteService {
    private val retrofit: Retrofit
    private val service: CurrencyService

    init {
        val httpClient = OkHttpClient().newBuilder().addInterceptor(::getInterceptor).build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        service = retrofit.create(CurrencyService::class.java)
    }

    private fun getInterceptor(chain: Interceptor.Chain): okhttp3.Response? {
        val originalRequest = chain.request()

        val url = originalRequest.url().newBuilder()
            .addQueryParameter("access_key", ACCESS_KEY)
            .addQueryParameter("format", "1")
            .build()

        return chain.proceed(originalRequest.newBuilder().url(url).build())
    }

    suspend fun getSymbolsResponse(): SymbolsResponse {
        return suspendCancellableCoroutine { continuation ->
            val call = service.getSymbolsJson()
            call.enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    val body = response.body()
                    val isSuccess = body.getAsJsonPrimitive("success").asBoolean

                    appLog(body.toString())

                    if (isSuccess)
                        continuation.resume(getSymbolsData(body))
                    else
                        continuation.resume(getSymbolsError(body))
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable?) {
                    t?.printStackTrace()

                    val errorDetail = SymbolsError(
                        code = -1,
                        type = "Network Error",
                        info = "Couldn't communicate to server, please try again!"
                    )
                    continuation.resume(errorDetail)
                }
            })
        }
    }

    private fun getSymbolsData(body: JsonObject): SymbolsSuccess {
        val entrySet = body.getAsJsonObject("symbols").entrySet()
        val list = entrySet.map {
            SymbolsAttributes(it.key, it.value.asString)
        }
        return SymbolsSuccess(list)
    }

    private fun getSymbolsError(body: JsonObject): SymbolsError {
        val errorJson = body.getAsJsonObject("error")
        return Gson().fromJson(errorJson, SymbolsError::class.java)
    }


    suspend fun getLatestRates(base: String): RatesResponse {
        return suspendCancellableCoroutine { continuation ->
            val call = service.getLatestRatesJson(base)
            call.enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    val body = response.body()
                    val isSuccess = body.getAsJsonPrimitive("success").asBoolean

                    appLog(body.toString())

                    if (isSuccess) {
                        val ratesData = getLatestRatesData(body)
                        continuation.resume(ratesData)
                    } else
                        continuation.resume(getLatestRatesError(body))
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable?) {
                    t?.printStackTrace()

                    val errorDetail = RatesError(
                        code = -1,
                        type = "Network Error",
                        info = "Couldn't communicate to server, please try again!"
                    )
                    continuation.resume(errorDetail)
                }
            })
        }
    }

    private fun getLatestRatesData(body: JsonObject): RatesSuccess {
        val entrySet = body.getAsJsonObject("rates").entrySet()
        val list = entrySet.map {
            RateAttributes(it.key, it.value.asFloat)
        }
        return RatesSuccess(list)
    }

    private fun getLatestRatesError(body: JsonObject): RatesError {
        val errorJson = body.getAsJsonObject("error")
        return Gson().fromJson(errorJson, RatesError::class.java)
    }
}