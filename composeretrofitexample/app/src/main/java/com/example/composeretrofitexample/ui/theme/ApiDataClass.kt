package com.example.composeretrofitexample.ui.theme

import com.google.gson.annotations.SerializedName

data class ApiDataClass(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
)