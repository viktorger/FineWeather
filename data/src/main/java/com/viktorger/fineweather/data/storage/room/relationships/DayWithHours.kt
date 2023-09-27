package com.viktorger.fineweather.data.storage.room.relationships

import androidx.room.Embedded
import androidx.room.Relation
import com.viktorger.fineweather.data.storage.room.entities.DayEntity
import com.viktorger.fineweather.data.storage.room.entities.HourEntity

data class DayWithHours(
    @Embedded val day: DayEntity,
    @Relation(
        parentColumn = "day",
        entityColumn = "day"
    )
    val hours: List<HourEntity>
)
