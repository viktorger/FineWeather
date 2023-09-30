package com.viktorger.fineweather.presentation.dailyweather

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.viktorger.fineweather.data.repository.interfaces.ForecastRepositoryImpl
import com.viktorger.fineweather.data.storage.ForecastLocalDataSource
import com.viktorger.fineweather.data.storage.ForecastRemoteDataSource
import com.viktorger.fineweather.data.storage.retrofit.ForecastApi
import com.viktorger.fineweather.data.storage.room.LocalDatabase
import com.viktorger.fineweather.domain.interfaces.ForecastRepository
import com.viktorger.fineweather.domain.usecase.GetWeatherTenDaysUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DailyViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

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

    private val localDatabase: LocalDatabase by lazy {
        Room.databaseBuilder(
            context,
            LocalDatabase::class.java,
            "localDb"
        ).build()
    }

    private val forecastLocalDataSource: ForecastLocalDataSource by lazy {
        ForecastLocalDataSource(localDatabase)
    }

    private val forecastRemoteDataSource: ForecastRemoteDataSource by lazy {
        ForecastRemoteDataSource(forecastApi)
    }

    private val forecastRepository: ForecastRepository by lazy {
        ForecastRepositoryImpl(forecastLocalDataSource, forecastRemoteDataSource)
    }

    private val getWeatherTenDaysUseCase: GetWeatherTenDaysUseCase by lazy {
        GetWeatherTenDaysUseCase(forecastRepository)
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DailyViewModel(getWeatherTenDaysUseCase) as T
    }
}