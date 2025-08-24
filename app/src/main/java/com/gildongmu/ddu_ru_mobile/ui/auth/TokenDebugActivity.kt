package com.gildongmu.ddu_ru_mobile.ui.auth

import TokenDataStore
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gildongmu.ddu_ru_mobile.R
import com.gildongmu.ddu_ru_mobile.util.auth.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TokenDebugActivity : AppCompatActivity() {

    private val tokenStore by lazy { TokenDataStore(applicationContext) }
    private val tokenManager by lazy { TokenManager(applicationContext) }

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
                try {
                    val refreshedToken = tokenManager.refreshToken()
                    if (refreshedToken != null) {
                        tvAccess.text = refreshedToken.accessToken
                        tvRefresh.text = refreshedToken.refreshToken
                        Toast.makeText(this@TokenDebugActivity, "토큰 갱신 성공", Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        Toast.makeText(this@TokenDebugActivity, "토큰 갱신 실패", Toast.LENGTH_SHORT)
                                .show()
                    }
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
                try {
                    val success = tokenManager.logout()
                    if (success) {

                        startActivity(
                            Intent(
                                this@TokenDebugActivity,
                                LoginActivity::class.java
                            )
                        )
                        tvAccess.text = ""
                        tvRefresh.text = ""
                        Toast.makeText(this@TokenDebugActivity, "로그아웃 성공", Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        Toast.makeText(this@TokenDebugActivity, "로그아웃 실패", Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: Exception) {
                    Log.d("=========== Logout", "${e.message}")
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
