package com.viktorger.fineweather.domain.usecase

import com.viktorger.fineweather.domain.interfaces.LocationRepository
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import javax.inject.Inject

class GetSavedLocationOrDefaultUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): ResultModel<SearchedLocationModel> = locationRepository
        .getSavedLocationOrPhysicalLocation()
}