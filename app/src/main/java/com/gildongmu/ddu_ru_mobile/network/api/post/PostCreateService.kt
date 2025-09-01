package com.gildongmu.ddu_ru_mobile.network.api.post

import com.gildongmu.ddu_ru_mobile.model.post.request.PostCreateRequest
import com.gildongmu.ddu_ru_mobile.model.post.response.PostCreateResponse
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Header

interface PostCreateService {
    @POST("posts") suspend fun postCreate( @Body post: PostCreateRequest): PostCreateResponse
}