package com.viktorger.fineweather.presentation.dailyweather

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.viktorger.fineweather.data.repository.ForecastRepositoryImpl
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
import javax.inject.Inject

class DailyViewModelFactory @Inject constructor(
    private val getWeatherTenDaysUseCase: GetWeatherTenDaysUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DailyViewModel(getWeatherTenDaysUseCase) as T
    }
}