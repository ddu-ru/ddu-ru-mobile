package com.gildongmu.ddu_ru_mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.gildongmu.ddu_ru_mobile.ui.LoginActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //LoginActivity를 띄움
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}