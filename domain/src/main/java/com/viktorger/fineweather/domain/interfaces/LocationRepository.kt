package com.viktorger.fineweather.domain.interfaces

import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel

interface LocationRepository {
    suspend fun getSearchedLocationList(query: String): ResultModel<List<SearchedLocationModel>>
    suspend fun getSavedLocationOrPhysicalLocation(): ResultModel<SearchedLocationModel>
    suspend fun getSavedLocation(): ResultModel<SearchedLocationModel>
    suspend fun saveLocation(searchedLocationModel: SearchedLocationModel)
    suspend fun getGpsLocation(): ResultModel<SearchedLocationModel>
}