package com.example.uas_0114.network

object UtilsApi {
    val apiService: ApiService
        get() = RetrofitClient.getClient(ApiConfig.END_POINT).create(ApiService::class.java)
}