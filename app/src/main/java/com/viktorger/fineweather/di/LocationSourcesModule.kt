package com.viktorger.fineweather.di

import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationSourcesModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            "com.example.myapp.location", Context.MODE_PRIVATE
        )
    }

    @Provides
    fun provideLocationManager(context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
}