package com.gildongmu.ddu_ru_mobile.network.api.auth

import com.gildongmu.ddu_ru_mobile.model.auth.request.LoginRequest
import com.gildongmu.ddu_ru_mobile.model.auth.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/google")
    suspend fun loginWithGoogle(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("auth/kakao")
    suspend fun loginWithKakao(@Body user: LoginRequest): LoginResponse

    @POST("auth/refresh")
    suspend fun refreshAccessToken(
        @Body refreshToken: String
    ): LoginResponse

}
