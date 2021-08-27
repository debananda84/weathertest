package com.deb.wealthtest.data

import com.deb.wealthtest.BuildConfig
import com.deb.wealthtest.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("current")
    suspend fun getWeatherDetails(
        @Query("access_key") accessKey: String = BuildConfig.API_KEY,
        @Query("query") query: String
    ): Response<WeatherResponse>
}