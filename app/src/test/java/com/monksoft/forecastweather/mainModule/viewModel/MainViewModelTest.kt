package com.monksoft.forecastweather.mainModule.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.monksoft.forecastweather.common.dataAccess.WeatherForecastService
import com.monksoft.forecastweather.entities.WeatherForecastEntity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var mainViewModel : MainViewModel
    private lateinit var service : WeatherForecastService

    companion object {
        private lateinit var retrofit: Retrofit

        @BeforeClass
        @JvmStatic
        fun setupCommon() {
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    @Before
    fun setup(){
        mainViewModel = MainViewModel()
        service = retrofit.create(WeatherForecastService::class.java)
    }

    @Test
    fun checkCurrentWeatherIsNotNullTest(){
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(19.4342, -99.1962, "2e7415bc983752b966ca6c3ef0b9bddb",
                "metric", "en")
            assertThat(result.current, `is`(notNullValue()))
        }
    }

    @Test
    fun checkTimezoneReturnsMexicoCityTest(){
        runBlocking {
            val result = service.getWeatherForecastByCoordinates(19.4342, -99.1962, "2e7415bc983752b966ca6c3ef0b9bddb",
                "metric", "en")
            assertThat(result.timezone, `is`("America/Mexico_City"))
        }
    }

    @Test
    fun checkErrorResponseWithOnlyCoordinatesTest(){
        runBlocking {
            try {
                val result = service.getWeatherForecastByCoordinates(19.4342, -99.1962, "", "", "")
            } catch (e: Exception){
                assertThat(e.localizedMessage, `is`("HTTP 401 Unauthorized"))
            }
        }
    }

    @Test
    fun checkHoutlySizeTest(){
        runBlocking {
            mainViewModel.getWeatherAndForecast(19.4342, -99.1962, "2e7415bc983752b966ca6c3ef0b9bddb","metric", "en")
            val result = mainViewModel.getResult().getOrAwaitValue()
            assertThat(result.hourly.size, `is`(48))
        }
    }
}