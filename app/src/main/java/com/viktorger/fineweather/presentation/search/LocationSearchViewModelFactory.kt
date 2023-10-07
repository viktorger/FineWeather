package com.viktorger.fineweather.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.fineweather.data.repository.SearchRepositoryImpl
import com.viktorger.fineweather.data.storage.LocationRemoteDataSource
import com.viktorger.fineweather.data.storage.retrofit.ForecastApi
import com.viktorger.fineweather.domain.interfaces.SearchRepository
import com.viktorger.fineweather.domain.usecase.GetSearchedLocationUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationSearchViewModelFactory : ViewModelProvider.Factory {

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


    private val locationRemoteDataSource: LocationRemoteDataSource by lazy {
        LocationRemoteDataSource(forecastApi)
    }

    private val searchRepository: SearchRepository by lazy {
        SearchRepositoryImpl(locationRemoteDataSource)
    }


    private val getSearchedLocationUseCase: GetSearchedLocationUseCase by lazy {
        GetSearchedLocationUseCase(searchRepository)
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationSearchViewModel(
            getSearchedLocationUseCase
        ) as T
    }
}