package com.viktorger.fineweather.domain.interfaces

import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel

interface ForecastRepository {
    suspend fun getWeatherDay(day: Int) : ResultModel<ForecastDayModel>
    suspend fun getWeatherToday(): ResultModel<ForecastDayModel>

    suspend fun getWeatherTenDays() : ResultModel<List<ForecastDayModel>>
}