package com.viktorger.fineweather.data.storage.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.viktorger.fineweather.data.storage.room.dao.DayDao
import com.viktorger.fineweather.data.storage.room.dao.HourDao
import com.viktorger.fineweather.data.storage.room.entities.DayEntity
import com.viktorger.fineweather.data.storage.room.entities.HourEntity

@Database(
    entities = [DayEntity::class, HourEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun dayDao(): DayDao
    abstract fun hourDao(): HourDao
}