package com.viktorger.fineweather.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import com.viktorger.fineweather.domain.usecase.GetSearchedLocationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationSearchViewModel(
    private val getSearchedLocationUseCase: GetSearchedLocationUseCase
) : ViewModel() {

    private var searchJob: Job? = null
    private val defaultDispatcher = Dispatchers.Default
    private var _locationListLiveData = MutableLiveData<ResultModel<List<SearchedLocationModel>>>()
    val locationListLiveData: LiveData<ResultModel<List<SearchedLocationModel>>> = _locationListLiveData

    fun updateLocationList(query: String) {
        _locationListLiveData.postValue(ResultModel.Loading)
        searchJob?.cancel()

        searchJob = viewModelScope.launch(defaultDispatcher) {
            val locationInfo = getSearchedLocationUseCase(query)
            _locationListLiveData.postValue(locationInfo)
        }
    }

}