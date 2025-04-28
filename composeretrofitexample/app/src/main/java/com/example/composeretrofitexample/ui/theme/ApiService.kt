package com.example.composeretrofitexample.ui.theme

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/albums")
    suspend fun getAlbums(): Response<Api>
}