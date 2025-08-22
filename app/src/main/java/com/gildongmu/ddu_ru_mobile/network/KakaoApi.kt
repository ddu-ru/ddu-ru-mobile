package com.gildongmu.ddu_ru_mobile.network

import com.gildongmu.ddu_ru_mobile.model.KakaoLoginRequest
import com.gildongmu.ddu_ru_mobile.model.KakaoLoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface KakaoApi {
    @POST("auth/kakao")
    suspend fun socialLogin(@Body user: KakaoLoginRequest): KakaoLoginResponse
}