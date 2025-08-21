package com.gildongmu.ddu_ru_mobile.network

import com.gildongmu.ddu_ru_mobile.model.KakaoLoginRequest
import com.gildongmu.ddu_ru_mobile.model.KakaoLoginResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface KakaoApi {
    @POST("/auth/kakao")
    @Headers("Content-Type:application/json")
    suspend fun socialLogin(@Body user: KakaoLoginRequest): KakaoLoginResponse
}