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

    private val defaultScope = CoroutineScope(Job() + Dispatchers.Default)
    private val _dayForecastLiveData = MutableLiveData<ResultModel<ForecastDayModel>>()
    val dayForecastLiveData: LiveData<ResultModel<ForecastDayModel>> = _dayForecastLiveData

    private var fetchJob: Job? = null

    private fun getForecast(dayEnum: DayEnum, forceUpdate: Boolean) {
        fetchJob?.cancel()

        fetchJob = defaultScope.launch {
            when (dayEnum) {
                DayEnum.Today -> getWeatherTodayUseCase(forceUpdate).collect {
                    _dayForecastLiveData.postValue(it)
                }

                else -> getWeatherTomorrowUseCase(forceUpdate).collect {
                    _dayForecastLiveData.postValue(it)
                }
            }
        }
    }

    fun loadForecast(dayEnum: DayEnum) {
        _dayForecastLiveData.value = ResultModel.Loading
        getForecast(dayEnum, false)
    }
    fun updateForecast(dayEnum: DayEnum) {
        getForecast(dayEnum, true)
    }

    override fun onCleared() {
        super.onCleared()
        defaultScope.cancel()
    }

}