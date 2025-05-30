package com.example.interviewpreptestcompose.api

import com.example.interviewpreptestcompose.data.Country
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class CountryApiService {
    private val gson = Gson()
    private val baseUrl = "https://restcountries.com/v3.1"

    suspend fun getAllCountries(): Result<List<Country>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/all")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<Country>>() {}.type
                val countries: List<Country> = gson.fromJson(response, listType)
                Result.success(countries)
            } else {
                Result.failure(Exception("HTTP Error: $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
