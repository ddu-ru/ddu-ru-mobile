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
                            .setFilterByAuthorizedAccounts(false)
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
                    if (BuildConfig.DEBUG) {
                        Log.e("GoogleLogin", "로그인 UI 표시 실패 : $e")
                    }
                }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("GoogleLogin", "Google 로그인 초기화 실패: ${e.message}")
            }
        }
    }

    fun handleResult(data: Intent?, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        try {
            if(data == null ){
                if(BuildConfig.DEBUG) Log.e("GoogleLogin", "Intent data가 null입니다. ")
                onFailure()
                return
            }
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken

                if(idToken!=null){
                    if(BuildConfig.DEBUG){
                        Log.d("GoogleLogin","IDToken 받음 : $idToken")
                    }
                    onSuccess(idToken)
                }
            } catch (e: ApiException) {
                if (BuildConfig.DEBUG) {
                    Log.e("GoogleLogin", "Google Sign-In 결과 파싱 실패 - ApiException: ${e.statusCode}")
                }
                onFailure()
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("GoogleLogin", "Sign-in 처리 중 예외 발생: ${e.message}")
            }
            onFailure()
        }
    }
}
