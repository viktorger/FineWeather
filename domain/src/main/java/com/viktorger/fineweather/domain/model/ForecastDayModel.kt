package com.viktorger.fineweather.domain.model

data class ForecastDayModel(
    val date: String,
    val maxtemp_c: Int,
    val mintemp_c: Int,
    val status: String,
    val daily_chance_of_rain: Float,
    val condition: ConditionModel,
    val hour: List<HourModel>,
)


data class HourModel(
    val time: String,
    val temp_c: Int,
    val condition: ConditionModel,
    val chance_of_rain: Float
)

data class ConditionModel(
    val text: String,
    val icon: String,
)
