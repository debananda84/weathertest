package com.deb.wealthtest.di

import com.deb.wealthtest.data.ApiService
import com.deb.wealthtest.repository.WeatherRepository
import com.deb.wealthtest.util.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideApiService():ApiService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constant.BASE_URL)
        .build()
        .create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideWeatherRepository( apiService: ApiService ) = WeatherRepository(apiService)
}