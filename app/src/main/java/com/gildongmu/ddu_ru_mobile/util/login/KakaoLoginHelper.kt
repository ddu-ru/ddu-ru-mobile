package com.gildongmu.ddu_ru_mobile.util.login

import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.user.UserApiClient
import android.content.Context
import com.gildongmu.ddu_ru_mobile.model.KakaoLoginRequest
import com.gildongmu.ddu_ru_mobile.network.NetworkModule
import com.kakao.sdk.common.model.ClientErrorCause
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object KakaoLoginHelper {
    private const val TAG = "LoginActivity"

    // 카카오 로그인 메소드
    fun kakaoLogin(context: Context) {

        // 카카오계정으로 로그인 시 사용할 콜백 함수 정의
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.idToken}")
                // 서버로 idToken을 전달
                CoroutineScope(Dispatchers.Main).launch {
                    sendKakaoTokenToServer(context, token.idToken!!)  // CoroutineScope 내에서 호출
                }
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 권한 요청을 취소한 경우 로그인 취소 처리
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없으면, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.idToken}")
                    // 서버로 idToken을 전달
                    CoroutineScope(Dispatchers.Main).launch {
                        sendKakaoTokenToServer(context, token.idToken!!)  // CoroutineScope 내에서 호출
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    // 서버로 카카오 토큰을 보내는 suspend 함수
    private suspend fun sendKakaoTokenToServer(context: Context, idToken: String) {
        val api = NetworkModule.provideKakaoApi(context.applicationContext)

        try {
            // 서버 요청 후 응답 처리
            val response = withContext(Dispatchers.IO) {
                api.socialLogin(KakaoLoginRequest(idToken = idToken)) // `idToken`을 서버로 전송
            }

            Log.d(TAG, "서버 응답: $response") // 여기서 응답이 KakaoLoginResponse 형태로 정상 반환되는지 확인

            // 서버에서 받은 accessToken, refreshToken 처리
            if (response != null) {
                val accessToken = response.accessToken
                val refreshToken = response.refreshToken
                // 토큰을 안전하게 저장하는 코드 추가 필요
            } else {
                Log.e(TAG, "서버 응답이 null입니다.")
            }

        } catch (e: Exception) {
            Log.e(TAG, "서버 요청 실패 ", e)
        }
    }
}