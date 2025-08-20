package com.gildongmu.ddu_ru_mobile.ui

import TokenDataStore
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gildongmu.ddu_ru_mobile.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class TokenDebugActivity : AppCompatActivity() {

    // 앱 컨텍스트로 생성 (누수 방지)
    private val tokenStore by lazy { TokenDataStore(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token_debug)

        val tvAccess = findViewById<TextView>(R.id.tv_access_token)
        val tvRefresh = findViewById<TextView>(R.id.tv_refresh_token)

        // 한 번만 읽어서 표시 (변경에 따라 계속 업데이트하려면 collect 사용)
        lifecycleScope.launch {
            val token = tokenStore.authToken.first()
            tvAccess.text = token.accessToken
            tvRefresh.text = token.refreshToken
        }
    }
}
