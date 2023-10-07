package com.viktorger.fineweather.domain.interfaces

import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel

interface SearchRepository {
    suspend fun getSearchedLocationList(query: String): ResultModel<List<SearchedLocationModel>>
    suspend fun getSavedLocationOrDefault(): SearchedLocationModel
}