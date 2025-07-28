package com.gildongmu.ddu_ru_mobile.data.remote.api

import com.gildongmu.ddu_ru_mobile.data.model.request.LoginRequest
import com.gildongmu.ddu_ru_mobile.data.model.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/login/google")
    suspend fun loginWithGoogle(
        @Body request: LoginRequest
    ): LoginResponse
}