package com.viktorger.fineweather.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.fineweather.domain.usecase.GetSearchedLocationUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LocationSearchViewModelFactory @Inject constructor(
    private val getSearchedLocationUseCase: GetSearchedLocationUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationSearchViewModel(
            getSearchedLocationUseCase
        ) as T
    }
}