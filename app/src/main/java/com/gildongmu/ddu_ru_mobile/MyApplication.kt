package com.gildongmu.ddu_ru_mobile

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

// MyApplication.kt
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}