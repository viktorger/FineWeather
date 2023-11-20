package com.viktorger.fineweather.data.storage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.viktorger.fineweather.domain.model.ResultModel
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class ImageRemoteDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient
) {

    suspend fun downloadImageByUrl(url: String): ResultModel<Bitmap> = try {

        val request = Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()

        if (response.isSuccessful) {
            val inputStream = response.body!!.byteStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)

            ResultModel.Success(bitmap)
        } else {
            ResultModel.Error(
                code = response.code,
                message = response.message
            )
        }
    } catch (e: Exception) {
        ResultModel.Error(
            -1,
            "Network error"
        )
    }


}