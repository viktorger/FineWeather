package com.viktorger.fineweather.data.repository

import com.viktorger.fineweather.data.model.DayEnum
import com.viktorger.fineweather.data.model.ForecastDayDataModel
import com.viktorger.fineweather.data.model.TenDaysEnum
import com.viktorger.fineweather.data.storage.ForecastLocalDataSource
import com.viktorger.fineweather.data.storage.ForecastRemoteDataSource
import com.viktorger.fineweather.domain.interfaces.ForecastRepository
import com.viktorger.fineweather.domain.model.ConditionModel
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.HourModel
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class ForecastRepositoryImpl @Inject constructor(
    private val forecastLocalDataSource: ForecastLocalDataSource,
    private val forecastRemoteDataSource: ForecastRemoteDataSource
) : ForecastRepository {

    override fun getWeatherToday(forceUpdate: Boolean): Flow<ResultModel<ForecastDayModel>> = flow {
        val localResult = forecastLocalDataSource.getForecastToday()

        if (localResult is ResultModel.Success && !forceUpdate) {
            val forecastResultModel: ResultModel<ForecastDayModel> =
                ResultModel.Success(
                    forecastDayToDomain(localResult.data)
                )

            emit(forecastResultModel)
        }

        if (localResult is ResultModel.Error
            || forceUpdate
            || isNeededToBeUpdated((localResult as ResultModel.Success).data)) {
            val forecastNetworkResult = forecastRemoteDataSource.getEveryWeather()
            val forecastResultModel: ResultModel<ForecastDayModel>

            when (forecastNetworkResult) {
                is ResultModel.Success -> {
                    forecastResultModel = ResultModel.Success(
                        forecastDayToDomain(forecastNetworkResult.data[DayEnum.Today.dayPos])
                    )

                    forecastLocalDataSource.saveForecastDayList(forecastNetworkResult.data)
                }

                else -> {
                    // Network result is nothing but Error
                    forecastResultModel = forecastResponseToDomainError(
                        forecastNetworkResult as ResultModel.Error
                    )
                }
            }

            emit(forecastResultModel)
        }
    }

    override fun getWeatherTomorrow(forceUpdate: Boolean): Flow<ResultModel<ForecastDayModel>> = flow {
        val localResult = forecastLocalDataSource.getForecastTomorrow()

        if (localResult is ResultModel.Success && !forceUpdate) {
            val forecastResultModel: ResultModel<ForecastDayModel> =
                ResultModel.Success(
                    forecastDayToDomain(localResult.data)
                )

            emit(forecastResultModel)
        }

        if (localResult is ResultModel.Error
            || forceUpdate
            || isNeededToBeUpdated((localResult as ResultModel.Success).data)) {

            val forecastNetworkResult = forecastRemoteDataSource.getEveryWeather()
            val forecastResultModel: ResultModel<ForecastDayModel>

            when (forecastNetworkResult) {
                is ResultModel.Success -> {
                    forecastResultModel = ResultModel.Success(
                        forecastDayToDomain(forecastNetworkResult.data[DayEnum.Tomorrow.dayPos])
                    )

                    forecastLocalDataSource.saveForecastDayList(forecastNetworkResult.data)
                }

                else -> {
                    // Network result is nothing but Error
                    forecastResultModel = forecastResponseToDomainError(
                        forecastNetworkResult as ResultModel.Error
                    )
                }
            }

            emit(forecastResultModel)
        } else {
            // Local result is Success
            val forecastResultModel: ResultModel<ForecastDayModel> =
                ResultModel.Success(
                    forecastDayToDomain(localResult.data)
                )

            emit(forecastResultModel)
        }
    }

    override fun getWeatherTenDays(forceUpdate: Boolean): Flow<ResultModel<List<ForecastDayModel>>> = flow {
        val localResult = forecastLocalDataSource.getForecastTenDays()

        if (localResult is ResultModel.Success && !forceUpdate) {
            val forecastResultModel: ResultModel<List<ForecastDayModel>> =
                ResultModel.Success(
                    localResult.data.map { forecastDayToDomain(it) }
                )

            emit(forecastResultModel)
        }

        if (localResult is ResultModel.Error
            || forceUpdate
            || isNeededToBeUpdated((localResult as ResultModel.Success).data[0])) {
            val forecastNetworkResult = forecastRemoteDataSource.getEveryWeather()
            val forecastResultModel: ResultModel<List<ForecastDayModel>>

            when (forecastNetworkResult) {
                is ResultModel.Success -> {
                    forecastResultModel = ResultModel.Success(
                        forecastNetworkResult.data.subList(
                            TenDaysEnum.First.dayPos,
                            forecastNetworkResult.data.size
                            ).map { forecastDayToDomain(it) }
                    )

                    forecastLocalDataSource.saveForecastDayList(forecastNetworkResult.data)
                }

                else -> {
                    // Network result is nothing but Error
                    forecastResultModel = forecastResponseToDomainError(
                        forecastNetworkResult as ResultModel.Error
                    )
                }
            }
            emit(forecastResultModel)

        }
    }

    /*private fun <T> Flow<ResultModel<T>>.handleErrors()
            : Flow<ResultModel<T>> = flow {
        try {
            collect { value -> emit(value) }
        } catch (e: Throwable) {
            emit(ResultModel.Error(-1, "Network error"))
        }
    }*/

    private fun <T, K> forecastResponseToDomainError(
        forecastNetworkResponse: ResultModel.Error<T>
    ): ResultModel.Error<K> {

        return ResultModel.Error(
            forecastNetworkResponse.code,
            forecastNetworkResponse.message
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

            /*Log.d("IsNeeded", """
                ${forecastDay.location} and ${internetData.name}
                ${forecastDay.lastUpdate - internetData.lastUpdate}
                ${getTimeString(simpleDateFormat, forecastDay.lastUpdate)} ${getTimeString(simpleDateFormat, internetData.lastUpdate)}
            """.trimIndent())*/

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