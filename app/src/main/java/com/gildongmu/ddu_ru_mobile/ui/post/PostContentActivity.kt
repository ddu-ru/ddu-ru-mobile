package com.gildongmu.ddu_ru_mobile.ui.post

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gildongmu.ddu_ru_mobile.databinding.ActivityPostContentBinding

class PostContentActivity : AppCompatActivity()  {
    private lateinit var postContentBinding: ActivityPostContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postContentBinding = ActivityPostContentBinding.inflate(layoutInflater)
        setContentView(postContentBinding.root)
    }
}