package com.viktorger.fineweather.data.storage

import com.viktorger.fineweather.data.model.ConditionDataModel
import com.viktorger.fineweather.data.model.DayEnum
import com.viktorger.fineweather.data.model.ForecastDayDataModel
import com.viktorger.fineweather.data.model.HourDataModel
import com.viktorger.fineweather.data.model.LocationDataModel
import com.viktorger.fineweather.data.model.TenDaysEnum
import com.viktorger.fineweather.data.storage.room.LocalDatabase
import com.viktorger.fineweather.data.storage.room.relationships.DayWithHours
import com.viktorger.fineweather.domain.model.ResultModel

class ForecastLocalDataSource(
    private val localDatabase: LocalDatabase
) {

    suspend fun getForecastToday(): ResultModel<ForecastDayDataModel> {
        val dayDao = localDatabase.dayDao()
        val localResult: List<DayWithHours> = dayDao.getDayWithHour(DayEnum.Today.dayPos)

        return if (localResult.isEmpty()) {
            ResultModel.Error(
                -1,
                "No local data found"
            )
        } else {
            ResultModel.Success(
                localDataToData(localResult[0])
            )
        }
    }

    suspend fun getForecastTomorrow(): ResultModel<ForecastDayDataModel> {
        val dayDao = localDatabase.dayDao()
        val localResult: List<DayWithHours> = dayDao.getDayWithHour(DayEnum.Tomorrow.dayPos)

        return if (localResult.isEmpty()) {
            ResultModel.Error(
                -1,
                "No local data found"
            )
        } else {
            ResultModel.Success(
                localDataToData(localResult[0])
            )
        }
    }

    suspend fun getForecastTenDays(): ResultModel<List<ForecastDayDataModel>> {
        val dayDao = localDatabase.dayDao()
        val range = TenDaysEnum.First.dayPos..TenDaysEnum.Last.dayPos
        val localResult: List<DayWithHours> = dayDao.getDayWithHour(
            *range.toList().toIntArray()
        )

        return if (localResult.isEmpty()) {
            ResultModel.Error(
                -1,
                "No local data found"
            )
        } else {
            val dayList = mutableListOf<ForecastDayDataModel>()
            localResult.forEach {
                dayList.add(localDataToData(it))
            }

            ResultModel.Success(
                dayList
            )
        }
    }

    suspend fun saveWeatherToday(forecastDay: ForecastDayDataModel, day: Int) {
        val dayDao = localDatabase.dayDao()
        val hourDao = localDatabase.hourDao()

        // dayDao.insertDay(forecastResponse)
    }

    private fun localDataToData(dayWithHours: DayWithHours) = ForecastDayDataModel(
        date = dayWithHours.day.date,
        location = dayWithHours.day.locationName,
        lastUpdate = dayWithHours.day.lastUpdate,
        maxTempC = dayWithHours.day.maxTempC,
        minTempC = dayWithHours.day.minTempC,
        dailyChanceOfRain = dayWithHours.day.dailyChanceOfRain,
        condition =  ConditionDataModel(
            text = dayWithHours.day.conditionText,
            icon = dayWithHours.day.conditionIcon,
        ),
        status = dayWithHours.day.status,
        hour = dayWithHours.hours.map {
            HourDataModel(
                time = it.time,
                tempC = it.tempC,
                condition = ConditionDataModel(
                    text = it.conditionText,
                    icon = it.conditionIcon
                ),
                chanceOfRain = it.chanceOfRain
            )
        }
    )
}