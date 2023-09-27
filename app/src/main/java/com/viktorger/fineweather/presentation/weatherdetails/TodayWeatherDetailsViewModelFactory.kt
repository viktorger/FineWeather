package com.viktorger.fineweather.presentation.weatherdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.fineweather.data.repository.interfaces.ForecastRepositoryImpl
import com.viktorger.fineweather.data.storage.retrofit.ForecastApi
import com.viktorger.fineweather.domain.interfaces.ForecastRepository
import com.viktorger.fineweather.domain.usecase.GetWeatherTodayUseCase
import com.viktorger.fineweather.domain.usecase.GetWeatherTomorrowUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TodayWeatherDetailsViewModelFactory : ViewModelProvider.Factory {

    private val forecastApi: ForecastApi by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()


        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        retrofit.create(ForecastApi::class.java)
    }

    private val forecastRepository: ForecastRepository by lazy {
        ForecastRepositoryImpl(forecastApi)
    }

    private val getWeatherTodayUseCase: GetWeatherTodayUseCase by lazy {
        GetWeatherTodayUseCase(forecastRepository)
    }

    private val getWeatherTomorrowUseCase: GetWeatherTomorrowUseCase by lazy {
        GetWeatherTomorrowUseCase(forecastRepository)
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherDetailsViewModel(
            getWeatherTodayUseCase,
            getWeatherTomorrowUseCase
        ) as T
    }
}