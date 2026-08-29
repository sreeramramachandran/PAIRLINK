package com.pairlink.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class WeatherInfo(
    val temp: String,
    val condition: String
)

interface WeatherRepository {
    suspend fun fetchWeather(lat: Double, lon: Double): WeatherInfo
    suspend fun updateLocationAndWeather(lat: Double, lon: Double, locationName: String): Result<Unit>
}

@Singleton
class OpenMeteoWeatherRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherRepository {

    override suspend fun fetchWeather(lat: Double, lon: Double): WeatherInfo = withContext(ioDispatcher) {
        try {
            val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)
                val currentWeather = json.getJSONObject("current_weather")
                val tempVal = currentWeather.getDouble("temperature")
                val code = currentWeather.getInt("weathercode")

                val condition = mapWmoCode(code)
                val tempStr = "${kotlin.math.round(tempVal).toInt()}°"
                return@withContext WeatherInfo(temp = tempStr, condition = condition)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching Open-Meteo weather for lat: %f, lon: %f", lat, lon)
        }
        return@withContext WeatherInfo(temp = "25°", condition = "Partly Cloudy ⛅")
    }

    override suspend fun updateLocationAndWeather(lat: Double, lon: Double, locationName: String): Result<Unit> = withContext(ioDispatcher) {
        val uid = auth.currentUser?.uid ?: return@withContext Result.failure(IllegalStateException("User not authenticated"))
        val weather = fetchWeather(lat, lon)

        try {
            val updates = mapOf(
                "latitude" to lat,
                "longitude" to lon,
                "locationName" to locationName,
                "weatherTemp" to weather.temp,
                "weatherCondition" to weather.condition,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid).set(updates, SetOptions.merge()).await()
            Timber.d("Updated location and weather for uid %s: %s, %s", uid, locationName, weather.temp)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update location & weather in Firestore")
            Result.failure(e)
        }
    }

    private fun mapWmoCode(code: Int): String {
        return when (code) {
            0 -> "Clear Sky ☀️"
            1, 2, 3 -> "Partly Cloudy ⛅"
            45, 48 -> "Foggy 🌫️"
            51, 53, 55, 61, 63, 65 -> "Rainy 🌧️"
            71, 73, 75 -> "Snowy ❄️"
            80, 81, 82 -> "Rain Showers 🌦️"
            95, 96, 99 -> "Thunderstorm 🌩️"
            else -> "Partly Cloudy ⛅"
        }
    }
}
