package com.viktorger.fineweather.domain.usecase

import com.viktorger.fineweather.domain.interfaces.LocationRepository
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import javax.inject.Inject

class GetSearchedLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    suspend operator fun invoke(query: String): ResultModel<List<SearchedLocationModel>> =
        locationRepository.getSearchedLocationList(query)

}