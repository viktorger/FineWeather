package com.viktorger.fineweather.data.repository

import com.viktorger.fineweather.data.model.SearchedLocationDataModel
import com.viktorger.fineweather.data.storage.LocationRemoteDataSource
import com.viktorger.fineweather.domain.interfaces.SearchRepository
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel

class SearchRepositoryImpl(
    private val locationRemoteDataSource: LocationRemoteDataSource
) : SearchRepository {

    override suspend fun getSearchedLocationList(query: String)
    : ResultModel<List<SearchedLocationModel>> {
        val networkResult = locationRemoteDataSource.getSearchedLocationList(query)

        return when (networkResult) {
            is ResultModel.Success -> {
                ResultModel.Success(
                    networkResult.data.map { locationListToDomain(it) }
                )
            }
            is ResultModel.Error -> {
                ResultModel.Error(
                    code = networkResult.code,
                    message = networkResult.message
                )
            }
            else -> {
                ResultModel.Error(
                    code = -1,
                    message = "Unknown error"
                )
            }
        }
    }

    override suspend fun getSavedLocationOrDefault(): SearchedLocationModel {
        return SearchedLocationModel(
            locationName = "name",
            coordinates = ""
        )
    }

    private fun locationListToDomain(searchedLocationDataModel: SearchedLocationDataModel)
    : SearchedLocationModel = SearchedLocationModel(
        locationName = searchedLocationDataModel.locationName,
        coordinates = searchedLocationDataModel.coordinates
    )
}