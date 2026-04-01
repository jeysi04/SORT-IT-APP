package com.example.sort_it_json

import com.example.sort_it_json.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Choose which BASE_URL to use
    private const val BASE_URL = Constants.BASE_URL_EMULATOR

    private val client = OkHttpClient.Builder().build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}