package com.viktorger.fineweather.data.model

data class ForecastDayDataModel(
    val date: String,
    val location: String,
    val lastUpdate: Int,
    val maxTempC: Int,
    val minTempC: Int,
    val status: String,
    val dailyChanceOfRain: Float,
    val condition: ConditionDataModel,
    val hour: List<HourDataModel>,
)

data class LocationDataModel (
    val name: String,
    val tzId: String,
    val lastUpdate: Int
)


data class HourDataModel(
    val time: String,
    val timeEpoch: Int,
    val tempC: Int,
    val condition: ConditionDataModel,
    val chanceOfRain: Float
)

data class ConditionDataModel(
    val text: String,
    val icon: String,
)