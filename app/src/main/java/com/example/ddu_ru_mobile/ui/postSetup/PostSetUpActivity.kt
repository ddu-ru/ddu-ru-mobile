package com.example.ddu_ru_mobile.ui.postSetup

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.ddu_ru_mobile.R
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.example.ddu_ru_mobile.ui.postSetup.adapter.BoardSpinnerAdapter

class PostSetUpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_setup)

        //id 가져오기
        val searchView = findViewById<SearchView>(R.id.searchView)
        val btnDepartureDate = findViewById<Button>(R.id.btnDepartureDate)
        val btnArivalDate = findViewById<Button>(R.id.btnArrivalDate)
        val calendarView = findViewById<LinearLayout>(R.id.calendarView)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val spinnerRecruit = findViewById<Spinner>(R.id.spinnerRecruit)
        val spinnerDeadline = findViewById<Spinner>(R.id.spinnerDeadline)

        //searchView UI
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    // 텍스트가 있을 때: 배경 변경
                    searchView.setBackgroundResource(R.drawable.view_border_pressed)
                    searchIcon.setImageResource(R.drawable.ic_searchicon_custom_pressed)
                } else {
                    // 텍스트 없을 때: 기본 배경
                    searchView.setBackgroundResource(R.drawable.view_border_defualt)
                    searchIcon.setImageResource(R.drawable.ic_searchicon_custom_defualt)
                }
                return true
            }
        })

        // 날짜 선택 순서 추적
        var isFirstDateSelection = true
        var isEditingArrivalDate = false  // 도착일 수정 모드 추적
        var departureYear = 0
        var departureMonth = 0
        var departureDay = 0

        //뷰 외의 영역 터치시 숨기는 함수
        fun View.hideIfTouchedOutside(target: View) {
            this.setOnTouchListener { view, event ->
                if (target.isVisible && event.action == MotionEvent.ACTION_DOWN) {
                    val rect = Rect()
                    target.getGlobalVisibleRect(rect)
                    if (!rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        target.isVisible = false
                    }
                }
                view.performClick()
                false
            }
        }

        // 달력 날짜 선택 설정
        btnDepartureDate.setOnClickListener {
            // 출발일 수정 모드: 도착일 초기화
            btnArivalDate.text = "도착일"
            btnArivalDate.setBackgroundResource(R.drawable.view_border_defualt)
            isFirstDateSelection = true
            isEditingArrivalDate = false
            calendarView.visibility = View.VISIBLE
        }

        // DatePicker 날짜 변경 리스너
        datePicker.setOnDateChangedListener { view, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"

            // 둘 다 설정되어 있는지 확인
            val isBothSet = btnDepartureDate.text != "출발일" && btnArivalDate.text != "도착일"

            if (isBothSet && !isEditingArrivalDate) {
                // 둘 다 설정된 상태에서 출발일 수정 시 출발일부터 재설정
                btnDepartureDate.text = selectedDate
                btnDepartureDate.setTextColor(ContextCompat.getColor(this, R.color.main_color))
                btnDepartureDate.setBackgroundResource(R.drawable.view_border_pressed)
                departureYear = year
                departureMonth = month
                departureDay = dayOfMonth

                // 도착일 초기화
                btnArivalDate.text = "도착일"
                btnArivalDate.setBackgroundResource(R.drawable.view_border_defualt)
                btnArivalDate.setTextColor(ContextCompat.getColor(this, R.color.sub_gray))
                isFirstDateSelection = false
            } else {
                // 첫 번째 선택은 출발일, 두 번째 선택은 도착일
                if (isFirstDateSelection) {
                    btnDepartureDate.text = selectedDate
                    btnDepartureDate.setBackgroundResource(R.drawable.view_border_pressed)
                    btnDepartureDate.setTextColor(ContextCompat.getColor(this, R.color.main_color))
                    departureYear = year
                    departureMonth = month
                    departureDay = dayOfMonth
                    isFirstDateSelection = false
                } else {
                    // 도착일은 출발일보다 늦어야 함
                    val isLaterDate = (year > departureYear) ||
                            (year == departureYear && month > departureMonth) ||
                            (year == departureYear && month == departureMonth && dayOfMonth > departureDay)

                    if (!isLaterDate) {
                        android.widget.Toast.makeText(
                            this,
                            "도착일은 출발일보다 늦어야 합니다!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@setOnDateChangedListener
                    }

                    btnArivalDate.text = selectedDate
                    btnArivalDate.setBackgroundResource(R.drawable.view_border_pressed)
                    btnArivalDate.setTextColor(ContextCompat.getColor(this, R.color.main_color))
                    isEditingArrivalDate = false
                }
            }
        }

        // 도착일 버튼 클릭 리스너
        btnArivalDate.setOnClickListener {
            if (isFirstDateSelection) android.widget.Toast.makeText(
                this,
                "출발일을 먼저 선택해주세요!",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            // 도착일 수정 모드: 출발일은 그대로 유지
            else {
                isFirstDateSelection = false
                isEditingArrivalDate = true
                calendarView.visibility = View.VISIBLE
            }
        }

        // 달력 외 다른 부분 클릭 시 달력 숨기기
        scrollView.hideIfTouchedOutside(calendarView)

        //인원수 드롭다운 메뉴
        val recruitItems = resources.getStringArray(R.array.recruitArray)
        val recruitSpinnerAdapter =
            BoardSpinnerAdapter(this, R.layout.spinner_item, R.id.spinnerText , recruitItems)
        spinnerRecruit.adapter = recruitSpinnerAdapter
        spinnerRecruit.setSelection(spinnerRecruit.adapter.count)

        spinnerRecruit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val textView = view.findViewById<TextView>(R.id.spinnerText)
                when (position) {
                    8 -> {
                        // hint 상태
                        spinnerRecruit.setBackgroundResource(R.drawable.view_border_defualt)
                    }
                    else -> {
                        // 선택된 상태
                        textView?.setTextColor(ContextCompat.getColor(spinnerRecruit.context, R.color.main_color))
                        spinnerRecruit.setBackgroundResource(R.drawable.view_border_pressed)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }

        //모집기간 드롭다운 메뉴
        val deadlineItems = resources.getStringArray(R.array.deadlineArray)
        val deadlineSpinnerAdapter =
            BoardSpinnerAdapter(this, R.layout.spinner_item, R.id.spinnerText, deadlineItems)
        spinnerDeadline.adapter =  deadlineSpinnerAdapter
        spinnerDeadline.setSelection(spinnerDeadline.adapter.count)
        spinnerDeadline.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val textView = view.findViewById<TextView>(R.id.spinnerText)
                when (position) {
                    5 -> {
                        // hint 상태
                        spinnerDeadline.setBackgroundResource(R.drawable.view_border_defualt)
                    }
                    else -> {
                        // 선택된 상태
                        textView?.setTextColor(ContextCompat.getColor(spinnerDeadline.context, R.color.main_color))
                        spinnerDeadline.setBackgroundResource(R.drawable.view_border_pressed)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }
    }
}