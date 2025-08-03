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

    fun initGoogleLogin(
        activity: Activity,
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        oneTapClient = Identity.getSignInClient(activity)

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(
                        BuildConfig.GOOGLE_CLIENT_ID
                    )
                    .setFilterByAuthorizedAccounts(false)
                    .build()

            )
            .setAutoSelectEnabled(false)
            .build()


        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val request = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                launcher.launch(request)
            }
            .addOnFailureListener { e ->
                Log.e("GoogleLogin", "로그인 UI 표시 실패 : $e", e)
            }
    }

    fun handleResult(
        data: Intent?,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                Log.d("GoogleLogin", "ID Token: $idToken")
                onSuccess(idToken)
            } else {
                Log.e("GoogleLogin", "ID Token이 null입니다.")
                onFailure()
            }
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "Sign-in 실패", e)
            onFailure()
        }
    }
}
