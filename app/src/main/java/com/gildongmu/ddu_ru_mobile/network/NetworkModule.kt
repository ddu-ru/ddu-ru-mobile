package com.gildongmu.ddu_ru_mobile.network

import android.content.Context
import com.gildongmu.ddu_ru_mobile.BuildConfig
import com.gildongmu.ddu_ru_mobile.network.api.auth.AuthService
import com.gildongmu.ddu_ru_mobile.network.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    val client = OkHttpClient.Builder().addInterceptor(logging).build()

    fun provideSocialLoginApi(context: Context): AuthService {
        val baseUrl = BuildConfig.BASE_URL
        val retrofit =
                Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create()) // ← Moshi 대신 Gson
                        .build()

        return retrofit.create(AuthService::class.java)
    }

    private fun provideOkHttpClient(interceptor: AuthInterceptor, context: Context): OkHttpClient {
        return OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            addInterceptor(AuthInterceptor(context))
            build()
        }
    }
}
