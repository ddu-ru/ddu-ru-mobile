package com.example.ddu_ru_mobile.ui.postSetup

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.ddu_ru_mobile.R
import com.example.ddu_ru_mobile.databinding.ActivityPostSetupBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class PostSetUpActivity : AppCompatActivity() {

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

    enum class Gender { MALE, FEMALE, ANY }
    private var selectedGender: Gender? = null

    // ---------- ViewContainers ----------
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

    // ---------- 유틸 ----------
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

    private var popupWindow: PopupWindow? = null

    private fun showDropdown(dropdownView: View, anchorView: View) {
        popupWindow?.dismiss()
        popupWindow = PopupWindow(
            dropdownView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 8f
            setBackgroundDrawable(ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_dropdown))
            showAsDropDown(anchorView, 0, 8)
        }
    }

    private fun showYearDropdown(anchorView: View, currentYearMonth: YearMonth) {
        val dropdownView = LayoutInflater.from(this).inflate(R.layout.dropdown_year_picker, null)
        val yearContainer = dropdownView.findViewById<LinearLayout>(R.id.yearContainer)
        val currentYear = currentYearMonth.year
        for (year in currentYear..(currentYear + 50)) {
            val item = LayoutInflater.from(this).inflate(R.layout.dropdown_item, yearContainer, false)
            val tv = item.findViewById<TextView>(R.id.dropdownItemText)
            tv.text = "${year}년"
            tv.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (year == currentYear) R.color.main_color else R.color.sub_gray
                )
            )
            tv.setOnClickListener {
                val newYm = YearMonth.of(year, currentYearMonth.monthValue)
                postSetUpBinding.calendarView.scrollToMonth(newYm)
                popupWindow?.dismiss()
            }
            yearContainer.addView(item)
        }
        showDropdown(dropdownView, anchorView)
    }

    private fun showMonthDropdown(anchorView: View, currentYearMonth: YearMonth) {
        val dropdownView = LayoutInflater.from(this).inflate(R.layout.dropdown_month_picker, null)
        val monthContainer = dropdownView.findViewById<LinearLayout>(R.id.monthContainer)
        val months = arrayOf("1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월")
        months.forEachIndexed { index, label ->
            val item = LayoutInflater.from(this).inflate(R.layout.dropdown_item, monthContainer, false)
            val tv = item.findViewById<TextView>(R.id.dropdownItemText)
            tv.text = label
            tv.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (index + 1 == currentYearMonth.monthValue) R.color.main_color else R.color.sub_gray
                )
            )
            tv.setOnClickListener {
                val newYm = YearMonth.of(currentYearMonth.year, index + 1)
                postSetUpBinding.calendarView.scrollToMonth(newYm)
                popupWindow?.dismiss()
            }
            monthContainer.addView(item)
        }
        showDropdown(dropdownView, anchorView)
    }

    // ---------- 버튼 스타일 ----------
    private fun Button.asSelected() {
        setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.main_color))
        setBackgroundResource(R.drawable.bg_border_selected)
    }
    private fun Button.asDefault() {
        setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.sub_gray))
        setBackgroundResource(R.drawable.bg_border_defualt) // 파일명 그대로 사용
    }
    private fun Button.ifSelectedThen(isSel: Boolean) {
        if (isSel) asSelected() else asDefault()
    }

    private fun applyGenderSelection(newGender: Gender) {
        selectedGender = if (selectedGender == newGender) null else newGender
        with(postSetUpBinding) {
            btnMale.ifSelectedThen(newGender == Gender.MALE && selectedGender == Gender.MALE)
            btnFemale.ifSelectedThen(newGender == Gender.FEMALE && selectedGender == Gender.FEMALE)
            btnGenderAny.ifSelectedThen(newGender == Gender.ANY && selectedGender == Gender.ANY)
        }
        updateNextEnabled()
    }

    // ---------- “다음” 활성화 토글 ----------
    private fun updateNextEnabled() {
        val placeOk = !postSetUpBinding.searchView.query.isNullOrBlank()
        val datesOk = (selectedStartDate != null && selectedEndDate != null)
        val genderOk = (selectedGender != null)
        val recruitOk = (postSetUpBinding.spinnerRecruit.selectedItemPosition != 8) // 힌트 인덱스 제외
        val deadlineOk = postSetUpBinding.btnRecruitDeadline.text?.toString()?.contains(".") == true

        val allOk = placeOk && datesOk && genderOk && recruitOk && deadlineOk

        postSetUpBinding.btnNext.isEnabled = allOk
        if (allOk) {
            postSetUpBinding.btnNext.setTextColor(ContextCompat.getColor(this, R.color.main_color))
            postSetUpBinding.btnNext.setBackgroundResource(R.drawable.bg_border_selected)
        } else {
            postSetUpBinding.btnNext.setTextColor(ContextCompat.getColor(this, R.color.sub_gray))
            postSetUpBinding.btnNext.setBackgroundResource(R.drawable.bg_border_defualt)
        }
    }

    // ---------- onCreate ----------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postSetUpBinding = ActivityPostSetupBinding.inflate(layoutInflater)
        setContentView(postSetUpBinding.root)

        // SearchView
        val searchIcon = postSetUpBinding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        postSetUpBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    postSetUpBinding.searchView.setBackgroundResource(R.drawable.bg_border_selected)
                    searchIcon.setImageResource(R.drawable.ic_searchicon_custom_pressed)
                } else {
                    postSetUpBinding.searchView.setBackgroundResource(R.drawable.bg_border_defualt)
                    searchIcon.setImageResource(R.drawable.ic_searchicon_custom_defualt)
                }
                updateNextEnabled()
                return true
            }
        })

        // Calendar
        postSetUpBinding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        postSetUpBinding.calendarView.scrollToMonth(currentMonth)

        postSetUpBinding.btnDepartureDate.setOnClickListener {
            editTarget = EditTarget.START
            postSetUpBinding.calendarWrapper.isVisible = true
        }
        postSetUpBinding.btnArrivalDate.setOnClickListener {
            editTarget = EditTarget.END
            postSetUpBinding.calendarWrapper.isVisible = true
        }

        postSetUpBinding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    container.textMonth.text =
                        month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.KOREAN)
                    container.textYear.text = month.yearMonth.year.toString()

                    container.textMonth.setOnClickListener {
                        showMonthDropdown(container.textMonth, month.yearMonth)
                    }
                    container.textYear.setOnClickListener {
                        showYearDropdown(container.textYear, month.yearMonth)
                    }
                    container.btnPrev.setOnClickListener {
                        postSetUpBinding.calendarView.smoothScrollToMonth(month.yearMonth.minusMonths(1))
                    }
                    container.btnNext.setOnClickListener {
                        postSetUpBinding.calendarView.smoothScrollToMonth(month.yearMonth.plusMonths(1))
                    }
                }
            }

        postSetUpBinding.calendarView.dayBinder =
            object : MonthDayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)

                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    container.day = day
                    val tv = container.textView
                    tv.text = day.date.dayOfMonth.toString()

                    // 기본 스타일
                    if (day.position == DayPosition.MonthDate) {
                        tv.setTextColor(getColor(R.color.title_text_black))
                        tv.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_default_day)
                    } else {
                        tv.setTextColor(getColor(R.color.sub_gray))
                        tv.background = null
                    }

                    // 선택 범위 스타일
                    val start = selectedStartDate
                    val end = selectedEndDate
                    when {
                        start != null && start == day.date -> {
                            tv.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_selected_day)
                            tv.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.white))
                        }
                        end != null && end == day.date -> {
                            tv.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_selected_day)
                            tv.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.white))
                        }
                        start != null && end != null && (day.date > start && day.date < end) -> {
                            tv.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_selected_middle_day)
                            tv.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.main_color))
                        }
                        day.date == today -> {
                            tv.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_border_selected)
                            tv.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.calendar_text_black))
                        }
                        else -> {
                            if (day.position == DayPosition.MonthDate) {
                                tv.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.calendar_text_black))
                                tv.background = ContextCompat.getDrawable(this@PostSetUpActivity, R.drawable.bg_default_day)
                            } else {
                                tv.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.sub_gray))
                                tv.background = null
                            }
                        }
                    }

                    // 클릭 로직
                    container.view.setOnClickListener {
                        val clicked = day.date

                        // 둘 다 선택된 상태에서 아무 날짜 탭 → 출발일부터 다시 선택
                        if (selectedStartDate != null && selectedEndDate != null && editTarget != EditTarget.END) {
                            selectedStartDate = clicked
                            selectedEndDate = null
                            val fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                            postSetUpBinding.btnDepartureDate.text = selectedStartDate?.format(fmt) ?: "출발일 선택"
                            postSetUpBinding.btnDepartureDate.asSelected()
                            postSetUpBinding.btnArrivalDate.text = "도착일 선택"
                            postSetUpBinding.btnArrivalDate.asDefault()
                            postSetUpBinding.calendarView.notifyCalendarChanged()
                            updateNextEnabled()
                            return@setOnClickListener
                        }

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
                                    selectedStartDate = clicked

                                    val fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                                    postSetUpBinding.btnDepartureDate.text = selectedStartDate?.format(fmt)
                                    postSetUpBinding.btnDepartureDate.asSelected()
                                    if (selectedEndDate != null) {
                                        postSetUpBinding.btnArrivalDate.text = selectedEndDate?.format(fmt)
                                        postSetUpBinding.btnArrivalDate.asSelected()
                                    } else {
                                        postSetUpBinding.btnArrivalDate.text = "도착일 선택"
                                        postSetUpBinding.btnArrivalDate.asDefault()
                                    }

                                    editTarget = EditTarget.NONE
                                    postSetUpBinding.calendarView.notifyCalendarChanged()
                                    updateNextEnabled()
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
                                if (selectedStartDate == null) {
                                    selectedStartDate = clicked
                                    selectedEndDate = null
                                } else {
                                    if (clicked < selectedStartDate) {
                                        // 🔁 더 이른 날짜 → 출발일만 교체
                                        selectedStartDate = clicked
                                        // 도착일은 아직 없음(그대로 null)
                                    } else {
                                        if (clicked <= today) {
                                            Toast.makeText(this@PostSetUpActivity, "도착일은 오늘 이후여야 합니다", Toast.LENGTH_SHORT).show()
                                            return@setOnClickListener
                                        }
                                        selectedEndDate = clicked
                                    }
                                }
                            }
                        }

                        // 버튼 텍스트/스타일 갱신
                        val fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd")
                        if (selectedStartDate != null) {
                            postSetUpBinding.btnDepartureDate.text = selectedStartDate?.format(fmt)
                            postSetUpBinding.btnDepartureDate.asSelected()
                        } else {
                            postSetUpBinding.btnDepartureDate.text = "출발일 선택"
                            postSetUpBinding.btnDepartureDate.asDefault()
                        }
                        if (selectedEndDate != null) {
                            postSetUpBinding.btnArrivalDate.text = selectedEndDate?.format(fmt)
                            postSetUpBinding.btnArrivalDate.asSelected()
                        } else {
                            postSetUpBinding.btnArrivalDate.text = "도착일 선택"
                            postSetUpBinding.btnArrivalDate.asDefault()
                        }

                        postSetUpBinding.calendarView.notifyCalendarChanged()
                        updateNextEnabled()
                    }
                }
            }

        // 캘린더 외 영역 터치 시 닫기
        postSetUpBinding.scrollView.hideIfTouchedOutside(postSetUpBinding.calendarWrapper)

        // 인원수 스피너
        val recruitItems = resources.getStringArray(R.array.recruitArray)
        val recruitAdapter =
            BoardSpinnerAdapter(this, R.layout.spinner_item, R.id.spinnerText, recruitItems)
        postSetUpBinding.spinnerRecruit.adapter = recruitAdapter
        postSetUpBinding.spinnerRecruit.setSelection(postSetUpBinding.spinnerRecruit.adapter.count)
        postSetUpBinding.spinnerRecruit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val tv = view.findViewById<TextView>(R.id.spinnerText)
                    if (position == 8) {
                        postSetUpBinding.spinnerRecruit.setBackgroundResource(R.drawable.bg_border_defualt)
                    } else {
                        tv?.setTextColor(ContextCompat.getColor(this@PostSetUpActivity, R.color.main_color))
                        postSetUpBinding.spinnerRecruit.setBackgroundResource(R.drawable.bg_border_selected)
                    }
                    updateNextEnabled()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    updateNextEnabled()
                }
            }

        // 모집기간 바텀시트
        fun modalWithRoundCorner() {
            val modal = ModalBottomSheet()
            modal.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)
            modal.apply {
                onDatePicked = { date ->
                    val pretty = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    postSetUpBinding.btnRecruitDeadline.text = pretty
                    postSetUpBinding.btnRecruitDeadline.asSelected()
                    updateNextEnabled()
                }
            }.show(supportFragmentManager, ModalBottomSheet.TAG)
        }
        postSetUpBinding.btnRecruitDeadline.setOnClickListener { modalWithRoundCorner() }

        // 성별 선택
        postSetUpBinding.btnMale.setOnClickListener { applyGenderSelection(Gender.MALE) }
        postSetUpBinding.btnFemale.setOnClickListener { applyGenderSelection(Gender.FEMALE) }
        postSetUpBinding.btnGenderAny.setOnClickListener { applyGenderSelection(Gender.ANY) }

        // 초기 상태 반영
        updateNextEnabled()
    }
}