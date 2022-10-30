package com.monksoft.forecastweather.mainModule.viewModel.common.dataAccess

import com.google.gson.Gson
import com.monksoft.forecastweather.entities.WeatherForecastEntity
import java.io.InputStreamReader

class JSONFileLoader {

    private var jsonStr : String? = null

    fun loadJSONString(file:String): String?{
        val loader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(file))
        jsonStr = loader.readText()
        loader.close()
        return jsonStr
    }

    fun loadWeatherForecastEntity(file:String): WeatherForecastEntity?{
        val loader = InputStreamReader(this.javaClass.classLoader?.getResourceAsStream(file))
        jsonStr = loader.readText()
        loader.close()
        return Gson().fromJson(jsonStr, WeatherForecastEntity::class.java)
    }
}