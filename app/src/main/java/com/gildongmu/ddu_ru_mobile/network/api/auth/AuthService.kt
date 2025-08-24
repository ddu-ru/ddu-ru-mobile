package com.gildongmu.ddu_ru_mobile.network.api.auth

import com.gildongmu.ddu_ru_mobile.model.auth.request.LoginRequest
import com.gildongmu.ddu_ru_mobile.model.auth.request.RefreshTokenRequest
import com.gildongmu.ddu_ru_mobile.model.auth.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/google") suspend fun loginWithGoogle(@Body request: LoginRequest): LoginResponse

    @POST("auth/kakao") suspend fun loginWithKakao(@Body user: LoginRequest): LoginResponse

    @POST("auth/logout") suspend fun logout(@Header("Authorization") accessToken: String)

    @POST("auth/refresh")
    suspend fun refreshAccessToken(
            @Header("Authorization") accessToken: String,
            @Body request: RefreshTokenRequest
    ): LoginResponse
}
