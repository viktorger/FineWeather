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
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

class ForecastRemoteDataSource(
    private val forecastApi: ForecastApi
) {
    suspend fun getLocationInfo(): ResultModel<LocationDataModel> {
        val locationNetworkResponse: Response<LocationResponse> = forecastApi.getLocationInfo(
            "1858f254dc844b8fa6a224832232608",
            "auto:ip"
        )

        return if (locationNetworkResponse.isSuccessful) {
            ResultModel.Success(
                locationToData(locationNetworkResponse.body()!!)
            )
        } else {
            ResultModel.Error(
                code = locationNetworkResponse.code(),
                message = locationNetworkResponse.errorBody().toString()
            )
        }
    }

    private fun locationToData(locationNetworkResponse: LocationResponse)
    : LocationDataModel = with(locationNetworkResponse.location) {
        LocationDataModel(
            name = name,
            tzId = tz_id,
            lastUpdate = localtime_epoch
        )
    }

    suspend fun getForecastToday(): ResultModel<ForecastDayDataModel> {
        val forecastNetworkResponse: Response<ForecastResponse> = forecastApi.getForecast(
            "1858f254dc844b8fa6a224832232608",
            "auto:ip",
            DayEnum.Tomorrow.dayPos + 1
        )

        return if (forecastNetworkResponse.isSuccessful) {
            ResultModel.Success(
                getForecastDayModelWithHourListFromCurrentTime(forecastNetworkResponse.body()!!)
            )
        } else {
            ResultModel.Error(
                code = forecastNetworkResponse.code(),
                message = forecastNetworkResponse.errorBody().toString()
            )
        }
    }

    suspend fun getForecastTomorrow(): ResultModel<ForecastDayDataModel> {
        val forecastNetworkResponse: Response<ForecastResponse> = forecastApi.getForecast(
            "1858f254dc844b8fa6a224832232608",
            "auto:ip",
            DayEnum.Tomorrow.dayPos + 1
        )

        return if (forecastNetworkResponse.isSuccessful) {
            ResultModel.Success(
                dayForecastResponseToData(forecastNetworkResponse.body()!!, DayEnum.Tomorrow.dayPos)
            )
        } else {
            ResultModel.Error(
                code = forecastNetworkResponse.code(),
                message = forecastNetworkResponse.errorBody().toString()
            )
        }
    }

    suspend fun getForecastTenDays(): ResultModel<List<ForecastDayDataModel>> {
        val forecastNetworkResponse: Response<ForecastResponse> = forecastApi.getForecast(
            "1858f254dc844b8fa6a224832232608",
            "auto:ip",
            DayEnum.TenDays.dayPos + 1
        )

        val forecastResultModel: ResultModel<List<ForecastDayDataModel>> =
            if (forecastNetworkResponse.isSuccessful) {
                val dayList = mutableListOf<ForecastDayDataModel>()
                val forecastNetworkResponseBody = forecastNetworkResponse.body()!!

                for (day in forecastNetworkResponseBody.forecast.forecastday.indices) {
                    val forecastDayModel = dayForecastResponseToData(
                        forecastNetworkResponseBody,
                        day
                    )
                    dayList.add(forecastDayModel)
                }

                ResultModel.Success(
                    dayList
                )
            } else {
                ResultModel.Error(
                    code = forecastNetworkResponse.code(),
                    message = forecastNetworkResponse.errorBody()?.string()
                )
            }

        return forecastResultModel
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
                condition =  ConditionDataModel(
                    text = forecastResponse.current.condition.text,
                    icon = "https:${forecastResponse.current.condition.icon}",
                ),
                status = forecastResponse.current.temp_c.roundToInt().toString(),
                hour = todayHours.map {
                    HourDataModel(
                        time = getTimeString(timeFormat, it.time_epoch),
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
                condition =  ConditionDataModel(
                    text = this.day.condition.text,
                    icon = "https:${this.day.condition.icon}",
                ),
                status = this.day.condition.text,
                hour = this.hour.map {
                    HourDataModel(
                        time = getTimeString(timeFormat, it.time_epoch),
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