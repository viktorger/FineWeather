package com.viktorger.fineweather.data.storage

import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

internal const val KEY = "1858f254dc844b8fa6a224832232608"

internal suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>)
        : ResultModel<T> = withContext(Dispatchers.IO) {
    try {
        val apiCallResult = apiCall()

        if (apiCallResult.isSuccessful) {
            val apiCallResultBody = apiCallResult.body()!!
            ResultModel.Success(apiCallResultBody)
        } else {
            ResultModel.Error(
                code = apiCallResult.code(),
                message = apiCallResult.message()
            )
        }
    } catch (e: Exception) {
        ResultModel.Error(
            -1,
            "Network error"
        )
    }
}