package com.deb.wealthtest.repository

import com.deb.wealthtest.data.ApiService
import com.deb.wealthtest.data.model.WeatherResponse
import com.deb.wealthtest.util.State
import java.lang.Error
import java.lang.Exception
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getWeatherDetails(query: String): State<WeatherResponse> {
        return try{
            val result = apiService.getWeatherDetails(query = query)
            if(result.isSuccessful){
                result.body()?.let {
                    return@let State.Success(it)
                }?:  State.Error("An unknown error ")
            }else{
                State.Error("An unknown error")
            }
        }catch (e: Exception){
            State.Error("Could not reach the Server, Please try again later")
        }
    }
}