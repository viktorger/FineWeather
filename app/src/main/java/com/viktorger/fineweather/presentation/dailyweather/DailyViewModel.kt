package com.viktorger.fineweather.presentation.dailyweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import com.viktorger.fineweather.domain.usecase.GetWeatherTenDaysUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DailyViewModel(private val getWeatherTenDaysUseCase: GetWeatherTenDaysUseCase) : ViewModel() {

    private val defaultDispatcher = Dispatchers.Default
    private val _forecastListLiveData = MutableLiveData<ResultModel<List<ForecastDayModel>>>()
    val forecastListLiveData: LiveData<ResultModel<List<ForecastDayModel>>> = _forecastListLiveData

    private var fetchJob: Job? = null

    private fun getTenDaysForecast(locationModel: SearchedLocationModel, forceUpdate: Boolean) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch(defaultDispatcher) {
            getWeatherTenDaysUseCase(locationModel, forceUpdate).collect {
                _forecastListLiveData.postValue(it)
            }
        }
    }

    fun loadTenDaysForecast(locationModel: SearchedLocationModel) {
        _forecastListLiveData.postValue(ResultModel.Loading)
        getTenDaysForecast(locationModel, false)
    }

    fun updateTenDaysForecast(locationModel: SearchedLocationModel) {
        getTenDaysForecast(locationModel, true)
    }
}