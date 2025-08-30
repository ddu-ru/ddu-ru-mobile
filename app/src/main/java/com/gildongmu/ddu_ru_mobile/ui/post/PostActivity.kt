package com.gildongmu.ddu_ru_mobile.ui.post

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.gildongmu.ddu_ru_mobile.R
import com.gildongmu.ddu_ru_mobile.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var postBinding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        postBinding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(postBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.postLayoutMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()

        // Navigation Controller 초기화
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setupToolbar() {
        setSupportActionBar(postBinding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)  // 뒤로가기 버튼 표시
            setDisplayShowTitleEnabled(false) // 타이틀 완전히 숨기기
        }

        // 뒤로가기 버튼 클릭 시 finish()
        postBinding.toolbar.setNavigationOnClickListener {
            val dispatcher = onBackPressedDispatcher
            dispatcher.onBackPressed()
        }
    }
}