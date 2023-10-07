package com.viktorger.fineweather.presentation.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.viktorger.fineweather.presentation.search.LocationSearchActivity

const val LOCATION_SEARCH_KEY = "result_coordinates"

class LocationSearchContract : ActivityResultContract<Nothing?, String?>() {

    override fun createIntent(context: Context, input: Nothing?): Intent {
        return Intent(context, LocationSearchActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? = when (resultCode) {
        Activity.RESULT_OK -> intent?.getStringExtra(LOCATION_SEARCH_KEY)
        else -> null
    }
}