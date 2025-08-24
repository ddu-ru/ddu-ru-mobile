package com.gildongmu.ddu_ru_mobile.ui.auth

import TokenDataStore
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gildongmu.ddu_ru_mobile.R
import com.gildongmu.ddu_ru_mobile.network.NetworkModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TokenDebugActivity : AppCompatActivity() {

    private val tokenStore by lazy { TokenDataStore(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token_debug)

        val tvAccess = findViewById<TextView>(R.id.tv_access_token)
        val tvRefresh = findViewById<TextView>(R.id.tv_refresh_token)
        val btnRefresh = findViewById<Button>(R.id.btn_refresh_token)
        val btnLogout = findViewById<Button>(R.id.btn_logout)

        // 토큰 표시
        lifecycleScope.launch {
            val token = tokenStore.authToken.first()
            tvAccess.text = token.accessToken
            tvRefresh.text = token.refreshToken
        }

        // 토큰 갱신 버튼
        btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                val token = tokenStore.authToken.first()
                val api = NetworkModule.provideSocialLoginApi(applicationContext)
                try {
                    val request =
                            com.gildongmu.ddu_ru_mobile.model.auth.request.RefreshTokenRequest(
                                    token.refreshToken
                            )
                    val response =
                            api.refreshAccessToken(
                                    accessToken = "Bearer ${token.accessToken}",
                                    request = request
                            )

                    tokenStore.saveTokens(response.accessToken, response.refreshToken)
                    tvAccess.text = response.accessToken
                    tvRefresh.text = response.refreshToken
                    Toast.makeText(this@TokenDebugActivity, "토큰 갱신 성공", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(
                                    this@TokenDebugActivity,
                                    "갱신 실패: ${e.message}",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                }
            }
        }

        // 로그아웃 버튼
        btnLogout.setOnClickListener {
            lifecycleScope.launch {
                val token = tokenStore.authToken.first()
                val api = NetworkModule.provideSocialLoginApi(applicationContext)
                try {
                    api.logout("Bearer ${token.accessToken}")
                    tokenStore.clearTokens()
                    tvAccess.text = ""
                    tvRefresh.text = ""
                    Toast.makeText(this@TokenDebugActivity, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(
                                    this@TokenDebugActivity,
                                    "로그아웃 실패: ${e.message}",
                                    Toast.LENGTH_SHORT
                            )
                            .show()
                }
            }
        }
    }
}
