package com.gildongmu.ddu_ru_mobile.network.interceptor

import TokenDataStore
import android.content.Context
import com.gildongmu.ddu_ru_mobile.BuildConfig
import com.gildongmu.ddu_ru_mobile.model.auth.response.LoginResponse
import com.gildongmu.ddu_ru_mobile.network.api.auth.AuthService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.Throws

class AuthInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        // TokenDataStore 인스턴스 생성
        val tokenDataStore = TokenDataStore(context)

        // runBlocking을 사용하여 비동기적 작업을 동기적으로 실행
        val token = runBlocking { tokenDataStore.getToken()}

        // 새로운 요청을 빌드하여 Authrization 헤더 추가
        val newRequest = chain.request().newBuilder()
            .apply {
                if (token != null) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()

        var response = chain.proceed(newRequest)

        if (response.code == 401) {
           val refreshToken =  runBlocking { tokenDataStore.getRefreshToken()}
          val newToken = refreshToken(refreshToken)

            runBlocking {
                tokenDataStore.saveTokens(newToken.accessToken, newToken.refreshToken)
            }

            val retryRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${newToken.accessToken}")
                .build()
            response = chain.proceed(retryRequest)
        }
        return  response
        // 새로운 요청(수정된 요청)으로 API 호출 진행

    }
    private  fun refreshToken(refreshToken: String): LoginResponse {
        val authService = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
// refreshToken 이 두개가 아니라 accessToken을 해야하는데 ,, 코드 이해좀 하고 하자
        return authService.refreshAccessToken(refreshToken)
    }
}
