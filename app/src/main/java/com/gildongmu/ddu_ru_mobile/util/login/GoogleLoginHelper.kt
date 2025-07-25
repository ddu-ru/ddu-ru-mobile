package com.gildongmu.ddu_ru_mobile.util.login

import android.content.Context
import android.widget.Toast

object GoogleLoginHelper {
    fun googleLogin(context: Context) {
        Toast.makeText(context, "구글로 계속하기 버튼을 클릭", Toast.LENGTH_SHORT).show()
    }
}