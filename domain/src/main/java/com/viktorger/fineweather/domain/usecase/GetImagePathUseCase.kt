package com.viktorger.fineweather.domain.usecase

import com.viktorger.fineweather.domain.interfaces.ImageRepository
import com.viktorger.fineweather.domain.model.ResultModel
import javax.inject.Inject

class GetImagePathUseCase @Inject constructor(private val imageRepository: ImageRepository) {

    suspend operator fun invoke(url: String): ResultModel<String> =
        imageRepository.getImagePathByUrl(url)

}