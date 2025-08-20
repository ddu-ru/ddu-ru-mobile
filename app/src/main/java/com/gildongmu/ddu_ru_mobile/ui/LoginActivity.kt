package com.gildongmu.ddu_ru_mobile.ui


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
import com.gildongmu.ddu_ru_mobile.data.model.request.LoginRequest
import com.gildongmu.ddu_ru_mobile.data.remote.RetrofitClient
import com.gildongmu.ddu_ru_mobile.util.login.GoogleLoginHelper
import com.gildongmu.ddu_ru_mobile.util.login.KakaoLoginHelper
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val tokenStore by lazy { TokenDataStore(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val kakaoBtn = findViewById<ImageButton>(R.id.kakao_btn)
        val googleBtn = findViewById<ImageButton>(R.id.google_btn)


        kakaoBtn.setOnClickListener {
            KakaoLoginHelper.kakaoLogin(this)
        }

        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {
                GoogleLoginHelper.handleResult(
                    result.data,
                    onSuccess = { idToken ->
                        Log.d("LoginActivity", "받은 ID Token: $idToken")

                        lifecycleScope.launch {
                            try {
                                val response = RetrofitClient.authService.loginWithGoogle(
                                    LoginRequest(idToken)
                                )
                                Log.d("LoginActivity", "로그인 성공: $response")
                                val access = response.accessToken
                                val refresh = response.refreshToken


                                tokenStore.saveTokens(access, refresh)
                                startActivity(Intent(this@LoginActivity, TokenDebugActivity::class.java))


                                // TODO: SharedPreferences에 accessToken 저장, 홈 화면 이동 등
                            } catch (e: Exception) {
                                Log.e("LoginActivity", "서버 로그인 실패", e)
                                Toast.makeText(this@LoginActivity, "서버 로그인 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onFailure = {
                        Toast.makeText(this, "Google 로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(this, "Google 로그인 취소됨", Toast.LENGTH_SHORT).show()
            }
        }


        googleBtn.setOnClickListener {
            GoogleLoginHelper.initGoogleLogin(this, googleSignInLauncher)
        }
    }
}
