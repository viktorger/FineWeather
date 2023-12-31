package com.viktorger.fineweather.presentation.weatherdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import com.viktorger.fineweather.domain.usecase.GetImagePathUseCase
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
    private val getWeatherTomorrowUseCase: GetWeatherTomorrowUseCase,
    private val getImagePathUseCase: GetImagePathUseCase
) : ViewModel() {

    private val defaultDispatcher = Dispatchers.Default
    private val _dayForecastLiveData = MutableLiveData<ResultModel<ForecastDayModel>>()
    val dayForecastLiveData: LiveData<ResultModel<ForecastDayModel>> = _dayForecastLiveData

    private val _imageLiveData = MutableLiveData<ResultModel<String>>()
    val imageLiveData: LiveData<ResultModel<String>> = _imageLiveData

    private var fetchJob: Job? = null
    private var imageJob: Job? = null

    private fun getForecast(
        locationModel: SearchedLocationModel,
        dayEnum: DayEnum,
        forceUpdate: Boolean
    ) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch(defaultDispatcher) {
            when (dayEnum) {
                DayEnum.Today -> getWeatherTodayUseCase(locationModel, forceUpdate).collect {
                    collectionAction(it)
                }

                else -> getWeatherTomorrowUseCase(locationModel, forceUpdate).collect {
                    collectionAction(it)
                }
            }
        }
    }

    private suspend fun collectionAction(resultModel: ResultModel<ForecastDayModel>) {
        _dayForecastLiveData.postValue(resultModel)

        imageJob?.cancel()
        imageJob = viewModelScope.launch(defaultDispatcher) {
            if (resultModel is ResultModel.Success) {
                val path = getImagePathUseCase(resultModel.data.condition.icon)
                _imageLiveData.postValue(path)
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

    fun setImageUrl(url: String) {
        imageJob?.cancel()
        imageJob = viewModelScope.launch(defaultDispatcher) {
            val path = getImagePathUseCase(url)
            _imageLiveData.postValue(path)
        }

    }

}