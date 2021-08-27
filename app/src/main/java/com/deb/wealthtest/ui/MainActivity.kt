package com.deb.wealthtest.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.deb.wealthtest.data.model.WeatherResponse
import com.deb.wealthtest.databinding.ActivityMainBinding
import com.deb.wealthtest.util.State
import com.deb.wealthtest.util.toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import com.deb.wealthtest.R
import com.deb.wealthtest.util.LocaleHelper


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        getLastLocation(this@MainActivity, fusedLocationClient)
        setUpObserver()
        binding.hindi.setOnClickListener {
            chnageLanguage("hi")
        }
        binding.english.setOnClickListener {
            chnageLanguage("en")
        }
    }
    private fun setUpObserver(){
        viewModel.latLng.observe(this, Observer {
             viewModel.getWeatherDetails("${it.latitude}, ${it.longitude}")
        })
        viewModel.weatherResponse.observe(this, Observer {
            it.getContentIfNotHandled()?.let { state ->
                when(state){
                    is State.Loading ->  showLoading()
                    is State.Success -> {
                        Log.d("TAG", "setObserver: ${state.data}")
                        state.data?.let {
                            updateUI(it)
                        }
                        hideLoading()
                    }
                    is State.Error ->{
                        toast(state.msg!!)
                        hideLoading()
                    }
                }
            }
        })
    }

    private fun updateUI(response: WeatherResponse){
        response.location?.let {
            binding.city.text = it.name
            binding.country.text = it.country
            binding.day.text = it.localtime?.let { date->
                formatDate(date)
            }
        }

        response.current?.let {
            it.weatherIcons?.let { icons ->
                if(icons.isNotEmpty()){
                    Picasso.get().load(icons[0]).into(binding.weathericon)
                }
            }
            var degree = getString(R.string.degree)
            binding.temp.text = "${it.temperature}${degree}C"
            it.weatherDescriptions?.let { description ->
                if(description.isNotEmpty()){
                    binding.cloud.text = description[0]
                }
            }
            binding.humidyVal.text = "${it.humidity.toString()}%"
            binding.windVal.text = "${it.windSpeed.toString()} km/h"
            binding.feelVal.text = "${it.feelslike}${degree}C"
        }


    }
    @SuppressLint("SetTextI18n")
    fun showLoading() {
        if(!binding.progressBar.isVisible){
            binding.progressBar?.visibility = View.VISIBLE
        }
    }
    @SuppressLint("SetTextI18n")
    fun hideLoading() {
        if(binding.progressBar.isVisible){
            binding.progressBar?.visibility = View.GONE
        }

    }
    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            val latlng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            viewModel.latLng.value = latlng
            stopLocationUpdate()
        }
    }
    private fun stopLocationUpdate(){
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }
    fun getLastLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        if (isLocationEnabled(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location: Location? = task.result
                if (location == null) {
                    requestNewLocationData(fusedLocationClient)
                } else {
                    val latlng = LatLng(location.latitude, location.longitude)
                    viewModel.latLng.value = latlng
                }

            }
        }else{
            showAlert(context)
        }
    }
    fun showAlert(context: Context) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(fusedLocationClient: FusedLocationProviderClient) {
        val mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    fun formatDate(date: String): String{
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val format1 = SimpleDateFormat("EEEE, HH:mm")
        val parsedate = format.parse(date)
        var datestring = format1.format(parsedate)
        return datestring
    }
    fun chnageLanguage(languageCode: String){
        var context = LocaleHelper.setLocale(this@MainActivity, languageCode);
        context?.let {
            var resource = it.resources
            binding.changelanguage.text = resource.getString(R.string.chnagelanguage)
            binding.humidity.text = resource.getString(R.string.humidity)
            binding.wind.text = resource.getString(R.string.wind)
            binding.realfeel.text = resource.getString(R.string.real_feel)
        }

    }

}