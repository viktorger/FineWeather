package com.viktorger.fineweather.data.storage

import com.viktorger.fineweather.data.model.ConditionDataModel
import com.viktorger.fineweather.data.model.DayEnum
import com.viktorger.fineweather.data.model.ForecastDayDataModel
import com.viktorger.fineweather.data.model.HourDataModel
import com.viktorger.fineweather.data.model.LocationDataModel
import com.viktorger.fineweather.data.storage.retrofit.ForecastApi
import com.viktorger.fineweather.data.storage.retrofit.ForecastResponse
import com.viktorger.fineweather.data.storage.retrofit.Hour
import com.viktorger.fineweather.data.storage.retrofit.LocationResponse
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

class ForecastRemoteDataSource(
    private val forecastApi: ForecastApi
) {
    suspend fun getLocationInfo(): ResultModel<LocationDataModel> {
        val location = safeApiCall {
            forecastApi.getLocationInfo(
                "1858f254dc844b8fa6a224832232608",
                "auto:ip"
            )
        }
        return when (location) {
            is ResultModel.Success -> ResultModel.Success(
                locationToData(location.data)
            )

            is ResultModel.Error -> ResultModel.Error(
                code = location.code,
                message = location.message
            )

            else -> ResultModel.Error(
                code = -1,
                message = "Unknown error"
            )
        }
    }

    private fun locationToData(locationNetworkResponse: LocationResponse)
            : LocationDataModel = with(locationNetworkResponse.location) {
        LocationDataModel(
            name = "$name, $country",
            tzId = tz_id,
            lastUpdate = localtime_epoch
        )
    }

    suspend fun getEveryWeather(): ResultModel<List<ForecastDayDataModel>> {
        val forecastResponse: ResultModel<ForecastResponse> = safeApiCall {
            forecastApi.getForecast(
                "1858f254dc844b8fa6a224832232608",
                "auto:ip",
                DayEnum.TenDays.dayPos + 1
            )
        }

        return when (forecastResponse) {
            is ResultModel.Success -> {
                val today = getForecastDayModelWithHourListFromCurrentTime(forecastResponse.data)
                val tomorrow = dayForecastResponseToData(
                    forecastResponse.data,
                    DayEnum.Tomorrow.dayPos
                )

                val tenDaysList = mutableListOf<ForecastDayDataModel>()
                for (day in forecastResponse.data.forecast.forecastday.indices) {
                    val forecastDayModel = dayForecastResponseToData(
                        forecastResponse.data,
                        day
                    )
                    tenDaysList.add(forecastDayModel)
                }

                val resultDaysList = mutableListOf<ForecastDayDataModel>(today, tomorrow)
                resultDaysList.addAll(tenDaysList)

                ResultModel.Success(resultDaysList)
            }
            is ResultModel.Error -> ResultModel.Error(
                code = forecastResponse.code,
                message = forecastResponse.message
            )
            else -> ResultModel.Error(
                code = -1,
                message = "Unknown error"
            )
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>)
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


    private fun getForecastDayModelWithHourListFromCurrentTime(
        forecastNetworkResponseBody: ForecastResponse
    ): ForecastDayDataModel {
        val forecastToday = forecastNetworkResponseBody.forecast.forecastday[0]
        val forecastTomorrow = forecastNetworkResponseBody.forecast.forecastday[1]
        val hours = mutableListOf<Hour>()

        var firstToday: Int = 0
        while (firstToday < 24 && forecastNetworkResponseBody
                .location.localtime_epoch > forecastToday.hour[firstToday].time_epoch
        ) {
            firstToday++
        }
        firstToday--

        var hoursLeft = 24
        for (curIndex in firstToday..forecastToday.hour.lastIndex) {
            hours.add(forecastToday.hour[curIndex])
            hoursLeft--
        }

        var curIndex = 0
        while (hoursLeft > 0) {
            hours.add(forecastTomorrow.hour[curIndex])
            curIndex++
            hoursLeft--
        }

        return todayForecastResponseToData(forecastNetworkResponseBody, DayEnum.Today.dayPos, hours)
    }

    private fun todayForecastResponseToData(
        forecastResponse: ForecastResponse, index: Int, todayHours: List<Hour>
    ): ForecastDayDataModel {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        timeFormat.timeZone = TimeZone.getTimeZone(forecastResponse.location.tz_id)

        val dateFormat = SimpleDateFormat("dd MMMM, HH:mm", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone(forecastResponse.location.tz_id)

        with(forecastResponse.forecast.forecastday[index]) {
            return ForecastDayDataModel(
                date = getTimeString(dateFormat, forecastResponse.location.localtime_epoch),
                location = "${forecastResponse.location.name}, ${forecastResponse.location.country}",
                lastUpdate = forecastResponse.location.localtime_epoch,
                maxTempC = this.day.maxtemp_c.roundToInt(),
                minTempC = this.day.mintemp_c.roundToInt(),
                dailyChanceOfRain = this.day.daily_chance_of_rain,
                condition = ConditionDataModel(
                    text = forecastResponse.current.condition.text,
                    icon = "https:${forecastResponse.current.condition.icon}",
                ),
                status = forecastResponse.current.temp_c.roundToInt().toString(),
                hour = todayHours.map {
                    HourDataModel(
                        time = getTimeString(timeFormat, it.time_epoch),
                        timeEpoch = it.time_epoch,
                        tempC = it.temp_c.roundToInt(),
                        condition = ConditionDataModel(
                            text = it.condition.text,
                            icon = "https:${it.condition.icon}"
                        ),
                        chanceOfRain = it.chance_of_rain
                    )
                }
            )
        }
    }

    private fun dayForecastResponseToData(
        forecastResponse: ForecastResponse, index: Int
    ): ForecastDayDataModel {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        timeFormat.timeZone = TimeZone.getTimeZone(forecastResponse.location.tz_id)

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone(forecastResponse.location.tz_id)

        with(forecastResponse.forecast.forecastday[index]) {
            return ForecastDayDataModel(
                date = getTimeString(dateFormat, this.date_epoch),
                location = "${forecastResponse.location.name}, ${forecastResponse.location.country}",
                lastUpdate = forecastResponse.location.localtime_epoch,
                maxTempC = this.day.maxtemp_c.roundToInt(),
                minTempC = this.day.mintemp_c.roundToInt(),
                dailyChanceOfRain = this.day.daily_chance_of_rain,
                condition = ConditionDataModel(
                    text = this.day.condition.text,
                    icon = "https:${this.day.condition.icon}",
                ),
                status = this.day.condition.text,
                hour = this.hour.map {
                    HourDataModel(
                        time = getTimeString(timeFormat, it.time_epoch),
                        timeEpoch = it.time_epoch,
                        tempC = it.temp_c.roundToInt(),
                        condition = ConditionDataModel(
                            text = it.condition.text,
                            icon = "https:${it.condition.icon}"
                        ),
                        chanceOfRain = it.chance_of_rain
                    )
                }
            )
        }
    }

    private fun getTimeString(format: SimpleDateFormat, time: Int): String {
        return format.format(time * 1000L)
    }
}