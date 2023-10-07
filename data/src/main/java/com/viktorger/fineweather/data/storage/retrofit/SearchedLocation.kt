package com.viktorger.fineweather.data.storage.retrofit

data class SearchedLocation (
    val name: String,
    val region: String,
    val country: String,
    val lat: Float,
    val lon: Float
)