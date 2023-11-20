package com.viktorger.fineweather.presentation.weatherdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.fineweather.domain.usecase.GetImagePathUseCase
import com.viktorger.fineweather.domain.usecase.GetWeatherTodayUseCase
import com.viktorger.fineweather.domain.usecase.GetWeatherTomorrowUseCase
import javax.inject.Inject

class WeatherDetailsViewModelFactory @Inject constructor(
    private val getWeatherTodayUseCase: GetWeatherTodayUseCase,
    private val getWeatherTomorrowUseCase: GetWeatherTomorrowUseCase,
    private val getImagePathUseCase: GetImagePathUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherDetailsViewModel(
            getWeatherTodayUseCase,
            getWeatherTomorrowUseCase,
            getImagePathUseCase
        ) as T
    }
}