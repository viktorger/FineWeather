package com.viktorger.fineweather.data.storage

import android.util.Log
import com.viktorger.fineweather.data.model.ConditionDataModel
import com.viktorger.fineweather.data.model.DayEnum
import com.viktorger.fineweather.data.model.ForecastDayDataModel
import com.viktorger.fineweather.data.model.HourDataModel
import com.viktorger.fineweather.data.model.TenDaysEnum
import com.viktorger.fineweather.data.storage.room.LocalDatabase
import com.viktorger.fineweather.data.storage.room.entities.DayEntity
import com.viktorger.fineweather.data.storage.room.entities.HourEntity
import com.viktorger.fineweather.data.storage.room.relationships.DayWithHours
import com.viktorger.fineweather.domain.model.ResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ForecastLocalDataSource @Inject constructor(
    private val localDatabase: LocalDatabase
) {

    suspend fun getForecastToday(): ResultModel<ForecastDayDataModel> {
        val dayDao = localDatabase.dayDao()
        val localResult: List<DayWithHours> = withContext(Dispatchers.IO) {
            dayDao.getDayWithHour(DayEnum.Today.dayPos)
        }

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
        val localResult: List<DayWithHours> = withContext(Dispatchers.IO) {
            dayDao.getDayWithHour(DayEnum.Tomorrow.dayPos)
        }

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
        val localResult: List<DayWithHours> = withContext(Dispatchers.IO) {
            dayDao.getDayWithHour(
                *range.toList().toIntArray()
            )
        }

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

    private suspend fun saveForecastDay(forecastDay: ForecastDayDataModel, day: Int) {
        val dayDao = localDatabase.dayDao()
        val hourDao = localDatabase.hourDao()

        val dayWithHours = dataToLocalData(forecastDay, day)

        withContext(Dispatchers.IO) {
            dayDao.insertDay(dayWithHours.day)
            hourDao.deleteHoursByDay(day)
            hourDao.insertHour(dayWithHours.hours)
        }
    }

    suspend fun saveForecastDayList(forecastDays: List<ForecastDayDataModel>) {
        forecastDays.forEachIndexed { index, it ->
            saveForecastDay(it, index)
        }
    }

    private fun localDataToData(dayWithHours: DayWithHours) = ForecastDayDataModel(
        date = dayWithHours.day.date,
        location = dayWithHours.day.locationName,
        tzId = dayWithHours.day.tzId,
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
                timeEpoch = it.timeEpoch,
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

    private fun dataToLocalData(forecastDayDataModel: ForecastDayDataModel, day: Int) = with(forecastDayDataModel) {
        DayWithHours(
            day = DayEntity(
                day = day,
                locationName = location,
                lastUpdate = lastUpdate,
                tzId = forecastDayDataModel.tzId,
                date = date,
                maxTempC = maxTempC,
                minTempC = minTempC,
                status = status,
                dailyChanceOfRain = dailyChanceOfRain,
                conditionText = condition.text,
                conditionIcon = condition.icon
            ),
            hours = hour.map {
                HourEntity(
                    timeEpoch = it.timeEpoch,
                    time = it.time,
                    tempC = it.tempC,
                    chanceOfRain = it.chanceOfRain,
                    day = day,
                    conditionText = it.condition.text,
                    conditionIcon = it.condition.icon
                )
            }
        )
    }
}