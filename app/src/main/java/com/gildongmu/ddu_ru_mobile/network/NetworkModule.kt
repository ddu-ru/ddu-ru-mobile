package com.gildongmu.ddu_ru_mobile.network

import android.content.Context
import com.gildongmu.ddu_ru_mobile.BuildConfig
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    val client = OkHttpClient.Builder().addInterceptor(logging).build()

    fun provideKakaoApi(context: Context): KakaoApi {
        val baseUrl = BuildConfig.BASE_URL
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // ← Moshi 대신 Gson
            .build()

        return retrofit.create(KakaoApi::class.java)
    }

}

