package com.viktorger.fineweather.data.storage.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "hour"
)
data class HourEntity(
    @PrimaryKey @ColumnInfo(name = "time_epoch") val timeEpoch: Int,
    val time: String,
    @ColumnInfo(name = "temp_c") val tempC: Int,
    @ColumnInfo(name = " chance_of_rain") val chanceOfRain: Float,
    val day: Int,
    @ColumnInfo(name = "condition_text") val conditionText: String,
    @ColumnInfo(name = "condition_icon") val conditionIcon: String,
)
