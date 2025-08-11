package com.example.ddu_ru_mobile.ui.postSetup

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ddu_ru_mobile.R
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.ddu_ru_mobile.databinding.ActivityPostSetupBinding

import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class PostSetUpActivity: AppCompatActivity() {
    private lateinit var postSetUpBinding: ActivityPostSetupBinding
    private val startMonth = YearMonth.now().minusMonths(100)
    private val endMonth = YearMonth.now().plusMonths(100)
    private val currentMonth = YearMonth.now()
    private val firstDayOfWeek = java.time.DayOfWeek.MONDAY

    private enum class EditTarget { START, END, NONE }
    private var editTarget: EditTarget = EditTarget.NONE
    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null
    private val today: LocalDate = LocalDate.now()

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay
        val textView: TextView = view.findViewById(R.id.calendarDayText)
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textMonth: TextView = view.findViewById(R.id.textMonth)
        val textYear: TextView = view.findViewById(R.id.textYear)
        val btnPrev: ImageButton = view.findViewById(R.id.btnPreviousMonth)
        val btnNext: ImageButton = view.findViewById(R.id.btnNextMonth)
    }

    //영역 밖을 터치시 뷰를 숨기는 함수
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

    // 연도 선택 드롭다운 표시 함수
    private fun showYearDropdown(anchorView: View, currentYearMonth: YearMonth) {
        val dropdownView = LayoutInflater.from(this).inflate(R.layout.dropdown_year_picker, null)
        val yearContainer = dropdownView.findViewById<LinearLayout>(R.id.yearContainer)
        
        // 연도 리스트 생성 (현재 연도부터 +50년)
        val currentYear = currentYearMonth.year
        for (year in currentYear..(currentYear + 50)) {
            val yearItemView = LayoutInflater.from(this).inflate(R.layout.dropdown_item, null)
            val yearText = yearItemView.findViewById<TextView>(R.id.dropdownItemText)
            
            yearText.text = "${year}년"
            
            // 현재 연도 강조 (스피너 스타일과 동일)
            val textColor = if (year == currentYear) {
                ContextCompat.getColor(this, R.color.main_color)
            } else {
                ContextCompat.getColor(this, R.color.sub_gray)
            }
            yearText.setTextColor(textColor)
            
            yearText.setOnClickListener {
                val newYearMonth = YearMonth.of(year, currentYearMonth.monthValue)
                postSetUpBinding.calendarView.scrollToMonth(newYearMonth)
                popupWindow?.dismiss()
            }
            
            yearContainer.addView(yearItemView)
        }
        
        showDropdown(dropdownView, anchorView)
    }
    
    // 월 선택 드롭다운 표시 함수
    private fun showMonthDropdown(anchorView: View, currentYearMonth: YearMonth) {
        val dropdownView = LayoutInflater.from(this).inflate(R.layout.dropdown_month_picker, null)
        val monthContainer = dropdownView.findViewById<LinearLayout>(R.id.monthContainer)
        
        // 월 리스트 생성
        val months = arrayOf("1월", "2월", "3월", "4월", "5월", "6월", 
                           "7월", "8월", "9월", "10월", "11월", "12월")
        
        months.forEachIndexed { index, monthName ->
            val monthItemView = LayoutInflater.from(this).inflate(R.layout.dropdown_item, null)
            val monthText = monthItemView.findViewById<TextView>(R.id.dropdownItemText)
            
            monthText.text = monthName
            
            // 현재 월 강조 (스피너 스타일과 동일)
            val textColor = if (index + 1 == currentYearMonth.monthValue) {
                ContextCompat.getColor(this, R.color.main_color)
            } else {
                ContextCompat.getColor(this, R.color.sub_gray)
            }
            monthText.setTextColor(textColor)
            
            monthText.setOnClickListener {
                val newYearMonth = YearMonth.of(currentYearMonth.year, index + 1)
                postSetUpBinding.calendarView.scrollToMonth(newYearMonth)
                popupWindow?.dismiss()
            }
            
            monthContainer.addView(monthItemView)
        }
        
        showDropdown(dropdownView, anchorView)
    }
    
    private var popupWindow: PopupWindow? = null
    
    // 공통 드롭다운 표시 함수
    private fun showDropdown(dropdownView: View, anchorView: View) {
        popupWindow?.dismiss() // 기존 팝업이 있다면 닫기
        
        popupWindow = PopupWindow(
            dropdownView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        
        // 드롭다운 스타일 설정
        popupWindow?.elevation = 8f
        popupWindow?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dropdown))
        
        // 앵커 뷰 아래에 표시
        popupWindow?.showAsDropDown(anchorView, 0, 8)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postSetUpBinding = ActivityPostSetupBinding.inflate(layoutInflater)
        setContentView(postSetUpBinding.root)

        // ViewBinding으로 뷰 참조

        //searchView UI
        val searchIcon = postSetUpBinding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)

        postSetUpBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    // 텍스트가 있을 때: 배경 변경
                    postSetUpBinding.searchView.setBackgroundResource(R.drawable.bg_border_selected)
                    searchIcon.setImageResource(R.drawable.ic_searchicon_custom_pressed)
                } else {
                    // 텍스트 없을 때: 기본 배경
                    postSetUpBinding.searchView.setBackgroundResource(R.drawable.bg_border_defualt)
                    searchIcon.setImageResource(R.drawable.ic_searchicon_custom_defualt)
                }
                return true
            }
        })

        //달력 구현
        postSetUpBinding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        postSetUpBinding.calendarView.scrollToMonth(currentMonth)


        // 클릭 이벤트
        postSetUpBinding.btnDepartureDate.setOnClickListener {
            editTarget = EditTarget.START
            postSetUpBinding.calendarWrapper.isVisible = true
        }

        postSetUpBinding.btnArrivalDate.setOnClickListener {
            editTarget = EditTarget.END
            postSetUpBinding.calendarWrapper.isVisible = true
        }

        postSetUpBinding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View): MonthViewContainer = MonthViewContainer(view)

            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // 월/연도 라벨
                container.textMonth.text =
                    month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.KOREAN) // 예: 8월
                container.textYear.text = month.yearMonth.year.toString()

                // 월/연도 텍스트 클릭 이벤트 - 각각 다른 드롭다운 표시
                container.textMonth.setOnClickListener {
                    showMonthDropdown(container.textMonth, month.yearMonth)
                }
                container.textYear.setOnClickListener {
                    showYearDropdown(container.textYear, month.yearMonth)
                }

                // 버튼: 이전/다음 달로 스크롤
                container.btnPrev.setOnClickListener {
                    postSetUpBinding.calendarView.smoothScrollToMonth(month.yearMonth.minusMonths(1))
                }
                container.btnNext.setOnClickListener {
                    postSetUpBinding.calendarView.smoothScrollToMonth(month.yearMonth.plusMonths(1))
                }
            }
        }

        postSetUpBinding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                textView.text = day.date.dayOfMonth.toString()

                // 기본 스타일
                if (day.position == DayPosition.MonthDate) {
                    textView.setTextColor(getColor(R.color.title_text_black))
                    textView.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_default_day)
                } else {
                    textView.setTextColor(getColor(R.color.sub_gray))
                    textView.background = null
                }

                // 선택 날짜/범위 처리
                val start = selectedStartDate
                val end = selectedEndDate

                when {
                    start != null && start == day.date -> {
                        // 시작일 (가장 우선순위)
                        textView.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_selected_day)
                        textView.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.white))
                    }
                    end != null && end == day.date -> {
                        // 종료일 (두 번째 우선순위)
                        textView.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_selected_day)
                        textView.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.white))
                    }
                    start != null && end != null && (day.date > start && day.date < end) -> {
                        // 사이 날짜 (세 번째 우선순위)
                        textView.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_selected_middle_day)
                        textView.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.main_color))
                    }
                    day.date == today -> {
                        // 오늘 날짜 (선택된 날짜가 아닌 경우에만)
                        textView.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_border_selected)
                        textView.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.calendar_text_black))
                    }
                    else -> {
                        // 기본 스타일
                        if (day.position == DayPosition.MonthDate) {
                            textView.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.calendar_text_black))
                            textView.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_default_day)
                        } else {
                            textView.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.sub_gray))
                            textView.background = null
                        }
                    }
                }

                container.view.setOnClickListener {
                    val clicked = day.date

                    // 둘 다 선택된 상태에서 다시 아무 날짜나 누르면: 출발일부터 다시 선택 (수정 모드가 아닐 때)
                    if (selectedStartDate != null && selectedEndDate != null && editTarget != EditTarget.END) {
                        selectedStartDate = clicked
                        selectedEndDate = null
                        // 필요하면 즉시 반영
                        val fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                        
                        // 출발일 버튼 텍스트, 색상 및 배경 설정
                        if (selectedStartDate != null) {
                            postSetUpBinding.btnDepartureDate.text = selectedStartDate?.format(fmt)
                            postSetUpBinding.btnDepartureDate.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.main_color))
                            postSetUpBinding.btnDepartureDate.setBackgroundResource(R.drawable.bg_border_selected)
                        } else {
                            postSetUpBinding.btnDepartureDate.text = "출발일 선택"
                            postSetUpBinding.btnDepartureDate.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.sub_gray))
                            postSetUpBinding.btnDepartureDate.setBackgroundResource(R.drawable.bg_border_defualt)
                        }
                        
                        // 도착일 버튼 텍스트, 색상 및 배경 설정
                        if (selectedEndDate != null) {
                            postSetUpBinding.btnArrivalDate.text = selectedEndDate?.format(fmt)
                            postSetUpBinding.btnArrivalDate.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.main_color))
                            postSetUpBinding.btnArrivalDate.setBackgroundResource(R.drawable.bg_border_selected)
                        } else {
                            postSetUpBinding.btnArrivalDate.text = "도착일 선택"
                            postSetUpBinding.btnArrivalDate.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.sub_gray))
                            postSetUpBinding.btnArrivalDate.setBackgroundResource(R.drawable.bg_border_defualt)
                        }
                        postSetUpBinding.calendarView.notifyCalendarChanged()
                        return@setOnClickListener
                    }

                    // ✅ 2) 일반 플로우 (버튼으로 지정한 수정 모드 우선)
                    when (editTarget) {
                        EditTarget.START -> {
                            selectedStartDate = clicked
                            if (selectedEndDate != null && selectedEndDate!! < selectedStartDate) {
                                selectedEndDate = null
                            }
                            editTarget = EditTarget.NONE
                        }

                        EditTarget.END -> {
                            if (selectedStartDate == null) {
                                Toast.makeText(this@PostSetUpActivity, "출발일을 먼저 선택하세요", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            if (clicked < selectedStartDate) {
                                Toast.makeText(this@PostSetUpActivity, "도착일은 출발일 이후여야 합니다", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            if (clicked <= today) {
                                Toast.makeText(this@PostSetUpActivity, "도착일은 오늘 이후여야 합니다", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            selectedEndDate = clicked
                            editTarget = EditTarget.NONE
                        }

                        EditTarget.NONE -> {
                            // 첫 클릭=출발, 두 번째=도착
                            if (selectedStartDate == null) {
                                selectedStartDate = clicked
                                selectedEndDate = null
                            } else {
                                if (clicked < selectedStartDate) {
                                    Toast.makeText(this@PostSetUpActivity, "도착일은 출발일 이후여야 합니다", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }
                                if (clicked <= today) {
                                    Toast.makeText(this@PostSetUpActivity, "도착일은 오늘 이후여야 합니다", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }
                                selectedEndDate = clicked
                            }
                        }
                    }

                    // 버튼 텍스트 갱신
                    val fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                    
                    // 출발일 버튼 텍스트, 색상 및 배경 설정
                    if (selectedStartDate != null) {
                        postSetUpBinding.btnDepartureDate.text = selectedStartDate?.format(fmt)
                        postSetUpBinding.btnDepartureDate.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.main_color))
                        postSetUpBinding.btnDepartureDate.setBackgroundResource(R.drawable.bg_border_selected)
                    } else {
                        postSetUpBinding.btnDepartureDate.text = "출발일 선택"
                        postSetUpBinding.btnDepartureDate.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.sub_gray))
                        postSetUpBinding.btnDepartureDate.setBackgroundResource(R.drawable.bg_border_defualt)
                    }
                    
                    // 도착일 버튼 텍스트, 색상 및 배경 설정
                    if (selectedEndDate != null) {
                        postSetUpBinding.btnArrivalDate.text = selectedEndDate?.format(fmt)
                        postSetUpBinding.btnArrivalDate.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.main_color))
                        postSetUpBinding.btnArrivalDate.setBackgroundResource(R.drawable.bg_border_selected)
                    } else {
                        postSetUpBinding.btnArrivalDate.text = "도착일 선택"
                        postSetUpBinding.btnArrivalDate.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.sub_gray))
                        postSetUpBinding.btnArrivalDate.setBackgroundResource(R.drawable.bg_border_defualt)
                    }

                    // 캘린더 갱신
                    postSetUpBinding.calendarView.notifyCalendarChanged()
                }
            }
        }



        //달력이 아닌 부분을 선택시 달력 사라짐
        postSetUpBinding.scrollView.hideIfTouchedOutside(postSetUpBinding.calendarWrapper)

        //인원수 드롭다운 메뉴
        val recruitItems = resources.getStringArray(R.array.recruitArray)
        val recruitSpinnerAdapter =
            BoardSpinnerAdapter(this, R.layout.spinner_item, R.id.spinnerText , recruitItems)
        postSetUpBinding.spinnerRecruit.adapter = recruitSpinnerAdapter
        postSetUpBinding.spinnerRecruit.setSelection(postSetUpBinding.spinnerRecruit.adapter.count)

        postSetUpBinding.spinnerRecruit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                        postSetUpBinding.spinnerRecruit.setBackgroundResource(R.drawable.bg_border_defualt)
                    }
                    else -> {
                        // 선택된 상태
                        textView?.setTextColor(ContextCompat.getColor(postSetUpBinding.spinnerRecruit.context, R.color.main_color))
                        postSetUpBinding.spinnerRecruit.setBackgroundResource(R.drawable.bg_border_selected)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }

         //모집기간 바텀 팝업
         fun modalWithRoundCorner() {
             val modal = ModalBottomSheet()
             modal.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
             modal.apply {
                 onDatePicked = { date ->
                     val pretty = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                     postSetUpBinding.btnRecruitDeadline.text = pretty
                     postSetUpBinding.btnRecruitDeadline.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.main_color))
                     postSetUpBinding.btnRecruitDeadline.setBackgroundResource(R.drawable.bg_border_selected)

                 }
             }.show(supportFragmentManager, ModalBottomSheet.TAG)
         }

        postSetUpBinding.btnRecruitDeadline.setOnClickListener{
            modalWithRoundCorner()
        }


        // 성별 선택
        postSetUpBinding.btnMale.setOnClickListener{

        }
        postSetUpBinding.btnFemale.setOnClickListener{

        }
        postSetUpBinding.btnGenderAny.setOnClickListener{}
    }
}