package com.gildongmu.ddu_ru_mobile.ui.auth

import TokenDataStore
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gildongmu.ddu_ru_mobile.R
import com.gildongmu.ddu_ru_mobile.model.auth.request.LoginRequest
import com.gildongmu.ddu_ru_mobile.network.NetworkModule
import com.gildongmu.ddu_ru_mobile.util.auth.GoogleLoginHelper
import com.gildongmu.ddu_ru_mobile.util.auth.KakaoLoginHelper
import com.gildongmu.ddu_ru_mobile.util.auth.SocialLoginCallback
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

        private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>
        private val tokenStore by lazy { TokenDataStore(applicationContext) }

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_login)

                val kakaoBtn = findViewById<ImageButton>(R.id.kakao_btn)
                val googleBtn = findViewById<ImageButton>(R.id.google_btn)

                // --- Kakao 로그인 ---
                kakaoBtn.setOnClickListener {
                        KakaoLoginHelper.login(
                                this,
                                object : SocialLoginCallback {
                                        override fun onSuccess(idToken: String) {
                                                lifecycleScope.launch {
                                                        try {
                                                                val authService =
                                                                        NetworkModule
                                                                                .provideSocialLoginApi(
                                                                                        this@LoginActivity
                                                                                )
                                                                val response =
                                                                        authService.loginWithKakao(
                                                                                LoginRequest(
                                                                                        idToken
                                                                                )
                                                                        )
                                                                Log.d(
                                                                        "LoginActivity",
                                                                        "카카오 로그인 성공: $response"
                                                                )
                                                                tokenStore.saveTokens(
                                                                        response.accessToken,
                                                                        response.refreshToken
                                                                )
                                                                startActivity(
                                                                        Intent(
                                                                                this@LoginActivity,
                                                                                TokenDebugActivity::class
                                                                                        .java
                                                                        )
                                                                )
                                                        } catch (e: Exception) {
                                                                Log.e(
                                                                        "LoginActivity",
                                                                        "서버 로그인 실패",
                                                                        e
                                                                )
                                                                Toast.makeText(
                                                                                this@LoginActivity,
                                                                                "서버 로그인 실패",
                                                                                Toast.LENGTH_SHORT
                                                                        )
                                                                        .show()
                                                        }
                                                }
                                        }

                                        override fun onFailure(errorMessage: String?) {
                                                Toast.makeText(
                                                                this@LoginActivity,
                                                                "카카오 로그인 실패: $errorMessage",
                                                                Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                        }
                                }
                        )
                }

                // --- Google 로그인 ---
                googleSignInLauncher =
                        registerForActivityResult(
                                ActivityResultContracts.StartIntentSenderForResult()
                        ) { result ->
                                if (result.resultCode == RESULT_OK) {
                                        GoogleLoginHelper.handleResult(
                                                context = this, // Context 전달
                                                data = result.data,
                                                callback =
                                                        object : SocialLoginCallback {
                                                                override fun onSuccess(
                                                                        idToken: String
                                                                ) {
                                                                        lifecycleScope.launch {
                                                                                try {
                                                                                        val authService =
                                                                                                NetworkModule
                                                                                                        .provideSocialLoginApi(
                                                                                                                this@LoginActivity
                                                                                                        )
                                                                                        val response =
                                                                                                authService
                                                                                                        .loginWithGoogle(
                                                                                                                LoginRequest(
                                                                                                                        idToken
                                                                                                                )
                                                                                                        )
                                                                                        Log.d(
                                                                                                "LoginActivity",
                                                                                                "구글 로그인 성공: $response"
                                                                                        )
                                                                                        tokenStore
                                                                                                .saveTokens(
                                                                                                        response.accessToken,
                                                                                                        response.refreshToken
                                                                                                )
                                                                                        startActivity(
                                                                                                Intent(
                                                                                                        this@LoginActivity,
                                                                                                        TokenDebugActivity::class
                                                                                                                .java
                                                                                                )
                                                                                        )
                                                                                } catch (
                                                                                        e:
                                                                                                Exception) {
                                                                                        Log.e(
                                                                                                "LoginActivity",
                                                                                                "서버 로그인 실패",
                                                                                                e
                                                                                        )
                                                                                        Toast.makeText(
                                                                                                        this@LoginActivity,
                                                                                                        "서버 로그인 실패",
                                                                                                        Toast.LENGTH_SHORT
                                                                                                )
                                                                                                .show()
                                                                                }
                                                                        }
                                                                }

                                                                override fun onFailure(
                                                                        errorMessage: String?
                                                                ) {
                                                                        Toast.makeText(
                                                                                        this@LoginActivity,
                                                                                        "Google 로그인 실패: $errorMessage",
                                                                                        Toast.LENGTH_SHORT
                                                                                )
                                                                                .show()
                                                                }
                                                        }
                                        )
                                } else {
                                        Toast.makeText(this, "Google 로그인 취소됨", Toast.LENGTH_SHORT)
                                                .show()
                                }
                        }

                googleBtn.setOnClickListener {
                        GoogleLoginHelper.initGoogleLogin(this, googleSignInLauncher)
                }
        }
}
