package com.gildongmu.ddu_ru_mobile.util.auth

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause

object KakaoLoginHelper : SocialLoginHelper("KakaoLogin") {

    override fun login(context: Context, callback: SocialLoginCallback) {

        val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            when {
                error != null -> {
                    Log.e(tag, "카카오 로그인 실패", error)
                    callback.onFailure(error.message)
                }
                token != null -> {
                    Log.i(tag, "카카오 로그인 성공: ${token.idToken}")
                    sendTokenToServer(context, token.idToken!!, SocialType.KAKAO, callback)
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null && (error !is ClientError || error.reason != ClientErrorCause.Cancelled)) {
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
                } else if (token != null) {
                    sendTokenToServer(context, token.idToken!!, SocialType.KAKAO, callback)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = kakaoCallback)
        }
    }
}
