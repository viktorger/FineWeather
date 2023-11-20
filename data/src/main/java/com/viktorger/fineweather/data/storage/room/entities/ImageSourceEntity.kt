package com.viktorger.fineweather.data.storage.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_source"
)
data class ImageSourceEntity(
    @PrimaryKey @ColumnInfo(name = "network_url") val networkUrl: String,
    @ColumnInfo(name = "local_path") val localPath: String
)
