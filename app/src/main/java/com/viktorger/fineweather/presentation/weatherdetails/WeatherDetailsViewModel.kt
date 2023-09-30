package com.viktorger.fineweather.presentation.weatherdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.usecase.GetWeatherTodayUseCase
import com.viktorger.fineweather.domain.usecase.GetWeatherTomorrowUseCase
import com.viktorger.fineweather.presentation.model.DayEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(
    private val getWeatherTodayUseCase: GetWeatherTodayUseCase,
    private val getWeatherTomorrowUseCase: GetWeatherTomorrowUseCase
) : ViewModel() {

    private val ioScope = CoroutineScope(Job() + Dispatchers.IO)
    private val _dayForecastLiveData = MutableLiveData<ResultModel<ForecastDayModel>>()
    val dayForecastLiveData: LiveData<ResultModel<ForecastDayModel>> = _dayForecastLiveData

    fun loadWeather(dayEnum: DayEnum) {
        _dayForecastLiveData.value = ResultModel.Loading

        ioScope.launch {

            when (dayEnum) {
                DayEnum.Today -> getWeatherTodayUseCase().collect {
                    _dayForecastLiveData.postValue(it)
                }
                else -> getWeatherTomorrowUseCase().collect {
                    _dayForecastLiveData.postValue(it)
                }
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        ioScope.cancel()
    }

}