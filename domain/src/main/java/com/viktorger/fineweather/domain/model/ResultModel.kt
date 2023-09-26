package com.viktorger.fineweather.domain.model

sealed class ResultModel<out T> {
    class Success<T>(val data: T): ResultModel<T>()
    class Error<T>(val code: Int, val message: String?): ResultModel<T>()
    data object Loading: ResultModel<Nothing>()
}