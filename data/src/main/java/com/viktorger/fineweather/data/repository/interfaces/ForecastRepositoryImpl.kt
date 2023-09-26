package com.viktorger.fineweather.data.repository.interfaces

import android.util.Log
import com.viktorger.fineweather.data.model.ForecastResponse
import com.viktorger.fineweather.data.model.Hour
import com.viktorger.fineweather.data.source.retrofit.ForecastApi
import com.viktorger.fineweather.domain.interfaces.ForecastRepository
import com.viktorger.fineweather.domain.model.ConditionModel
import com.viktorger.fineweather.domain.model.ForecastDayModel
import com.viktorger.fineweather.domain.model.HourModel
import com.viktorger.fineweather.domain.model.ResultModel
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

class ForecastRepositoryImpl(private val forecastApi: ForecastApi) : ForecastRepository {

    // Pls make better code :)
    override suspend fun getWeatherDay(day: Int): ResultModel<ForecastDayModel> {
        val forecastNetworkResponse: Response<ForecastResponse> = try {
            forecastApi.getForecast(
                "1858f254dc844b8fa6a224832232608",
                "auto:ip",
                day + 1
            )
        } catch (e: Exception) {
            return ResultModel.Error(-1, "Network error")
        }

        val forecastResultModel: ResultModel<ForecastDayModel> =
            if (forecastNetworkResponse.isSuccessful) {
                ResultModel.Success(
                    dayForecastResponseToDomain(forecastNetworkResponse.body()!!, day)
                )
            } else {
                ResultModel.Error(
                    forecastNetworkResponse.code(),
                    forecastNetworkResponse.errorBody()?.string()
                )
            }

        return forecastResultModel
    }

    override suspend fun getWeatherToday(): ResultModel<ForecastDayModel> {
        val forecastNetworkResponse: Response<ForecastResponse> = try {
            forecastApi.getForecast(
                "1858f254dc844b8fa6a224832232608",
                "auto:ip",
                2
            )
        } catch (e: Exception) {
            return ResultModel.Error(-1, "Network error")
        }

        val forecastResultModel: ResultModel<ForecastDayModel> =
            if (forecastNetworkResponse.isSuccessful) {
                getSuccessWithHourListFromCurrentTime(forecastNetworkResponse)
            } else {
                ResultModel.Error(
                    forecastNetworkResponse.code(),
                    forecastNetworkResponse.errorBody()?.string()
                )
            }

        return forecastResultModel
    }

    override suspend fun getWeatherTenDays(): ResultModel<List<ForecastDayModel>> {
        val forecastNetworkResponse: Response<ForecastResponse> = try {
            forecastApi.getForecast(
                "1858f254dc844b8fa6a224832232608",
                "auto:ip",
                10
            )
        } catch (e: Exception) {
            return ResultModel.Error(-1, "Network error")
        }

        forecastNetworkResponse.body()!!.forecast.forecastday.forEach {
            Log.d("repo_method", it.date)

        }

        val forecastResultModel: ResultModel<List<ForecastDayModel>> =
            if (forecastNetworkResponse.isSuccessful) {
                val dayList = mutableListOf<ForecastDayModel>()
                val forecastNetworkResponseBody = forecastNetworkResponse.body()!!
                for (day in forecastNetworkResponseBody.forecast.forecastday.indices) {
                    val forecastDayModel = dayForecastResponseToDomain(
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
                    forecastNetworkResponse.code(),
                    forecastNetworkResponse.errorBody()?.string()
                )
            }

        return forecastResultModel
    }

    private fun getSuccessWithHourListFromCurrentTime(
        forecastNetworkResponse: Response<ForecastResponse>
    ): ResultModel.Success<ForecastDayModel> {
        val forecastNetworkResponseBody = forecastNetworkResponse.body()!!
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

        return ResultModel.Success(
            todayForecastResponseToDomain(forecastNetworkResponse.body()!!, 0, hours)
        )
    }

    private fun todayForecastResponseToDomain(
        forecastResponse: ForecastResponse, index: Int, todayHours: List<Hour>
    ): ForecastDayModel {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        timeFormat.timeZone = TimeZone.getTimeZone(forecastResponse.location.tz_id)

        val dateFormat = SimpleDateFormat("dd MMMM, HH:mm", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone(forecastResponse.location.tz_id)

        with(forecastResponse.forecast.forecastday[index]) {
            return ForecastDayModel(
                date = getTimeString(dateFormat, forecastResponse.location.localtime_epoch),
                maxtemp_c = this.day.maxtemp_c.roundToInt(),
                mintemp_c = this.day.mintemp_c.roundToInt(),
                daily_chance_of_rain = this.day.daily_chance_of_rain,
                condition =  ConditionModel(
                    text = forecastResponse.current.condition.text,
                    icon = "https:${forecastResponse.current.condition.icon}",
                ),
                status = forecastResponse.current.temp_c.roundToInt().toString(),
                hour = todayHours.map {
                    HourModel(
                        time = getTimeString(timeFormat, it.time_epoch),
                        temp_c = it.temp_c.roundToInt(),
                        condition = ConditionModel(
                            text = it.condition.text,
                            icon = "https:${it.condition.icon}"
                        ),
                        chance_of_rain = it.chance_of_rain
                    )
                }
            )
        }
    }

    private fun dayForecastResponseToDomain(
        forecastResponse: ForecastResponse, index: Int
    ): ForecastDayModel {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
        timeFormat.timeZone = TimeZone.getTimeZone(forecastResponse.location.tz_id)

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone(forecastResponse.location.tz_id)

        with(forecastResponse.forecast.forecastday[index]) {
            return ForecastDayModel(
                date = getTimeString(dateFormat, this.date_epoch),
                maxtemp_c = this.day.maxtemp_c.roundToInt(),
                mintemp_c = this.day.mintemp_c.roundToInt(),
                daily_chance_of_rain = this.day.daily_chance_of_rain,
                condition =  ConditionModel(
                    text = this.day.condition.text,
                    icon = "https:${this.day.condition.icon}",
                ),
                status = this.day.condition.text,
                hour = this.hour.map {
                    HourModel(
                        time = getTimeString(timeFormat, it.time_epoch),
                        temp_c = it.temp_c.roundToInt(),
                        condition = ConditionModel(
                            text = it.condition.text,
                            icon = "https:${it.condition.icon}"
                        ),
                        chance_of_rain = it.chance_of_rain
                    )
                }
            )
        }
    }

    private fun getTimeString(format: SimpleDateFormat, time: Int): String {
        return format.format(time * 1000L)
    }

}