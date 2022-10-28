package com.monksoft.forecastweather.mainModule.view.adapters

import com.monksoft.forecastweather.entities.Forecast

interface OnClickListener {
    fun onClick(forecast: Forecast)
}