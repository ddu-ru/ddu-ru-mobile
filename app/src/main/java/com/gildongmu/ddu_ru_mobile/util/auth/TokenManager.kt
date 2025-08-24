package com.gildongmu.ddu_ru_mobile.util.auth

import TokenDataStore
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.gildongmu.ddu_ru_mobile.model.auth.request.RefreshTokenRequest
import com.gildongmu.ddu_ru_mobile.network.NetworkModule
import com.gildongmu.ddu_ru_mobile.proto.AuthToken
import com.gildongmu.ddu_ru_mobile.ui.auth.LoginActivity
import com.gildongmu.ddu_ru_mobile.ui.auth.TokenDebugActivity
import kotlinx.coroutines.flow.first

class TokenManager(private val context: Context) {

    private val tokenStore = TokenDataStore(context)
    private val authService = NetworkModule.provideSocialLoginApi(context)


    suspend fun refreshToken(): AuthToken? {
        return try {
            val currentToken = tokenStore.authToken.first()
            val request = RefreshTokenRequest(currentToken.refreshToken)

            val response =
                authService.refreshAccessToken(
                    accessToken = "Bearer ${currentToken.accessToken}",
                    request = request
                )


            tokenStore.saveTokens(response.accessToken, response.refreshToken)


            tokenStore.authToken.first()
        } catch (e: Exception) {
            Log.e("TokenManager", "토큰 갱신 실패", e)
            null
        }
    }

    suspend fun logout(): Boolean {
        return try {
            val currentToken = tokenStore.authToken.first()
            val response = authService.logout("Bearer ${currentToken.accessToken}")

            if (response.isSuccessful) {

                tokenStore.clearTokens()

                true

            } else {
                Log.e("TokenManager", "로그아웃 실패: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("TokenManager", "로그아웃 실패", e)
            false
        }
    }


}
