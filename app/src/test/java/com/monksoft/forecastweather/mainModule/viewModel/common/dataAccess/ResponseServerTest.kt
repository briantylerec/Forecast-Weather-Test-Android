package com.monksoft.forecastweather.mainModule.viewModel.common.dataAccess

import com.google.gson.Gson
import com.monksoft.forecastweather.entities.WeatherForecastEntity
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.net.HttpURLConnection

@RunWith(MockitoJUnitRunner::class)
class ResponseServerTest {

    private lateinit var mockWebServer : MockWebServer

    @Before
    fun setup(){
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown(){
        mockWebServer.shutdown()
    }

    @Test
    fun readJsonFileSuccess(){
        val reader = JSONFileLoader().loadJSONString("weather_forecast_response_success")
        assertThat(reader, `is`(notNullValue()))
        assertThat(reader, containsString("America/Mexico_City"))
    }

    @Test
    fun getWeatherForecastCheckTimeZoneExist(){
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(JSONFileLoader().loadJSONString("weather_forecast_response_success") ?: "{ErrorCode:100}")

        mockWebServer.enqueue(response)

        assertThat(response.getBody()?.readUtf8(), containsString("\"timezone\""))
    }

    @Test
    fun getWeatherForecastCheckFailResponse(){
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(JSONFileLoader().loadJSONString("weather_forecast_response_fail") ?: "{ErrorCode:100}")
        mockWebServer.enqueue(response)

        assertThat(response.getBody()?.readUtf8(), containsString("{\"cod\":401, \"message\": \"Invalid API key. Please see http://openweathermap.org/faq#error401 for more info.\"}"))
    }

    @Test
    fun getWeatherForecastCheckContainsHourly(){
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(JSONFileLoader().loadJSONString("weather_forecast_response_success") ?: "{ErrorCode:100}")

        mockWebServer.enqueue(response)

        assertThat(response.getBody()?.readUtf8(), containsString("hourly"))

        val json = Gson().fromJson(response.getBody()?.readUtf8() ?: "", WeatherForecastEntity::class.java)
        assertThat(json.hourly.isEmpty(), `is`(false))
    }
}