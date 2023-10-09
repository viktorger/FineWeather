package com.viktorger.fineweather.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import com.viktorger.fineweather.domain.usecase.GetSavedLocationOrDefaultUseCase
import com.viktorger.fineweather.domain.usecase.SaveLocationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationViewModel(
    private val getSavedLocationOrDefaultUseCase: GetSavedLocationOrDefaultUseCase,
    private val saveLocationUseCase: SaveLocationUseCase
) : ViewModel() {

    private val defaultDispatcher = Dispatchers.Default
    private val _locationLiveData = MutableLiveData<SearchedLocationModel>()
    val locationLiveData: LiveData<SearchedLocationModel> = _locationLiveData

    fun saveResultFromActivity(searchedLocationModel: SearchedLocationModel) {
        _locationLiveData.postValue(searchedLocationModel)

        viewModelScope.launch(defaultDispatcher) {
            saveLocationUseCase(searchedLocationModel)
        }
    }

    fun initVm() {
        if (!locationLiveData.isInitialized) {
            CoroutineScope(defaultDispatcher).launch {
                val location = getSavedLocationOrDefaultUseCase()

                if (location is ResultModel.Success)
                    _locationLiveData.postValue(location.data)
            }
        }
    }
}