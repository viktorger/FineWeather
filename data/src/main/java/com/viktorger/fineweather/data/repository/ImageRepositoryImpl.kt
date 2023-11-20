package com.viktorger.fineweather.data.repository

import android.graphics.Bitmap
import com.viktorger.fineweather.data.storage.ImageLocalDataSource
import com.viktorger.fineweather.data.storage.ImageRemoteDataSource
import com.viktorger.fineweather.domain.interfaces.ImageRepository
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageLocalDataSource: ImageLocalDataSource,
    private val imageRemoteDataSource: ImageRemoteDataSource
) : ImageRepository {
    @DelicateCoroutinesApi
    @ExperimentalCoroutinesApi
    override suspend fun getImagePathByUrl(url: String): ResultModel<String> {
        val networkDispatcher = newSingleThreadContext("Network")
        val diskDispatcher = newSingleThreadContext("Disk")
        val localPathData = withContext(diskDispatcher) {
            imageLocalDataSource.getImagePathByUrl(url)
        }

        val result: ResultModel<String>

        if (localPathData is ResultModel.Success) {
            result = localPathData
        } else {
            val networkData: ResultModel<Bitmap> = withContext(networkDispatcher) {
                imageRemoteDataSource.downloadImageByUrl(url)
            }

            if (networkData is ResultModel.Success) {
                result = withContext(diskDispatcher) {
                    imageLocalDataSource.saveImageUrlAndGetPath(url, networkData.data)
                }
            } else {
                result = ResultModel.Error(
                    code = (networkData as ResultModel.Error).code,
                    message = networkData.message
                )
            }
        }
        return result
    }
}