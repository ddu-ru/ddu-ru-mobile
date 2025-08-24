package com.gildongmu.ddu_ru_mobile.network.interceptor

import TokenDataStore
import android.content.Context
import android.util.Log
import com.gildongmu.ddu_ru_mobile.BuildConfig
import com.gildongmu.ddu_ru_mobile.model.auth.response.LoginResponse
import com.gildongmu.ddu_ru_mobile.network.NetworkModule
import kotlin.jvm.Throws
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

class AuthInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val tokenDataStore = TokenDataStore(context)
        val token = runBlocking { tokenDataStore.getAccessToken() }

        val newRequest =
                chain.request()
                        .newBuilder()
                        .apply {
                            if (token != null) {
                                addHeader("Authorization", "Bearer $token")
                            }
                        }
                        .build()

        var response = chain.proceed(newRequest)

        if (response.code == 401) {
            val refreshToken = runBlocking { tokenDataStore.getRefreshToken() } ?: ""

            if (refreshToken.isNotEmpty()) {
                try {

                    val newToken = refreshTokenSync(refreshToken)

                    runBlocking {
                        tokenDataStore.saveTokens(newToken.accessToken, newToken.refreshToken)
                    }

                    val retryRequest =
                            chain.request()
                                    .newBuilder()
                                    .addHeader("Authorization", "Bearer ${newToken.accessToken}")
                                    .build()
                    response = chain.proceed(retryRequest)
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG) {
                        Log.d("$e", "토큰 갱신을 실패했습니다 $e")
                    }

                    // TODO: 나중에 로그인 화면으로 리다이렉트 처리
                }
            }
        }

        return response
    }
    private fun refreshTokenSync(refreshToken: String): LoginResponse {
        val authService = NetworkModule.provideSocialLoginApi(context)

        return try {
            runBlocking {
                val request =
                        com.gildongmu.ddu_ru_mobile.model.auth.request.RefreshTokenRequest(
                                refreshToken
                        )
                authService.refreshAccessToken(
                        accessToken = "Bearer $refreshToken", // 헤더로도 보냄
                        request = request // 바디로도 보냄
                )
            }
        } catch (e: Exception) {
            throw RuntimeException("토큰 갱신 실패", e)
        }
    }
}
