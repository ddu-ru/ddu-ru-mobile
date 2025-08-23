package com.gildongmu.ddu_ru_mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.gildongmu.ddu_ru_mobile.ui.auth.LoginActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}