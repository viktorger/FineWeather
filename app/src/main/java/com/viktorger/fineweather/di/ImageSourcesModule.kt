package com.viktorger.fineweather.di

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
class ImageSourcesModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient()

    @Singleton
    @Provides
    fun provideCacheRootDir(context: Context): File = context.getDir("photos", Context.MODE_PRIVATE)

}