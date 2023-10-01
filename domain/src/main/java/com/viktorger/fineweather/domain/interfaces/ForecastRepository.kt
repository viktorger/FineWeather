package com.viktorger.fineweather.domain.interfaces

import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.flow.Flow

interface ForecastRepository {
    fun getWeatherTomorrow(forceUpdate: Boolean) : Flow<ResultModel<ForecastDayModel>>
    fun getWeatherToday(forceUpdate: Boolean): Flow<ResultModel<ForecastDayModel>>
    fun getWeatherTenDays(forceUpdate: Boolean) : Flow<ResultModel<List<ForecastDayModel>>>
}