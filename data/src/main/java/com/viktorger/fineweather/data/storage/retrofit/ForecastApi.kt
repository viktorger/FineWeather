package com.viktorger.fineweather.data.storage.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApi {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") key: String,
        @Query("q", encoded = true) location: String,
        @Query("days") days: Int
    ): Response<ForecastResponse>

    @GET("timezone.json")
    suspend fun getLocationInfo(
        @Query("key") key: String,
        @Query("q", encoded = true) location: String
    ): Response<LocationResponse>
}