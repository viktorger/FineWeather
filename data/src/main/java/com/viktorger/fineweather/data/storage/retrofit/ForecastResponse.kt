package com.viktorger.fineweather.data.storage.retrofit

data class ForecastResponse(
    val location: Location,
    var current: Current,
    val forecast: Forecast,
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val tz_id: String,
    val localtime_epoch: Int
)

data class Current(
    val temp_c: Float,
    val condition: Condition
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val date_epoch: Int,
    val day: Day,
    val hour: List<Hour>,
)

data class Day(
    val maxtemp_c: Float,
    val mintemp_c: Float,
    val daily_chance_of_rain: Float,
    val condition: Condition,
)

data class Condition(
    val text: String,
    val icon: String,
)

data class Hour(
    val time_epoch: Int,
    val time: String,
    val temp_c: Float,
    val condition: Condition,
    val chance_of_rain: Float
)