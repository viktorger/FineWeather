package com.viktorger.fineweather.domain.usecase

import com.viktorger.fineweather.domain.interfaces.SearchRepository
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel

class GetSearchedLocationUseCase(private val searchRepository: SearchRepository) {

    suspend operator fun invoke(query: String): ResultModel<List<SearchedLocationModel>> =
        searchRepository.getSearchedLocationList(query)

}