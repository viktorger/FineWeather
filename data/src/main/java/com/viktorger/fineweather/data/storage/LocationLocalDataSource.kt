package com.viktorger.fineweather.data.storage

import android.content.SharedPreferences
import com.viktorger.fineweather.data.model.SearchedLocationDataModel
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Error
import javax.inject.Inject

class LocationLocalDataSource @Inject constructor(
    private val sharedPref: SharedPreferences
) {

    val locationNameKey = "result_name"
    val locationCoordsKey = "result_coordinates"

    suspend fun saveLocation(
        locationDataModel: SearchedLocationDataModel
    ) = withContext(Dispatchers.IO) {
        val editor = sharedPref.edit()
        editor.putString(locationNameKey, locationDataModel.locationName)
        editor.putString(locationCoordsKey, locationDataModel.coordinates)
        editor.commit()
    }

    fun getLocation(): ResultModel<SearchedLocationDataModel> {
        val locationName = sharedPref.getString(locationNameKey, null)
        val coordinates = sharedPref.getString(locationCoordsKey, null)

        return if (locationName == null || coordinates == null) {
            ResultModel.Error(
                code = -1,
                message = "No saved location info"
            )
        } else {
            ResultModel.Success(
                SearchedLocationDataModel(locationName, coordinates)
            )
        }
    }

}