package com.viktorger.fineweather.data.storage

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.viktorger.fineweather.data.model.SearchedLocationDataModel
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GpsDataSource @Inject constructor(
    private val locationManager: LocationManager
) {
    suspend fun getCurrentLocation()
    : ResultModel<SearchedLocationDataModel> = withContext(Dispatchers.Main) {
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var locationByGps: Location? = null
        var locationByNetwork: Location? = null

        /*val gpsLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
            }
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        val networkLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
            }
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }*/

        try {
            /*if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    gpsLocationListener
                )
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
                )
            }*/

            val lastKnownLocationByGps =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocationByGps?.let {
                locationByGps = lastKnownLocationByGps
            }

            val lastKnownLocationByNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork?.let {
                locationByNetwork = lastKnownLocationByNetwork
            }

            var currentLocation: Location? = null

            locationByNetwork?.let { itNetwork ->
                locationByGps?.let { itGps ->
                    currentLocation = if (itGps.accuracy > itNetwork.accuracy) {
                        itGps
                    } else {
                        itNetwork
                    }
                } ?: let {
                    currentLocation = itNetwork
                }
            }
            /*Log.d("Gps", "${locationByGps?.latitude} ${locationByGps?.longitude}")
            Log.d("network", "${locationByNetwork?.latitude} ${locationByNetwork?.longitude}")*/


            // Log.d("PRIKOL", "${currentLocation?.latitude} ${currentLocation?.longitude}")
            val result: ResultModel<SearchedLocationDataModel> = currentLocation?.let {
                ResultModel.Success(
                    SearchedLocationDataModel(
                        locationName = "",
                        coordinates = "${it.latitude}, ${it.longitude}"
                    )
                )
            } ?: let {
                ResultModel.Error(
                    -1,
                    "No access error"
                )
            }

            result
        } catch (e: SecurityException) {
            ResultModel.Error(
                code = -1,
                message = "No access error"
            )
        }
    }
}