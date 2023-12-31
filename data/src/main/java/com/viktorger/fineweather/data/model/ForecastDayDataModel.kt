package com.viktorger.fineweather.data.model

data class ForecastDayDataModel(
    val date: String,
    var location: String,
    val tzId: String,
    val lastUpdate: Int,
    val maxTempC: Int,
    val minTempC: Int,
    val status: String,
    val dailyChanceOfRain: Float,
    val condition: ConditionDataModel,
    val hour: List<HourDataModel>,
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