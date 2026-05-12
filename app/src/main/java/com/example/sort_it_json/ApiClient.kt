package com.example.sort_it_json

import com.example.sort_it_json.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // Base URL (Render ML API)
    private const val BASE_URL = Constants.RENDERED_MODEL //"http://10.0.2.2:8000" //Constants.RENDERED_MODEL

    // Custom OkHttpClient with increased timeouts (for slow ML responses)
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)   // important for ML processing delay
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // attach custom client here
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API service
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}