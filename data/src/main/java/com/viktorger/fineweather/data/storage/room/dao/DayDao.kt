package com.viktorger.fineweather.data.storage.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.viktorger.fineweather.data.storage.room.entities.DayEntity
import com.viktorger.fineweather.data.storage.room.relationships.DayWithHours

@Dao
interface DayDao {
    @Transaction
    @Query("SELECT * FROM day WHERE day.day in (:day)")
    suspend fun getDayWithHour(vararg day: Int): List<DayWithHours>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(dayEntity: DayEntity)
}