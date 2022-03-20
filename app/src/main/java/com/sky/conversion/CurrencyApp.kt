package com.sky.conversion

import android.app.Application
import androidx.test.espresso.idling.CountingIdlingResource
import com.sky.conversion.core.CONVERSION_APP

class CurrencyApp : Application() {

    lateinit var idlingRes: CountingIdlingResource

    override fun onCreate() {
        super.onCreate()
        idlingRes = CountingIdlingResource(CONVERSION_APP)
    }
}