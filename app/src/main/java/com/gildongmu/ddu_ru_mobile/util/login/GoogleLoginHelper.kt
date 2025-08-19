package com.gildongmu.ddu_ru_mobile.util.login

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
            Log.d("GoogleLogin", "=== Google Sign-In 초기화 시작 ===")


            oneTapClient = Identity.getSignInClient(activity)
            Log.d("GoogleLogin", "SignInClient 초기화 완료")

            val clientId = BuildConfig.GOOGLE_CLIENT_ID
            Log.d("GoogleLogin", "사용할 Google Client ID: $clientId")

            if (clientId.isBlank() || clientId == "your_google_client_id_here") {
                Log.e("GoogleLogin", "Google Client ID가 설정되지 않았습니다. local.properties 파일을 확인하세요.")
                return
            }

            Log.d("GoogleLogin", "BeginSignInRequest 설정 시작...")
            Log.d("GoogleLogin", "Android Client ID: $clientId")

            val webClientId = BuildConfig.WEB_CLIENT_ID
            Log.d("GoogleLogin", "Web Client ID: $webClientId")

            if (webClientId.isBlank() || webClientId == "your_web_client_id_here") {
                Log.e("GoogleLogin", "Web Client ID가 설정되지 않았습니다. local.properties 파일을 확인하세요.")
                return
            }

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

            Log.d("GoogleLogin", "BeginSignInRequest 설정 완료: $signInRequest")

            Log.d("GoogleLogin", "Google 로그인 요청 시작...")


            Log.d("GoogleLogin", "beginSignIn() 호출 시작...")
            Log.d("GoogleLogin", "Request 설정: $signInRequest")

            oneTapClient
                    .beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        Log.d("GoogleLogin", "Google 로그인 UI 표시 성공")
                        Log.d("GoogleLogin", "PendingIntent: ${result.pendingIntent}")
                        Log.d("GoogleLogin", "IntentSender: ${result.pendingIntent.intentSender}")
                        val request =
                                IntentSenderRequest.Builder(result.pendingIntent.intentSender)
                                        .build()
                        launcher.launch(request)
                    }
                    .addOnFailureListener { e ->
                        Log.e("GoogleLogin", "로그인 UI 표시 실패 : $e", e)

                        when (e) {
                            is ApiException -> {
                                Log.e("GoogleLogin", "API Exception - Status Code: ${e.statusCode}")
                                Log.e(
                                        "GoogleLogin",
                                        "API Exception - Status Message: ${e.statusMessage}"
                                )

                                when (e.statusCode) {
                                    10 -> {
                                        Log.e(
                                                "GoogleLogin",
                                                "DEVELOPER_ERROR: Google Cloud Console 설정 문제"
                                        )
                                        Log.e("GoogleLogin", "해결 방법:")
                                        Log.e(
                                                "GoogleLogin",
                                                "1. Google Cloud Console에서 Google Sign-In API 활성화"
                                        )
                                        Log.e("GoogleLogin", "2. OAuth 2.0 클라이언트 ID 확인")
                                        Log.e("GoogleLogin", "3. SHA-1 인증서 지문 확인")
                                    }
                                    12501 -> Log.e("GoogleLogin", "SIGN_IN_CANCELLED: 사용자가 로그인 취소")
                                    12500 ->
                                            Log.e("GoogleLogin", "INTERNAL_ERROR: Google 서비스 내부 오류")
                                    12502 -> Log.e("GoogleLogin", "NETWORK_ERROR: 네트워크 연결 문제")
                                    12508 -> Log.e("GoogleLogin", "SIGN_IN_REQUIRED: 추가 로그인 필요")
                                    else -> Log.e("GoogleLogin", "알 수 없는 상태 코드: ${e.statusCode}")
                                }
                            }
                            else -> {
                                Log.e("GoogleLogin", "일반 Exception: ${e.javaClass.simpleName}")
                                Log.e("GoogleLogin", "Exception Message: ${e.message}")
                            }
                        }
                    }
        } catch (e: Exception) {
            Log.e("GoogleLogin", "Google 로그인 초기화 실패", e)
            Log.e("GoogleLogin", "Exception Type: ${e.javaClass.simpleName}")
            Log.e("GoogleLogin", "Exception Message: ${e.message}")
        }
    }

    fun handleResult(data: Intent?, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        try {

            if (!::oneTapClient.isInitialized) {
                Log.e("GoogleLogin", "oneTapClient가 초기화되지 않았습니다. initGoogleLogin을 먼저 호출하세요.")
                onFailure()
                return
            }

            Log.d("GoogleLogin", "=== Google Sign-In 결과 처리 시작 ===")

            if (data == null) {
                Log.e("GoogleLogin", "Intent data가 null입니다.")
                onFailure()
                return
            }


            Log.d("GoogleLogin", "Intent Action: ${data.action}")
            Log.d("GoogleLogin", "Intent Data: ${data.dataString}")
            Log.d("GoogleLogin", "Intent Extras 크기: ${data.extras?.size() ?: 0}")


            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken

                if (idToken != null) {
                    Log.d("GoogleLogin", "=== ID Token 상세 정보 ===")
                    Log.d("GoogleLogin", "ID Token 전체: $idToken")
                    Log.d("GoogleLogin", "ID Token 길이: ${idToken.length}")
                    Log.d("GoogleLogin", "ID Token 시작 부분: ${idToken.take(50)}...")
                    Log.d("GoogleLogin", "ID Token 끝 부분: ...${idToken.takeLast(50)}")
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
                Log.e("GoogleLogin", "Status Message: ${e.statusMessage}")


                when (e.statusCode) {
                    10 -> Log.e("GoogleLogin", "DEVELOPER_ERROR: Google Cloud Console 설정 문제")
                    12501 -> Log.e("GoogleLogin", "SIGN_IN_CANCELLED: 사용자가 로그인 취소")
                    12500 -> Log.e("GoogleLogin", "INTERNAL_ERROR: Google 서비스 내부 오류")
                    12502 -> Log.e("GoogleLogin", "NETWORK_ERROR: 네트워크 연결 문제")
                    12508 -> Log.e("GoogleLogin", "SIGN_IN_REQUIRED: 추가 로그인 필요")
                    else -> Log.e("GoogleLogin", "알 수 없는 상태 코드: ${e.statusCode}")
                }

                onFailure()
            }
        } catch (e: Exception) {
            Log.e("GoogleLogin", "Sign-in 처리 중 예외 발생: ${e.javaClass.simpleName}", e)
            Log.e("GoogleLogin", "Exception 상세: ${e.message}")
            onFailure()
        }
    }
}
