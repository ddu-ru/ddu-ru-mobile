package com.gildongmu.ddu_ru_mobile.ui

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.gildongmu.ddu_ru_mobile.R
import com.gildongmu.ddu_ru_mobile.util.login.KakaoLoginHelper
import com.gildongmu.ddu_ru_mobile.util.login.GoogleLoginHelper

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)  // XML 연결

        val kakao_btn = findViewById<ImageButton>(R.id.kakao_btn)
        val google_btn = findViewById<ImageButton>(R.id.google_btn)

        //버튼 클릭시 kakao로그인 실행
        kakao_btn.setOnClickListener{
            KakaoLoginHelper.kakaoLogin(this)
        }

        //버튼 클릭시 google로그인 실행
        google_btn.setOnClickListener {
            GoogleLoginHelper.googleLogin(this)
        }
    }
}