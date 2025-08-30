package com.gildongmu.ddu_ru_mobile.model.auth.request

data class LoginRequest(
    val idToken : String
)

data class RefreshTokenRequest(val refreshToken: String)
