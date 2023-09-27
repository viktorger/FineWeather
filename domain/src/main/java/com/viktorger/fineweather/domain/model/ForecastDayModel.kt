package com.viktorger.fineweather.domain.model

data class ForecastDayModel(
    val date: String,
    val maxTempC: Int,
    val minTempC: Int,
    val status: String,
    val dailyChanceOfRain: Float,
    val condition: ConditionModel,
    val hour: List<HourModel>,
)


data class HourModel(
    val time: String,
    val tempC: Int,
    val condition: ConditionModel,
    val chanceOfRain: Float
)

data class ConditionModel(
    val text: String,
    val icon: String,
)
