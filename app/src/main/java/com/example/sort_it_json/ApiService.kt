package com.example.sort_it_json

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import kotlinx.coroutines.launch

// Retrofit interface
interface ApiService {
    @Multipart
    @POST("/api/predict")
    suspend fun predict(
        @Part file: MultipartBody.Part
    ): PredictResponse
}