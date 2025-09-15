package com.gildongmu.ddu_ru_mobile.util

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.gildongmu.ddu_ru_mobile.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationBarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_navigation_bar)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_main)
        bottomNavigationView.itemIconTintList = null
    }
}