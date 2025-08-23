package com.gildongmu.ddu_ru_mobile.util.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gildongmu.ddu_ru_mobile.BuildConfig
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException

object GoogleLoginHelper {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    fun initGoogleLogin(activity: Activity, launcher: ActivityResultLauncher<IntentSenderRequest>) {
        try {
            oneTapClient = Identity.getSignInClient(activity)
            val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

            signInRequest =
                    BeginSignInRequest.builder()
                            .setGoogleIdTokenRequestOptions(
                                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                            .setSupported(true)
                                            .setServerClientId(webClientId)
                                            .setFilterByAuthorizedAccounts(
                                                    false
                                            )
                                            .build()
                            )
                            .setAutoSelectEnabled(false)
                            .build()

            oneTapClient
                    .beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        val request =
                                IntentSenderRequest.Builder(result.pendingIntent.intentSender)
                                        .build()
                        launcher.launch(request)
                    }
                    .addOnFailureListener { e ->
                        Log.e("GoogleLogin", "로그인 UI 표시 실패 : $e", e)

                        when (e) {
                            is ApiException -> {
                                Log.e(
                                        "GoogleLogin",
                                        "API Exception - Status Code Message: ${e.statusMessage}"
                                )

                            }
                            else -> {
                                Log.e("GoogleLogin", "일반 Exception Message: ${e.message}")
                            }
                        }
                    }
        } catch (e: Exception) {
            Log.e("GoogleLogin", "Google 로그인 초기화 실패: ${e.message}")
        }
    }

    fun handleResult(data: Intent?, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        try {

            if (!::oneTapClient.isInitialized) {
                Log.e("GoogleLogin", "oneTapClient가 초기화되지 않았습니다. initGoogleLogin을 먼저 호출하세요.")
                onFailure()
                return
            }

            if (data == null) {
                Log.e("GoogleLogin", "Intent data가 null입니다.")
                onFailure()
                return
            }



            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken

                if (idToken != null) {
                    Log.d(
                            "GoogleLogin",
                            "ID Token 형식 확인: ${if (idToken.contains(".")) "올바른 JWT 형식" else "잘못된 형식"}"
                    )

                    // JWT 구조 확인 (header.payload.signature)
                    val tokenParts = idToken.split(".")
                    if (tokenParts.size == 3) {
                        Log.d(
                                "GoogleLogin",
                                "JWT 구조: Header=${tokenParts[0].length}자, Payload=${tokenParts[1].length}자, Signature=${tokenParts[2].length}자"
                        )
                    } else {
                        Log.e("GoogleLogin", "JWT 구조가 올바르지 않음: ${tokenParts.size}개 부분")
                    }
                    Log.d("GoogleLogin", "=== ID Token 로깅 완료 ===")
                    onSuccess(idToken)
                } else {
                    Log.e("GoogleLogin", "ID Token이 null입니다.")
                    onFailure()
                }
            } catch (e: ApiException) {
                Log.e("GoogleLogin", "Google Sign-In 결과 파싱 실패 - ApiException: ${e.statusCode}", e)
                when (e.statusCode) {
                    else -> Log.e("GoogleLogin", "알 수 없는 상태 코드: ${e.statusCode}")
                }

                onFailure()
            }
        } catch (e: Exception) {
            Log.e("GoogleLogin", "Sign-in 처리 중 예외 발생 - Exception 상세: ${e.message}")
            onFailure()
        }
    }
}
