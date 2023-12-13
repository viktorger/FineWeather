package com.viktorger.fineweather.data.repository

import com.viktorger.fineweather.data.model.SearchedLocationDataModel
import com.viktorger.fineweather.data.storage.GpsDataSource
import com.viktorger.fineweather.data.storage.LocationLocalDataSource
import com.viktorger.fineweather.data.storage.LocationRemoteDataSource
import com.viktorger.fineweather.domain.interfaces.LocationRepository
import com.viktorger.fineweather.domain.model.ResultModel
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val gpsDataSource: GpsDataSource,
    private val locationLocalDataSource: LocationLocalDataSource,
    private val locationRemoteDataSource: LocationRemoteDataSource
) : LocationRepository {

    override suspend fun getSearchedLocationList(query: String)
    : ResultModel<List<SearchedLocationModel>> {
        return when (val networkResult = locationRemoteDataSource.getSearchedLocationList(query)) {
            is ResultModel.Success -> {
                ResultModel.Success(
                    networkResult.data.map { locationToDomain(it) }
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

    override suspend fun getSavedLocationOrPhysicalLocation(): ResultModel<SearchedLocationModel> {
        val localResult = locationLocalDataSource.getLocation()

        if (localResult is ResultModel.Success) {
            return ResultModel.Success(
                locationToDomain(localResult.data)
            )
        } else {
            return getGpsLocation()
        }
    }

    override suspend fun getSavedLocation(): ResultModel<SearchedLocationModel> {
        val localResult = locationLocalDataSource.getLocation()

        if (localResult is ResultModel.Success) {
            return ResultModel.Success(
                locationToDomain(localResult.data)
            )
        } else {
            return ResultModel.Error(
                (localResult as ResultModel.Error).code,
                localResult.message
            )
        }
    }

    override suspend fun getGpsLocation(): ResultModel<SearchedLocationModel> {
        val gpsResult = gpsDataSource.getCurrentLocation()
        val result: ResultModel<SearchedLocationModel>

        if (gpsResult is ResultModel.Success) {
            val locationRes: ResultModel<SearchedLocationDataModel> = locationRemoteDataSource
                .getSearchedLocationFirst(gpsResult.data.coordinates)

            result = if (locationRes is ResultModel.Success) {
                ResultModel.Success(
                    locationToDomain(locationRes.data)
                )
            } else {
                ResultModel.Error(
                    code = (locationRes as ResultModel.Error).code,
                    message = locationRes.message
                )
            }
        } else {
            val locationRes: ResultModel<SearchedLocationDataModel> = locationRemoteDataSource
                .getSearchedLocationFirst("auto:ip")

            result = if (locationRes is ResultModel.Success) {
                ResultModel.Success(
                    locationToDomain(locationRes.data)
                )
            } else {
                ResultModel.Error(
                    code = (locationRes as ResultModel.Error).code,
                    message = locationRes.message
                )
            }
        }

        return result
    }

    override suspend fun saveLocation(searchedLocationModel: SearchedLocationModel) {
        val searchedLocationDataModel = locationToData(searchedLocationModel)
        locationLocalDataSource.saveLocation(searchedLocationDataModel)
    }

    private fun locationToDomain(searchedLocationDataModel: SearchedLocationDataModel)
    : SearchedLocationModel = SearchedLocationModel(
        locationName = searchedLocationDataModel.locationName,
        coordinates = searchedLocationDataModel.coordinates
    )

    private fun locationToData(searchedLocationModel: SearchedLocationModel)
    : SearchedLocationDataModel = SearchedLocationDataModel(
        locationName = searchedLocationModel.locationName,
        coordinates = searchedLocationModel.coordinates
    )
}