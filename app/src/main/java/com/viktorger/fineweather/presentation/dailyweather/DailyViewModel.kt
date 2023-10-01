package com.viktorger.fineweather.presentation.dailyweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.usecase.GetWeatherTenDaysUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DailyViewModel(private val getWeatherTenDaysUseCase: GetWeatherTenDaysUseCase) : ViewModel() {

    private val defaultScope = CoroutineScope(Job() + Dispatchers.Default)
    private val _forecastListLiveData = MutableLiveData<ResultModel<List<ForecastDayModel>>>()
    val forecastListLiveData: LiveData<ResultModel<List<ForecastDayModel>>> = _forecastListLiveData

    private var fetchJob: Job? = null

    private fun getTenDaysForecast(forceUpdate: Boolean) {
        fetchJob?.cancel()

        fetchJob = defaultScope.launch {
            getWeatherTenDaysUseCase(forceUpdate).collect {
                _forecastListLiveData.postValue(it)
            }
        }
    }

    fun loadTenDaysForecast() {
        _forecastListLiveData.postValue(ResultModel.Loading)
        getTenDaysForecast(false)
    }
    fun updateTenDaysForecast() {
        getTenDaysForecast(true)
    }

    override fun onCleared() {
        super.onCleared()
        defaultScope.cancel()
    }
}