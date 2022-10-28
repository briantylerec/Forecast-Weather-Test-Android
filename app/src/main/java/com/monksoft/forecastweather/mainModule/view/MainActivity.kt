package com.monksoft.forecastweather.mainModule.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.monksoft.forecastweather.R
import com.monksoft.forecastweather.common.CommonUtils
import com.monksoft.forecastweather.common.dataAccess.WeatherForecastService
import com.monksoft.forecastweather.databinding.ActivityMainBinding
import com.monksoft.forecastweather.entities.Forecast
import com.monksoft.forecastweather.entities.WeatherForecastEntity
import com.monksoft.forecastweather.mainModule.view.adapters.ForecastAdapter
import com.monksoft.forecastweather.mainModule.view.adapters.OnClickListener
import com.monksoft.forecastweather.mainModule.viewModel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() , OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ForecastAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupRecyclerView()
        setupViewModel()
    }

    private fun setupAdapter() {
        adapter = ForecastAdapter(this)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    override fun onStart() {
        super.onStart()
        setupData()
    }

    private fun setupData() {
        lifecycleScope.launch {
            mainViewModel.getWeatherAndForecast(19.4342, -99.1962,
                "6364546cb00c113bff0065ac8aea2438", "metric", "en")
        }
    }

    private fun setupViewModel(){
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.getResult().observe(this){ result ->
            setupUI(result)
        }
    }

    private fun setupUI(data: WeatherForecastEntity) {
        with(binding) {
            tvTimeZone.text = data.timezone.replace("_", " ")
            current.tvTemp.text = getString(R.string.weather_temp, data.current.temp)
            current.tvDt.text = CommonUtils.getFullDate(data.current.dt)
            current.tvHumidity.text = getString(R.string.weather_humidity, data.current.humidity)
            current.tvMain.text = CommonUtils.getWeatherMain(data.current.weather)
            current.tvDescription.text = CommonUtils.getWeatherDescription(data.current.weather)
        }
        adapter.submitList(data.hourly)
    }

    //https://openweathermap.org/api/one-call-api#current
    private suspend fun getHistoricalWeather(): WeatherForecastEntity = withContext(Dispatchers.IO) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // TODO: 17/12/21 get appId
        val service = retrofit.create(WeatherForecastService::class.java)
        service.getWeatherForecastByCoordinates(19.4342, -99.1962, "2e7415bc983752b966ca6c3ef0b9bddb",
            "metric", "en")
    }

    override fun onClick(forecast: Forecast) {
        Snackbar.make(binding.root, CommonUtils.getFullDate(forecast.dt), Snackbar.LENGTH_LONG).show()
    }
}