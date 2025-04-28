package com.example.todo.ui.theme

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiServices {
    @GET("AKfycbw59ARa3GAs6v7WpnudnAHBnQSqYUi0AtXUVr8I4QYX2cMDa8XzkbrQ3AZv-ILI8UWM/exec")
    suspend fun getTasks(): ArrayList<Task>
    // api to fetch all available tasks in sheet


    @POST("AKfycbz-aBKykhFuVsyOxai9rqQVPazLkYypSW4h7xdM6ZRudC0wBW_1ukCHaSzOGMheFlnI/exec")
    suspend fun postTask(
        @Body postTaskBody: PostTaskBody
    ) : PostTaskSuccessResponse
    // api to post new task in schema


    @POST("AKfycbyyqMKir_DCtXOeWQwq7QGQJ_HTsLtrvsoO_eZ0mekNMi78XgXkjQ7_UnATc1WULnzn/exec")
    suspend fun postUpdatesTask(
        @Body postUpdatedTaskBody: PostUpdatedTaskBody
    )
    // api to update any task already present in schema
}
