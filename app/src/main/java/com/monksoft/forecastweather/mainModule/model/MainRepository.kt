package com.monksoft.forecastweather.mainModule.model

import com.monksoft.forecastweather.common.dataAccess.WeatherForecastService
import com.monksoft.forecastweather.entities.WeatherForecastEntity

class MainRepository(private val service: WeatherForecastService) {
    suspend fun getWeatherAndForecast(lat: Double, lon: Double, appId: String, units: String,
                                      lang: String) : WeatherForecastEntity {
        return service.getWeatherForecastByCoordinates(lat, lon, appId, units, lang)
    }
}