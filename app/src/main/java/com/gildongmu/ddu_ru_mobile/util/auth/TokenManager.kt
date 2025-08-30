package com.gildongmu.ddu_ru_mobile.util.auth

import TokenDataStore
import android.content.Context
import android.util.Log
import androidx.browser.trusted.TokenStore
import com.gildongmu.ddu_ru_mobile.model.auth.request.RefreshTokenRequest
import com.gildongmu.ddu_ru_mobile.network.NetworkModule
import com.gildongmu.ddu_ru_mobile.proto.AuthToken
import com.kakao.sdk.common.util.SdkLog.Companion.e
import com.kakao.sdk.v2.auth.BuildConfig
import kotlinx.coroutines.flow.first

class TokenManager(private val context: Context) {

    private val tokenStore = TokenDataStore(context)
    private val authService = NetworkModule.provideSocialLoginApi(context)

    suspend fun refreshToken(): AuthToken? {
        return try {
            val currentToken = tokenStore.authToken.first()
            val request = RefreshTokenRequest(currentToken.refreshToken)

            // AuthInterceptor가 자동으로 토큰을 추가하므로 accessToken 파라미터 제거
            val response = authService.refreshAccessToken(request = request)

            tokenStore.saveTokens(response.accessToken, response.refreshToken)
            tokenStore.authToken.first()
        } catch (e: Exception) {
            Log.e("TokenManager", "토큰 갱신 실패", e)
            null
        }
    }

    suspend fun logout(): Boolean {
        return try {
            val authApi = NetworkModule.provideAuthApi(context)

            val response = authApi.logout()
            if (response.isSuccessful) {
                tokenStore.clearTokens()
                true
            } else {

                Log.e("TokenManager", "로그아웃 실패 ${response.code()}")
                Log.e("TokenManager", "로그아웃 실패 ${response.message()}")
                false
            }
        }            catch (e: Exception) {
                Log.e("TokenManager", "로그아웃 실패 ${e.message}")
                false
            }
        }}
//        return try {
//            val currentToken = tokenStore.authToken.first()
//            val response = authService.logout("Bearer ${currentToken.accessToken}")
//
//            if (response.isSuccessful) {
//                tokenStore.clearTokens()
//                true
//            } else {
//                Log.e("TokenManager", "로그아웃 실패: ${response.code()}")
//                Log.e("TokenManager", "로그아웃 실패: ${response.message().toString()}")
//                Log.e("TokenManager", "로그아웃 실패: ${response.toString()}")
//                false
//            }
//        } catch (e: Exception) {
//            Log.e("TokenManager", "로그아웃 실패 nn ${e.message}", e)
//            false
//        }



