package com.viktorger.fineweather.di

import com.viktorger.fineweather.data.repository.ForecastRepositoryImpl
import com.viktorger.fineweather.data.repository.LocationRepositoryImpl
import com.viktorger.fineweather.domain.interfaces.ForecastRepository
import com.viktorger.fineweather.domain.interfaces.LocationRepository
import dagger.Binds
import dagger.Module

@Module
abstract class ForecastModule {

    @Binds
    abstract fun provideForecastRepository(repository: ForecastRepositoryImpl): ForecastRepository

    @Binds
    abstract fun provideLocationRepository(repository: LocationRepositoryImpl): LocationRepository
}