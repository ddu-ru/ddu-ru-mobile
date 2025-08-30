package com.gildongmu.ddu_ru_mobile.model.post.request

data class PostCreateRequest (
    val destinationId: Long,
    val title: String,
    val content: String,
    val startDate: String,
    val endDate: String,
    val recruitCapacity: Int,
    val recruitDeadline: String,
    val preferredGender: String,
    val preferredAgeMin: String,
    val preferredAgeMax: String,
    val budgetMin: Int,
    val budgetMax: Int,
    val photoUrls: Array<String>,
    val tags: Array<String>
)