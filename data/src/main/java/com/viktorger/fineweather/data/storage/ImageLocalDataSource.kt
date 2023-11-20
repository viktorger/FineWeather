package com.viktorger.fineweather.data.storage

import android.graphics.Bitmap
import com.viktorger.fineweather.data.storage.room.LocalDatabase
import com.viktorger.fineweather.data.storage.room.entities.ImageSourceEntity
import com.viktorger.fineweather.domain.model.ResultModel
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ImageLocalDataSource @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val rootDir: File
) {

    suspend fun getImagePathByUrl(url: String): ResultModel<String> {
        val imageSourceDao = localDatabase.imageSourceDao()
        val localPathData = imageSourceDao.getImagePathByUrl(url)

        val result: ResultModel<String> = if (localPathData.isNotEmpty()) {
            ResultModel.Success(
                localPathData[0]
            )
        } else {
            ResultModel.Error(
                code = -1,
                message = "No path for queried url"
            )
        }

        return result
    }

    suspend fun saveImageUrlAndGetPath(url: String, bitmap: Bitmap): ResultModel<String> {
        val name = getFileName(url)
        try {
            val file = File(rootDir, name)
            val fOut = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()

            val imageSourceDao = localDatabase.imageSourceDao()
            imageSourceDao.insertImageSource(
                ImageSourceEntity(
                    networkUrl = url,
                    localPath = file.path
                )
            )

            return ResultModel.Success(file.path)
        } catch (e: Exception) {
            return ResultModel.Error(
                code = -1,
                message = "File error"
            )
        }

    }

    private fun getFileName(url: String): String {
        val lastDot = url.indexOfLast { it == '.' }
        val lastSlash = url.indexOfLast { it == '/' }

        val name = url.substring(
            lastSlash, if (lastDot != -1) {
                lastDot
            } else {
                url.length
            }
        )

        return name
    }

    // https://api.weather.com/files/clear.png
}