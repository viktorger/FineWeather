package com.viktorger.fineweather.domain.usecase

import com.viktorger.fineweather.domain.interfaces.LocationRepository
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    suspend operator fun invoke(searchedLocationModel: SearchedLocationModel) =
        locationRepository.saveLocation(searchedLocationModel)

}