package com.viktorger.fineweather.data.storage.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.viktorger.fineweather.data.storage.room.entities.HourEntity

@Dao
interface HourDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(hourEntity: HourEntity)
}