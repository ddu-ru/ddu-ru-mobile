package com.gildongmu.ddu_ru_mobile.model.auth.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
