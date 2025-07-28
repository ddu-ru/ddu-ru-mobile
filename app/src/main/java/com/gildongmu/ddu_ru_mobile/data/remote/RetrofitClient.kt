package com.gildongmu.ddu_ru_mobile.data.remote

import com.gildongmu.ddu_ru_mobile.data.remote.api.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://loginAPI"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService ::class.java)
}