package com.example.ddu_ru_mobile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ddu_ru_mobile.ui.post.PostContentActivity

class DevEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 테스트할 화면으로 바로 이동
        startActivity(Intent(this, PostContentActivity::class.java))

        // 이 액티비티는 필요 없으니 종료
        finish()
    }
}