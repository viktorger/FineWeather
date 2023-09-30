package com.viktorger.fineweather.domain.usecase

import com.viktorger.fineweather.domain.interfaces.ForecastRepository
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.flow.Flow


class GetWeatherTomorrowUseCase(private val forecastRepository: ForecastRepository) {

    suspend operator fun invoke(): Flow<ResultModel<ForecastDayModel>> {
        return forecastRepository.getWeatherTomorrow()
    }

}