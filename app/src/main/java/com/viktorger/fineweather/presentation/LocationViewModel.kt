package com.viktorger.fineweather.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel

class LocationViewModel : ViewModel() {
    private val _locationLiveData = MutableLiveData<SearchedLocationModel>()
    val locationLiveData: LiveData<SearchedLocationModel> = _locationLiveData

    fun loadLocation() {

    }
}