package com.viktorger.fineweather.data.storage

import android.util.Log
import com.viktorger.fineweather.data.model.ConditionDataModel
import com.viktorger.fineweather.data.model.DayEnum
import com.viktorger.fineweather.data.model.ForecastDayDataModel
import com.viktorger.fineweather.data.model.HourDataModel
import com.viktorger.fineweather.data.model.SearchedLocationDataModel
import com.viktorger.fineweather.data.storage.retrofit.ForecastApi
import com.viktorger.fineweather.data.storage.retrofit.ForecastResponse
import com.viktorger.fineweather.data.storage.retrofit.Hour
import com.viktorger.fineweather.domain.model.ResultModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlin.math.roundToInt

class ForecastRemoteDataSource @Inject constructor(
    private val forecastApi: ForecastApi
) {
    suspend fun getEveryWeather(
        locationModel: SearchedLocationDataModel
    ): ResultModel<List<ForecastDayDataModel>> {
        val forecastResponse: ResultModel<ForecastResponse> = safeApiCall {
            forecastApi.getForecast(
                KEY,
                locationModel.coordinates,
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

                val resultDaysList = mutableListOf(today, tomorrow)
                resultDaysList.addAll(tenDaysList)

                resultDaysList.map { it.apply {
                    location = locationModel.locationName
                } }

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


    private fun getForecastDayModelWithHourListFromCurrentTime(
        forecastNetworkResponseBody: ForecastResponse
    ): ForecastDayDataModel {
        val forecastToday = forecastNetworkResponseBody.forecast.forecastday[0]
        val forecastTomorrow = forecastNetworkResponseBody.forecast.forecastday[1]
        val hours = mutableListOf<Hour>()

        var firstToday = 0
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
                location = "${forecastResponse.location.country}, ${forecastResponse.location.region}, ${forecastResponse.location.name}",
                lastUpdate = forecastResponse.location.localtime_epoch,
                tzId = forecastResponse.location.tz_id,
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
        Log.d("dayForecastResponseTOdata", forecastResponse.location.tz_id)
        Log.d("dayForecastResponseTOdata", "${forecastResponse.forecast.forecastday[index].date_epoch} ${getTimeString(dateFormat, forecastResponse.forecast.forecastday[index].date_epoch)}")

        with(forecastResponse.forecast.forecastday[index]) {
            return ForecastDayDataModel(
                date = getTimeString(dateFormat, this.date_epoch),
                location = "${forecastResponse.location.country}, ${forecastResponse.location.region}, ${forecastResponse.location.name}",
                lastUpdate = forecastResponse.location.localtime_epoch,
                tzId = forecastResponse.location.tz_id,
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