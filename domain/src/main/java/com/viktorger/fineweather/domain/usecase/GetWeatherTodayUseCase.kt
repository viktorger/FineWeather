package com.viktorger.fineweather.domain.usecase

import com.viktorger.fineweather.domain.interfaces.ForecastRepository
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.flow.Flow

class GetWeatherTodayUseCase(private val forecastRepository: ForecastRepository) {
    operator fun invoke(forceUpdate: Boolean): Flow<ResultModel<ForecastDayModel>> =
        forecastRepository.getWeatherToday(forceUpdate)
}