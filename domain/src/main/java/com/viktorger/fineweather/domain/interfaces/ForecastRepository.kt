package com.viktorger.fineweather.domain.interfaces

import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.flow.Flow

interface ForecastRepository {
    suspend fun getWeatherTomorrow() : Flow<ResultModel<ForecastDayModel>>
    suspend fun getWeatherToday(): Flow<ResultModel<ForecastDayModel>>

    suspend fun getWeatherTenDays() : Flow<ResultModel<List<ForecastDayModel>>>
}