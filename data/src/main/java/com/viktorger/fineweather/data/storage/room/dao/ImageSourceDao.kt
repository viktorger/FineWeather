package com.viktorger.fineweather.data.storage.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.viktorger.fineweather.data.storage.room.entities.ImageSourceEntity
import com.viktorger.fineweather.data.storage.room.relationships.DayWithHours

@Dao
interface ImageSourceDao {
    @Query("SELECT local_path FROM image_source WHERE network_url = :url")
    suspend fun getImagePathByUrl(url: String): List<String>

    @Insert
    suspend fun insertImageSource(imageSourceEntity: ImageSourceEntity)
}