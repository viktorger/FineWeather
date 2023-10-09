package com.viktorger.fineweather.data.storage

import com.viktorger.fineweather.data.model.SearchedLocationDataModel
import com.viktorger.fineweather.data.storage.retrofit.ForecastApi
import com.viktorger.fineweather.data.storage.retrofit.SearchedLocation
import com.viktorger.fineweather.domain.model.ResultModel
import javax.inject.Inject

class LocationRemoteDataSource @Inject constructor(
    private val forecastApi: ForecastApi
) {
    suspend fun getSearchedLocationList(query: String)
    : ResultModel<List<SearchedLocationDataModel>> {

        val searchResponse = safeApiCall {
            forecastApi.getSearchedLocationList(
                key = KEY,
                location = query
            )
        }

        return when (searchResponse) {
            is ResultModel.Success -> {

                ResultModel.Success(
                    searchResponse.data.map {
                        locationListToData(it)
                    }
                )

            }
            is ResultModel.Error -> {
                ResultModel.Error(
                    code = searchResponse.code,
                    message = searchResponse.message
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

    suspend fun getSearchedLocationFirst(query: String): ResultModel<SearchedLocationDataModel> {
        val searchResponse = safeApiCall {
            forecastApi.getSearchedLocationList(
                key = KEY,
                location = query
            )
        }

        return when (searchResponse) {
            is ResultModel.Success -> {
                ResultModel.Success(
                    locationListToData(searchResponse.data[0])
                )
            }
            is ResultModel.Error -> {
                ResultModel.Error(
                    code = searchResponse.code,
                    message = searchResponse.message
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

    private fun locationListToData(
        searchedLocation: SearchedLocation
    ): SearchedLocationDataModel = SearchedLocationDataModel(
        locationName = "${searchedLocation.country}, ${searchedLocation.region}, ${searchedLocation.name}",
        coordinates = "${searchedLocation.lat}, ${searchedLocation.lon}"
    )


}