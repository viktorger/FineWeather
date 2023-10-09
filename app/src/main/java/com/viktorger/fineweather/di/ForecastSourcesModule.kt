package com.viktorger.fineweather.di

import android.content.Context
import androidx.room.Room
import com.viktorger.fineweather.data.storage.retrofit.ForecastApi
import com.viktorger.fineweather.data.storage.room.LocalDatabase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ForecastSourcesModule {

    @Singleton
    @Provides
    fun provideForecastApi(): ForecastApi {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()


        val forecastApi = Retrofit.Builder()
            //.client(okHttpClient)
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ForecastApi::class.java)

        return forecastApi
    }

    @Singleton
    @Provides
    fun provideLocalDataBase(context: Context): LocalDatabase = Room.databaseBuilder(
        context,
        LocalDatabase::class.java,
        "localDb"
    ).build()

}