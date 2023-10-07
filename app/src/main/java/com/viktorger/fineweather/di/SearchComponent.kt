package com.viktorger.fineweather.di

import com.viktorger.fineweather.presentation.search.LocationSearchActivity
import dagger.Subcomponent

@Subcomponent
interface SearchComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): SearchComponent
    }

    fun inject(activity: LocationSearchActivity)
}