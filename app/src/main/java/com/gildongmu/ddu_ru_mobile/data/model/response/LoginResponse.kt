package com.gildongmu.ddu_ru_mobile.data.model.response

data class LoginResponse(
    val accessToken: String,
    val userId :Long,
    val nickname: String
)
