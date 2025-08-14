package com.gildongmu.ddu_ru_mobile.model

data class KakaoLoginRequest(
    val idToken: String
)

data class KakaoLoginResponse(
    val accessToken: String,
    val refreshToken: String
)
