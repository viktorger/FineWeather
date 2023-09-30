package com.viktorger.fineweather.data.repository.interfaces

import com.viktorger.fineweather.data.model.ForecastDayDataModel
import com.viktorger.fineweather.data.storage.ForecastLocalDataSource
import com.viktorger.fineweather.data.storage.ForecastRemoteDataSource
import com.viktorger.fineweather.data.storage.retrofit.Forecast
import com.viktorger.fineweather.domain.interfaces.ForecastRepository
import com.viktorger.fineweather.domain.model.ConditionModel
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.HourModel
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class ForecastRepositoryImpl(
    private val forecastLocalDataSource: ForecastLocalDataSource,
    private val forecastRemoteDataSource: ForecastRemoteDataSource
) : ForecastRepository {

    override suspend fun getWeatherToday(): Flow<ResultModel<ForecastDayModel>> = flow {
        val localResult = forecastLocalDataSource.getForecastToday()

        if (localResult is ResultModel.Error
            || isNeededToBeUpdated((localResult as ResultModel.Success).data)) {
            val forecastNetworkResult = forecastRemoteDataSource.getForecastToday()
            val forecastResultModel: ResultModel<ForecastDayModel>

            when (forecastNetworkResult) {
                is ResultModel.Success -> {
                    forecastResultModel = forecastResponseToDomainSuccess(forecastNetworkResult)
                }

                else -> {
                    // Network result is nothing but Error
                    forecastResultModel = forecastResponseToDomainError(forecastNetworkResult)
                }
            }

            emit(forecastResultModel)
        } else {
            // Local result is Success
            val forecastResultModel: ResultModel<ForecastDayModel> =
                forecastResponseToDomainSuccess(localResult)

            emit(forecastResultModel)
        }
    }
        .handleErrors()

    override suspend fun getWeatherTomorrow(): Flow<ResultModel<ForecastDayModel>> = flow {
        val localResult = forecastLocalDataSource.getForecastTomorrow()

        if (localResult is ResultModel.Error
            || isNeededToBeUpdated((localResult as ResultModel.Success).data)) {

            val forecastNetworkResult = forecastRemoteDataSource.getForecastTomorrow()
            val forecastResultModel: ResultModel<ForecastDayModel>

            when (forecastNetworkResult) {
                is ResultModel.Success -> {
                    forecastResultModel = forecastResponseToDomainSuccess(forecastNetworkResult)
                }

                else -> {
                    // Network result is nothing but Error
                    forecastResultModel = forecastResponseToDomainError(forecastNetworkResult)
                }
            }

            emit(forecastResultModel)
        } else {
            // Local result is Success
            val forecastResultModel: ResultModel<ForecastDayModel> =
                forecastResponseToDomainSuccess(localResult)

            emit(forecastResultModel)
        }
    }
        .handleErrors()

    override suspend fun getWeatherTenDays(): Flow<ResultModel<List<ForecastDayModel>>> = flow {
        val localResult = forecastLocalDataSource.getForecastTenDays()

        if (localResult is ResultModel.Error
            || isNeededToBeUpdated((localResult as ResultModel.Success).data[0])) {
            val forecastNetworkResult = forecastRemoteDataSource.getForecastTenDays()
            val forecastResultModel: ResultModel<List<ForecastDayModel>>

            when (forecastNetworkResult) {
                is ResultModel.Success -> {
                    forecastResultModel = ResultModel.Success(
                        forecastNetworkResult.data.map { forecastDayToDomain(it) }
                    )
                }

                else -> {
                    // Network result is nothing but Error
                    forecastResultModel = forecastResponseToDomainError(forecastNetworkResult)
                }
            }

            emit(forecastResultModel)
        } else {
            // Local result is Success
            val forecastResultModel: ResultModel<List<ForecastDayModel>> =
                ResultModel.Success(
                    (localResult as ResultModel.Success).data.map { forecastDayToDomain(it) }
                )

            emit(forecastResultModel)
        }
    }
        .handleErrors()

    private fun <T> Flow<ResultModel<T>>.handleErrors()
            : Flow<ResultModel<T>> = flow {
        try {
            collect { value -> emit(value) }
        } catch (e: Throwable) {
            emit(ResultModel.Error(-1, "Network error"))
        }
    }

    private fun <T, K> forecastResponseToDomainError(
        forecastNetworkResponse: ResultModel<T>
    ): ResultModel.Error<K> {
        val forecastNetworkError = forecastNetworkResponse as ResultModel.Error

        return ResultModel.Error(
            forecastNetworkError.code,
            forecastNetworkError.message
        )
    }

    private fun forecastResponseToDomainSuccess(
        forecastNetworkResponse: ResultModel<ForecastDayDataModel>
    ): ResultModel<ForecastDayModel> {
        val forecastNetworkSuccess = forecastNetworkResponse as ResultModel.Success

        return ResultModel.Success(
            forecastDayToDomain(forecastNetworkSuccess.data)
        )
    }

    private fun forecastDayToDomain(
        forecastDayDataModel: ForecastDayDataModel,
    ): ForecastDayModel = ForecastDayModel(
        date = forecastDayDataModel.date,
        maxTempC = forecastDayDataModel.maxTempC,
        minTempC = forecastDayDataModel.minTempC,
        dailyChanceOfRain = forecastDayDataModel.dailyChanceOfRain,
        condition = ConditionModel(
            text = forecastDayDataModel.condition.text,
            icon = forecastDayDataModel.condition.icon,
        ),
        status = forecastDayDataModel.status,
        hour = forecastDayDataModel.hour.map {
            HourModel(
                time = it.time,
                tempC = it.tempC,
                condition = ConditionModel(
                    text = it.condition.text,
                    icon = it.condition.icon
                ),
                chanceOfRain = it.chanceOfRain
            )
        }
    )

    /*
    * Logic for fetching once a day
    *
    * We check if the day numbers are the same and if the difference in time is not
    * more than 1 day. If so, fetch the data
    */
    private suspend fun isNeededToBeUpdated(forecastDay: ForecastDayDataModel): Boolean {
        val simpleDateFormat = SimpleDateFormat("dd", Locale.US)
        val secondsInADay = 86_400L

        val localInfo = forecastRemoteDataSource.getLocationInfo()
        if (localInfo is ResultModel.Success) {
            val internetData = localInfo.data

            return !(forecastDay.location == internetData.name
                    && abs(forecastDay.lastUpdate - internetData.lastUpdate) < secondsInADay
                    && getTimeString(simpleDateFormat, forecastDay.lastUpdate) == getTimeString(simpleDateFormat, internetData.lastUpdate))
        }
        return false
    }

    private fun getTimeString(format: SimpleDateFormat, time: Int): String {
        return format.format(time * 1000L)
    }
}