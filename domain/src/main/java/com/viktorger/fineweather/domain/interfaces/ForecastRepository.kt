package com.viktorger.fineweather.domain.interfaces

import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import kotlinx.coroutines.flow.Flow

interface ForecastRepository {
    fun getWeatherTomorrow(
        locationModel: SearchedLocationModel,
        forceUpdate: Boolean
    ) : Flow<ResultModel<ForecastDayModel>>
    fun getWeatherToday(
        locationModel: SearchedLocationModel, forceUpdate: Boolean
    ): Flow<ResultModel<ForecastDayModel>>
    fun getWeatherTenDays(
        locationModel: SearchedLocationModel,
        forceUpdate: Boolean
    ) : Flow<ResultModel<List<ForecastDayModel>>>
}