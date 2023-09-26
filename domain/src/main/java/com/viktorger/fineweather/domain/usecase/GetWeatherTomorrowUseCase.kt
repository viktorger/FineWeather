package com.viktorger.fineweather.domain.usecase

import com.viktorger.fineweather.domain.interfaces.ForecastRepository
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel


class GetWeatherTomorrowUseCase(private val forecastRepository: ForecastRepository) {

    suspend operator fun invoke(): ResultModel<ForecastDayModel> {
        return forecastRepository.getWeatherDay(1)
    }

}