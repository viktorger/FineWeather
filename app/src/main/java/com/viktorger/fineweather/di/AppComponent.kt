package com.viktorger.fineweather.di

import android.content.Context
import com.viktorger.fineweather.databinding.ActivityLocationSearchBinding
import com.viktorger.fineweather.presentation.MainActivity
import com.viktorger.fineweather.presentation.dailyweather.DailyWeatherFragment
import com.viktorger.fineweather.presentation.search.LocationSearchActivity
import com.viktorger.fineweather.presentation.weatherdetails.WeatherDetailsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ForecastModule::class, ForecastSourcesModule::class, AppSubcomponentsModule::class,
    LocationSourcesModule::class
])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun searchComponent(): SearchComponent.Factory
    fun inject(fragment: DailyWeatherFragment)
    fun inject(fragment: WeatherDetailsFragment)
    fun inject(activity: MainActivity)


}