package com.viktorger.fineweather.app

import android.app.Application
import com.viktorger.fineweather.di.AppComponent
import com.viktorger.fineweather.di.DaggerAppComponent
import dagger.internal.DaggerGenerated

class MyApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}