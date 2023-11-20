package com.viktorger.fineweather.domain.interfaces

import com.viktorger.fineweather.domain.model.ResultModel

interface ImageRepository {

    suspend fun getImagePathByUrl(url: String): ResultModel<String>

}