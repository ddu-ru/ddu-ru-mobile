package com.gildongmu.ddu_ru_mobile.util

import android.content.Context
import com.gildongmu.ddu_ru_mobile.model.post.request.PostCreateRequest
import com.gildongmu.ddu_ru_mobile.network.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object PostCreateHelper {

    // 게시글 생성용 콜백
    interface PostCreateCallback {
        fun onSuccess(id: Long)           // 게시글 생성 성공 시 서버가 반환한 ID
        fun onFailure(errorMessage: String?) // 실패 시 에러 메시지
    }

    fun createPost(
        context: Context,
        postRequest: PostCreateRequest,
        callback: PostCreateCallback
    ) {
        val api = NetworkModule.providePostCreateApi(context.applicationContext)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) { api.postCreate(postRequest) }
                if (response != null) {
                    callback.onSuccess(response.id)
                } else {
                    callback.onFailure("서버 응답이 null입니다.")
                }
            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }
}