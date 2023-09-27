package com.viktorger.fineweather.data.storage.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "day"
)
data class DayEntity(
    @PrimaryKey val day: Int,
    val date: String,
    @ColumnInfo(name = "maxtemp_c") val maxTempC: Int,
    @ColumnInfo(name = "mintemp_c") val minTempC: Int,
    val status: String,
    @ColumnInfo(name = "daily_chance_of_rain") val dailyChanceOfRain: Float,
    @ColumnInfo(name = "condition_text") val conditionText: String,
    @ColumnInfo(name = "condition_icon") val conditionIcon: String
)
