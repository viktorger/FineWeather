package com.viktorger.fineweather.presentation.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.viktorger.fineweather.domain.model.SearchedLocationModel
import com.viktorger.fineweather.presentation.search.LocationSearchActivity

const val LOCATION_NAME_KEY = "result_name"
const val LOCATION_COORDS_KEY = "result_coordinates"

class LocationSearchContract : ActivityResultContract<Nothing?, SearchedLocationModel?>() {

    override fun createIntent(context: Context, input: Nothing?): Intent {
        return Intent(context, LocationSearchActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): SearchedLocationModel? = when (resultCode) {
        Activity.RESULT_OK -> {
            val coordinates = intent?.getStringExtra(LOCATION_COORDS_KEY) ?: ""
            val locationName = intent?.getStringExtra(LOCATION_NAME_KEY) ?: ""

            SearchedLocationModel(locationName, coordinates)
        }
        else -> null
    }
}