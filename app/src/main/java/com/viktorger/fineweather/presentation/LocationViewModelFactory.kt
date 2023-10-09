package com.viktorger.fineweather.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viktorger.fineweather.domain.usecase.GetSavedLocationOrDefaultUseCase
import com.viktorger.fineweather.domain.usecase.SaveLocationUseCase
import javax.inject.Inject

class LocationViewModelFactory @Inject constructor(
    private val getSavedLocationOrDefaultUseCase: GetSavedLocationOrDefaultUseCase,
    private val saveLocationUseCase: SaveLocationUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationViewModel(
            getSavedLocationOrDefaultUseCase = getSavedLocationOrDefaultUseCase,
            saveLocationUseCase = saveLocationUseCase
        ) as T
    }
}