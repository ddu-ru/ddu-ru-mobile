package com.gildongmu.ddu_ru_mobile.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostViewModel: ViewModel() {

    private val _destinationId = MutableLiveData<Long>(0)
    val destinationId: LiveData<Long> = _destinationId

    private val _title = MutableLiveData<String>("")
    val title: LiveData<String> = _title

    private val _content = MutableLiveData<String>("")
    val content: LiveData<String> = _content

    private val _startDate = MutableLiveData<String>("출발일")
    val startDate: LiveData<String> = _startDate

    private val _endDate = MutableLiveData<String>("도착일")
    val endDate: LiveData<String> = _endDate

    private val _recruitCapacity = MutableLiveData<Int>(0)
    val recruitCapacity: LiveData<Int> = _recruitCapacity

    private val _recruitDeadline = MutableLiveData<String>("모집마감일")
    val recruitDeadline: LiveData<String> = _recruitDeadline

    private val _preferredGender = MutableLiveData<String>("")
    val preferredGender: LiveData<String> = _preferredGender

    private val _preferredAgeMin = MutableLiveData<String>("")
    val preferredAgeMin: LiveData<String> = _preferredAgeMin

    private val _preferredAgeMax = MutableLiveData<String>("")
    val preferredAgeMax: LiveData<String> = _preferredAgeMax

    private val _budgetMin = MutableLiveData<Int>(0)
    val budgetMin: LiveData<Int> = _budgetMin

    private val _budgetMax = MutableLiveData<Int>(10000000)
    val budgetMax: LiveData<Int> = _budgetMax

    private val _photoUrls = MutableLiveData<Array<String>>(arrayOf(""))
    val photoUrls: LiveData<Array<String>> = _photoUrls

    private val _tags = MutableLiveData<Array<String>>(arrayOf(""))
    val tags: LiveData<Array<String>> = _tags

    fun setDestinationId(destinationId : Long) {
        _destinationId.value = destinationId
    }

    fun setTitle(title : String) {
        _title.value = title
    }

    fun setContent(content : String) {
        _content.value = content
    }

    fun setStartDate(startDate : String) {
        _startDate.value = startDate
    }

    fun setEndDate(endDate : String) {
        _endDate.value = endDate
    }

    fun setRecruitCapacity(recruitCapacity : Int) {
        _recruitCapacity.value = recruitCapacity
    }

    fun setRecruitDeadline(recruitDeadline : String) {
        _recruitDeadline.value = recruitDeadline
    }

    fun setPreferredGender(preferredGender : String) {
        _preferredGender.value = preferredGender
    }

    fun setPreferredAgeMin(preferredAgeMin : String) {
        _preferredAgeMin.value = preferredAgeMin
    }

    fun setPreferredAgeMax(preferredAgeMax : String) {
        _preferredAgeMax.value = preferredAgeMax
    }

    fun setBudgetMin(budgetMin : Int) {
        _budgetMin.value = budgetMin
    }

    fun setBudgetMax(budgetMax : Int) {
        _budgetMax.value = budgetMax
    }

    fun setPhotoUrls(photoUrls: Array<String>) {
        _photoUrls.value = photoUrls
    }

    fun setTags(tags: Array<String>) {
        _tags.value = tags
    }
}