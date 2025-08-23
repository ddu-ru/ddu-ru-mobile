package com.gildongmu.ddu_ru_mobile.network.interceptor

import TokenDataStore
import android.content.Context
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
        // TokenDataStore 인스턴스 생성
        val tokenDataStore = TokenDataStore(context)

        // 현재 access token 가져오기 (suspend 함수를 동기적으로 호출)
        val token = runBlocking { tokenDataStore.getToken() }

        // 새로운 요청을 빌드하여 Authorization 헤더 추가
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

        // 401 Unauthorized 에러 발생 시 토큰 갱신 시도
        if (response.code == 401) {
            val refreshToken = runBlocking { tokenDataStore.getRefreshToken() } ?: ""

            if (refreshToken.isNotEmpty()) {
                try {
                    // 토큰 갱신 API 호출
                    val newToken = refreshTokenSync(refreshToken)

                    // 새로운 토큰 저장 (suspend 함수를 동기적으로 호출)
                    runBlocking {
                        tokenDataStore.saveTokens(newToken.accessToken, newToken.refreshToken)
                    }

                    // 갱신된 토큰으로 요청 재시도
                    val retryRequest =
                            chain.request()
                                    .newBuilder()
                                    .addHeader("Authorization", "Bearer ${newToken.accessToken}")
                                    .build()
                    response = chain.proceed(retryRequest)
                } catch (e: Exception) {
                    // 토큰 갱신 실패 시 기존 응답 반환
                    // TODO: 로그인 화면으로 리다이렉트 처리
                }
            }
        }

        return response
    }

    /** 동기적으로 토큰을 갱신하는 함수 주의: 메인 스레드에서 호출하면 안됨 */
    private fun refreshTokenSync(refreshToken: String): LoginResponse {
        // NetworkModule을 사용하여 AuthService 생성
        val authService = NetworkModule.provideSocialLoginApi(context)

        return try {
            // 동기적으로 토큰 갱신 (runBlocking 사용)
            runBlocking { authService.refreshAccessToken(refreshToken) }
        } catch (e: Exception) {
            throw RuntimeException("토큰 갱신 실패", e)
        }
    }
}
