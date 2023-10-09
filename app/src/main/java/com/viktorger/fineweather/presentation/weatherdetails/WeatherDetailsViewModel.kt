package com.viktorger.fineweather.presentation.weatherdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel
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

    private val defaultDispatcher = Dispatchers.Default
    private val _dayForecastLiveData = MutableLiveData<ResultModel<ForecastDayModel>>()
    val dayForecastLiveData: LiveData<ResultModel<ForecastDayModel>> = _dayForecastLiveData

    private var fetchJob: Job? = null

    private fun getForecast(
        locationModel: SearchedLocationModel,
        dayEnum: DayEnum,
        forceUpdate: Boolean
    ) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch(defaultDispatcher) {
            when (dayEnum) {
                DayEnum.Today -> getWeatherTodayUseCase(locationModel, forceUpdate).collect {
                    _dayForecastLiveData.postValue(it)
                }

                else -> getWeatherTomorrowUseCase(locationModel, forceUpdate).collect {
                    _dayForecastLiveData.postValue(it)
                }
            }
        }
    }

    fun loadForecast(locationModel: SearchedLocationModel, dayEnum: DayEnum) {
        _dayForecastLiveData.value = ResultModel.Loading
        getForecast(locationModel, dayEnum, false)
    }
    fun updateForecast(locationModel: SearchedLocationModel, dayEnum: DayEnum) {
        getForecast(locationModel, dayEnum, true)
    }

}