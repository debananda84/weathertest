package com.deb.wealthtest.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deb.wealthtest.data.model.WeatherResponse
import com.deb.wealthtest.repository.WeatherRepository
import com.deb.wealthtest.util.Event
import com.deb.wealthtest.util.State
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
): ViewModel() {
    private var _weatherResponse = MutableLiveData<Event<State<WeatherResponse>>>()
    val weatherResponse: LiveData<Event<State<WeatherResponse>>> = _weatherResponse

    var latLng = MutableLiveData<LatLng>()

    fun getWeatherDetails(query: String){
        if(query.isEmpty()){
            return
        }
        _weatherResponse.value = Event(State.Loading())
        viewModelScope.launch {
            val response = repository.getWeatherDetails(query)
            _weatherResponse.value = Event(response)
        }
    }
}