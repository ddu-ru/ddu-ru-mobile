package com.gildongmu.ddu_ru_mobile.network

import android.content.Context
import com.gildongmu.ddu_ru_mobile.BuildConfig
import com.gildongmu.ddu_ru_mobile.network.api.post.PostCreateService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    val client = OkHttpClient.Builder().addInterceptor(logging).build()

    fun providePostCreateApi(context: Context): PostCreateService {
        val baseUrl = BuildConfig.BASE_URL
        val retrofit =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(PostCreateService::class.java)
    }
}