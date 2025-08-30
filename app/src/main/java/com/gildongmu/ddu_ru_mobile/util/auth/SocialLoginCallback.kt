package com.gildongmu.ddu_ru_mobile.util.auth

import android.content.Context
import android.util.Log
import com.gildongmu.ddu_ru_mobile.model.auth.request.LoginRequest
import com.gildongmu.ddu_ru_mobile.network.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SocialType { KAKAO, GOOGLE }

interface SocialLoginCallback {
    fun onSuccess(idToken: String)
    fun onFailure(errorMessage: String? = null)
}

abstract class SocialLoginHelper(val tag: String = "SocialLogin") {
    abstract fun login(context: Context, callback: SocialLoginCallback)

    protected fun sendTokenToServer(
        context: Context,
        idToken: String,
        type: SocialType,
        callback: SocialLoginCallback
    ) {
        val api = NetworkModule.provideSocialLoginApi(context.applicationContext)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    when (type) {
                        SocialType.KAKAO -> api.loginWithKakao(LoginRequest(idToken))
                        SocialType.GOOGLE -> api.loginWithGoogle(LoginRequest(idToken))
                    }
                }

                if (true) {
                    Log.d(tag, "서버 응답 성공: accessToken=${response.accessToken}, refreshToken=${response.refreshToken}")
                    callback.onSuccess(idToken)
                    // TODO: accessToken, refreshToken 안전하게 저장
                } else {
                    Log.e(tag, "서버 응답이 null입니다.")
                    callback.onFailure("서버 응답이 null입니다.")
                }
            } catch (e: Exception) {
                Log.e(tag, "서버 요청 실패", e)
                callback.onFailure(e.message)
            }
        }
    }
}
